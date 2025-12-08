package com.example.demo.dao;

import com.example.demo.entity.Student;

public interface StudentDao {
    void saveStudent(Student student);
    Student getStudentById(int id);
}
