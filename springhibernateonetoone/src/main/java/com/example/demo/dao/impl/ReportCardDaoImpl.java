package com.example.demo.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.ReportCardDao;
import com.example.demo.entity.ReportCard;

import jakarta.persistence.EntityManager;

@Repository
@Transactional
public class ReportCardDaoImpl implements ReportCardDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void saveReportCard(ReportCard reportCard) {
        entityManager.persist(reportCard);
    }

    @Override
    public ReportCard getReportCardById(int id) {
        return entityManager.find(ReportCard.class, id);
    }
}
