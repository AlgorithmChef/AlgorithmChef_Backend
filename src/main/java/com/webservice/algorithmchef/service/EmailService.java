package com.webservice.algorithmchef.service;


import java.security.SecureRandom;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;
    
	private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();
	
	public String makeTemporaryPassword() {
        final int passwordLength = 12;
        StringBuilder sb = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            int rndIdx = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndIdx);

            sb.append(rndChar);
        }
        return sb.toString();
	}
	
	public void sendTemporaryPasswordEmail(String toEmail, String tempPassword) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[알고리듬 셰프] 임시 비밀번호 안내");

            String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<h2>[밥도둑] 임시 비밀번호 안내</h2>"
                + "<p>요청하신 임시 비밀번호입니다. 로그인 후 반드시 비밀번호를 변경해주세요.</p>"
                + "<div style='background-color: #f4f4f4; padding: 15px; border-radius: 5px; font-size: 18px; font-weight: bold; text-align: center;'>"
                + tempPassword
                + "</div>"
                + "</body>"
                + "</html>";
            
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("임시 비밀번호 이메일 발송 성공: {}", toEmail);

        } catch (MessagingException e) {
            log.error("임시 비밀번호 이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
	
}
