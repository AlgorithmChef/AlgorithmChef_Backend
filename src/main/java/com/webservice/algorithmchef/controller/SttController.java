package com.webservice.algorithmchef.controller;

import com.webservice.algorithmchef.service.SpeechService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SttController {

    private final SpeechService speechService;

    public SttController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @PostMapping("/stt")
    public ResponseEntity<Map<String, Object>> stt(@RequestParam("audio") MultipartFile audioFile) {
        try {
            String text = speechService.stt(audioFile);

            Map<String, Object> body = new HashMap<>();
            body.put("text", text.isEmpty() ? "stt fail" : text);

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> body = new HashMap<>();
            body.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }
}
