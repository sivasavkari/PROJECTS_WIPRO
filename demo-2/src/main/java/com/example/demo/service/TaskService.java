package com.example.demo.service;

import java.util.List;
import com.example.demo.entity.Task;

public interface TaskService {

    Task addTask(Task task);
    Task updateTask(Task task);
    void deleteTask(Integer id);
    List<Task> getAllTasks();
    List<Task> searchByStatus(String status);
    List<Task> filterByPriority(String priority);
    List<Task> getOverdueTasks();
}
