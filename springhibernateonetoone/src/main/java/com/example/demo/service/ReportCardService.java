package com.example.demo.service;

import com.example.demo.entity.ReportCard;

public interface ReportCardService {

    void saveReportCard(ReportCard reportCard);

    ReportCard getReportCard(int id);
}

