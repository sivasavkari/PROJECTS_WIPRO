package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepo;

    @Override
    public Task addTask(Task task) {

        // If user gives an ID and it already exists -> conflict (409)
        if (task.getId() != null && taskRepo.existsById(task.getId())) {
            throw new IllegalArgumentException("Task with same ID already exists");
        }

        return taskRepo.save(task);
    }

    @Override
    public Task updateTask(Task task) {

        // Task must exist
        if (!taskRepo.existsById(task.getId())) {
            return null; // Controller will return 404
        }

        return taskRepo.save(task);
    }

    @Override
    public void deleteTask(Integer id) {

        if (!taskRepo.existsById(id)) {
            throw new IllegalArgumentException("Task does not exist");
        }

        taskRepo.deleteById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    @Override
    public List<Task> searchByStatus(String status) {
        return taskRepo.findByStatusIgnoreCase(status);
    }

    @Override
    public List<Task> filterByPriority(String priority) {
        return taskRepo.findByPriorityIgnoreCase(priority);
    }

    @Override
    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();

        return taskRepo.findAll().stream()
                .filter(t -> t.getDueDate() != null
                        && t.getDueDate().isBefore(today)
                        && !t.getStatus().equalsIgnoreCase("completed"))
                .toList();
    }
}
