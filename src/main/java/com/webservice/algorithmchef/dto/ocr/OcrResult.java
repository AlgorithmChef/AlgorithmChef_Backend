package com.webservice.algorithmchef.dto.ocr;

import java.time.LocalDateTime;

public record OcrResult(
        int id,
        String description,
        String candidateName,
        LocalDateTime expirationDate,
        int quantity
) {}
