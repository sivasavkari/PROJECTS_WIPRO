package com.example.demo.service;

import com.example.demo.entity.Student;

public interface StudentService {

    void saveStudent(Student student);

    Student getStudent(int id);
}
