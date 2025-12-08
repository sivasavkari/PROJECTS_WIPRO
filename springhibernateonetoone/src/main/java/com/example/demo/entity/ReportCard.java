package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ReportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int mathsMarks;

    private int scienceMarks;

    private int englishMarks;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;
}

