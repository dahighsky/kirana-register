package com.example.kirana_register.dto.response;

import com.example.kirana_register.model.Report;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {
    private Report report;
}
