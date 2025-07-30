package com.itambition.taskmanagment.rests;

import com.itambition.taskmanagment.models.Task;
import com.itambition.taskmanagment.services.TasksServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TasksController {

    @Autowired
    private TasksServices tasksServices;

    /**
     * Add a new task
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        try {
            Task savedTask = tasksServices.addTask(task);
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing task
     * PUT /api/tasks
     */
    @PutMapping
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        try {
            Task updatedTask = tasksServices.updateTask(task);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing task by ID
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskById(@PathVariable("id") Long id, @RequestBody Task task) {
        try {
            task.setId(id);
            Task updatedTask = tasksServices.updateTask(task);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove multiple tasks by their IDs
     * DELETE /api/tasks
     */
    @DeleteMapping
    public ResponseEntity<HttpStatus> removeTasks(@RequestBody List<Long> taskIds) {
        try {
            if (taskIds == null || taskIds.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            tasksServices.removeTasks(taskIds);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Remove a single task by ID
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> removeTask(@PathVariable("id") Long id) {
        try {
            List<Long> taskIds = List.of(id);
            tasksServices.removeTasks(taskIds);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search tasks by description with pagination
     * GET /api/tasks/search?description={description}&page={page}&size={size}
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Task>> searchByDescription(
            @RequestParam("description") String description,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Task> tasks = tasksServices.searchByDescription(description, pageable);
            
            if (!tasks.hasContent()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
