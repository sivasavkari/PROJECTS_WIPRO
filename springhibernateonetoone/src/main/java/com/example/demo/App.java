package com.example.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.demo.config.AppConfig;
import com.example.demo.entity.ReportCard;
import com.example.demo.entity.Student;
import com.example.demo.service.ReportCardService;
import com.example.demo.service.StudentService;

public class App {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        StudentService studentService = context.getBean(StudentService.class);
        ReportCardService reportCardService = context.getBean(ReportCardService.class);

        // ------------------------------
        // Create Student
        // ------------------------------
        Student student = new Student();
        student.setName("Lakshmi");
        student.setDepartment("CSE");

        // ------------------------------
        // Create ReportCard
        // ------------------------------
        ReportCard rc = new ReportCard();
        rc.setMathsMarks(95);
        rc.setScienceMarks(88);
        rc.setEnglishMarks(90);

        //  Connect both
        rc.setStudent(student);
        student.setReportCard(rc);

        // Save (Hibernate will save both because of cascade)
        studentService.saveStudent(student);

        System.out.println("Student & ReportCard saved successfully!");

        // Fetch back
        Student savedStudent = studentService.getStudent(student.getId());
        System.out.println("Fetched Student: " + savedStudent.getName());
        System.out.println("Report Card English Marks: " +
                savedStudent.getReportCard().getEnglishMarks());

        context.close();
    }
}
