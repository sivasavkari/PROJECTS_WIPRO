package com.example.demo.dao;

import com.example.demo.entity.ReportCard;

public interface ReportCardDao {
    void saveReportCard(ReportCard reportCard);
    ReportCard getReportCardById(int id);
}

