package com.webservice.algorithmchef.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SpeechService {

    private final SpeechClient speechClient;

    public SpeechService() throws IOException {
        this.speechClient = SpeechClient.create();
    }

    public String stt(MultipartFile audioFile) throws IOException {
        byte[] data = audioFile.getBytes();
        ByteString audioBytes = ByteString.copyFrom(data);

        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("ko-KR")
                .build();

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioBytes)
                .build();

        try {
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            if (results.isEmpty()) {
                log.warn("STT 결과 없음 (Google API 응답이 비어있음)");
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                sb.append(alternative.getTranscript());
            }

            return sb.toString();

        } catch (Exception e) {
            log.error("Google Cloud Speech API 호출 실패", e);
            throw new IOException("Speech-to-Text fail", e);
        }
    }
}
