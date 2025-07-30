package com.itambition.taskmanagment.services;
import com.itambition.taskmanagment.models.Task;
import com.itambition.taskmanagment.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;





@Service
public class TasksServices {
    
    @Autowired
    private TaskRepository taskRepository;
    
    
    
    /**
     * Add a new task
     */
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }
    
    /**
     * Update an existing task
     */
    public Task updateTask(Task task) {
        if (task.getId() == null || !taskRepository.existsById(task.getId())) {
            throw new IllegalArgumentException("Task not found with ID: " + task.getId());
        }
        return taskRepository.save(task);
    }
    
    /**
     * Remove multiple tasks by their IDs
     */
    @Transactional
    public void removeTasks(List<Long> taskIds) {
        taskRepository.deleteInBatch(taskIds.stream()
                .map(id -> {
                    Task task = new Task();
                    task.setId(id);
                    return task;
                })
                .collect(Collectors.toList()));
    }
    
    /**
     * Search tasks by description with pagination
     */
    public Page<Task> searchByDescription(String descriptionPart, Pageable pageable) {
        return taskRepository.findByDescriptionContainingIgnoreCase(descriptionPart, pageable);
    }
}
