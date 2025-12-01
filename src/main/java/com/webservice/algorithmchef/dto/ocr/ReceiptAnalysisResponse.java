package com.webservice.algorithmchef.dto.ocr;

import java.time.LocalDateTime;
import java.util.List;
public record ReceiptAnalysisResponse(
		LocalDateTime purchaseDate,
		List<OcrResult> items) {}
