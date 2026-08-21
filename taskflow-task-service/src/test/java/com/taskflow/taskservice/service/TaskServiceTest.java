package com.taskflow.taskservice.service;

import com.taskflow.taskservice.dto.TaskRequest;
import com.taskflow.taskservice.exception.TaskNotFoundException;
import com.taskflow.taskservice.model.Task;
import com.taskflow.taskservice.model.TaskStatus;
import com.taskflow.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task existingTask;

    @BeforeEach
    void setUp() {
        existingTask = new Task("Write tests", "Cover the service layer", TaskStatus.TODO, LocalDate.now().plusDays(3));
        existingTask.setId("abc123");
    }

    @Test
    void createTask_savesAndReturnsTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Write tests");
        request.setDescription("Cover the service layer");
        request.setStatus(TaskStatus.TODO);
        request.setDueDate(LocalDate.now().plusDays(3));

        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        Task result = taskService.createTask(request);

        assertThat(result.getId()).isEqualTo("abc123");
        assertThat(result.getTitle()).isEqualTo("Write tests");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_whenFound_returnsTask() {
        when(taskRepository.findById("abc123")).thenReturn(Optional.of(existingTask));

        Task result = taskService.getTaskById("abc123");

        assertThat(result).isEqualTo(existingTask);
    }

    @Test
    void getTaskById_whenNotFound_throwsException() {
        when(taskRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById("missing"))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("missing");
    }

    @Test
    void getAllTasks_returnsListFromRepository() {
        when(taskRepository.findAll()).thenReturn(List.of(existingTask));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).hasSize(1).contains(existingTask);
    }

    @Test
    void updateTask_whenFound_updatesFieldsAndSaves() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setDueDate(LocalDate.now().plusDays(7));

        when(taskRepository.findById("abc123")).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask("abc123", request);

        assertThat(result.getTitle()).isEqualTo("Updated title");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void deleteTask_whenFound_deletesTask() {
        when(taskRepository.findById("abc123")).thenReturn(Optional.of(existingTask));

        taskService.deleteTask("abc123");

        verify(taskRepository, times(1)).delete(existingTask);
    }

    @Test
    void deleteTask_whenNotFound_throwsException() {
        when(taskRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask("missing"))
                .isInstanceOf(TaskNotFoundException.class);

        verify(taskRepository, never()).delete(any(Task.class));
    }
}
