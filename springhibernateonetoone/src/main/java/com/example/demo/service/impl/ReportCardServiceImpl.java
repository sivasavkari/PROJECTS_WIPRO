package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReportCardDao;
import com.example.demo.entity.ReportCard;
import com.example.demo.service.ReportCardService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReportCardServiceImpl implements ReportCardService {

    @Autowired
    private ReportCardDao reportCardDao;

    @Override
    public void saveReportCard(ReportCard reportCard) {
        reportCardDao.saveReportCard(reportCard);
    }

    @Override
    public ReportCard getReportCard(int id) {
        return reportCardDao.getReportCard(id);
    }
}
