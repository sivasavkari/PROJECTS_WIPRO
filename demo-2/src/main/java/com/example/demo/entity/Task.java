package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String assignedTo;

    @Pattern(
        regexp = "low|medium|high",
        flags = Pattern.Flag.CASE_INSENSITIVE,
        message = "Priority must be one of: low, medium, high"
    )
    private String priority;

    @Pattern(
        regexp = "pending|in-progress|completed",
        flags = Pattern.Flag.CASE_INSENSITIVE,
        message = "Status must be one of: pending, in-progress, completed"
    )
    private String status;

    private LocalDate dueDate;
}
