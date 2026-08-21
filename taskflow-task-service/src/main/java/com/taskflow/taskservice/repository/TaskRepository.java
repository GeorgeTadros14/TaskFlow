package com.taskflow.taskservice.repository;

import com.taskflow.taskservice.model.Task;
import com.taskflow.taskservice.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByStatus(TaskStatus status);
}
