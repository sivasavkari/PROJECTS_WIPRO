package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id") // foreign key
    private User user;

    private String priority;
    private String status;
    private LocalDate dueDate;
}

