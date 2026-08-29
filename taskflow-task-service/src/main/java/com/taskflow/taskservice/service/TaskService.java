package com.taskflow.taskservice.service;

import com.taskflow.taskservice.client.UserServiceClient;
import com.taskflow.taskservice.dto.TaskRequest;
import com.taskflow.taskservice.exception.TaskNotFoundException;
import com.taskflow.taskservice.model.Task;
import com.taskflow.taskservice.model.TaskStatus;
import com.taskflow.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;

    public TaskService(TaskRepository taskRepository, UserServiceClient userServiceClient) {
        this.taskRepository = taskRepository;
        this.userServiceClient = userServiceClient; 
    }

    public Task createTask(TaskRequest request) {
        Task task = new Task(request.getTitle(), request.getDescription(), request.getStatus(), request.getDueDate());
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Task updateTask(String id, TaskRequest request) {
        Task existing = getTaskById(id);
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setStatus(request.getStatus());
        existing.setDueDate(request.getDueDate());
        existing.setUpdatedAt(Instant.now());
        return taskRepository.save(existing);
    }

    public void deleteTask(String id) { 
        Task existing = getTaskById(id);
        taskRepository.delete(existing);
    }

    public Task assignUser(String id,String username)
    {
        Task task = getTaskById(id);
        
        boolean exists=userServiceClient.userExists(username);
        if(!exists){
            throw new IllegalArgumentException(
                "Cannot assign: user '"+ username +"' doesn't exist, or User Service is unavailable");
        }
        
        task.setAssignedTo(username);
        task.setUpdatedAt(Instant.now());
        return taskRepository.save(task);

            
        
    }
}
