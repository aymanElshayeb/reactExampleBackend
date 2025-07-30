package com.itambition.taskmanagment.rests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itambition.taskmanagment.models.Task;
import com.itambition.taskmanagment.models.User;
import com.itambition.taskmanagment.services.TasksServices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(TasksController.class)
public class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TasksServices tasksServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;
    private User testUser;

    @Before
    public void setUp() {
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDeadline(LocalDateTime.now().plusDays(1));
        testTask.setUser(testUser);
    }

    // Test adding a task successfully
    @Test
    public void testAddTask_Success() throws Exception {
        when(tasksServices.addTask(any(Task.class))).thenReturn(testTask);
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.description").value(testTask.getDescription()));
    }
    
    // Test adding a task with error
    @Test
    public void testAddTask_Error() throws Exception {
        when(tasksServices.addTask(any(Task.class))).thenThrow(new RuntimeException("Error"));
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isInternalServerError());
    }
    
    // Test updating a task successfully
    @Test
    public void testUpdateTask_Success() throws Exception {
        when(tasksServices.updateTask(any(Task.class))).thenReturn(testTask);
        
        mockMvc.perform(put("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.name").value(testTask.getName()));
    }
    
    // Test updating a task not found
    @Test
    public void testUpdateTask_NotFound() throws Exception {
        when(tasksServices.updateTask(any(Task.class))).thenThrow(new IllegalArgumentException("Task not found"));
        
        mockMvc.perform(put("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isNotFound());
    }
    
    // Test updating task by ID successfully
    @Test
    public void testUpdateTaskById_Success() throws Exception {
        when(tasksServices.updateTask(any(Task.class))).thenReturn(testTask);
        
        mockMvc.perform(put("/api/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()));
    }
    
    // Test removing multiple tasks successfully
    @Test
    public void testRemoveTasks_Success() throws Exception {
        List<Long> taskIds = Arrays.asList(1L, 2L);
        doNothing().when(tasksServices).removeTasks(taskIds);
        
        mockMvc.perform(delete("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskIds)))
                .andExpect(status().isNoContent());
    }
    
    // Test removing multiple tasks with empty list
    @Test
    public void testRemoveTasks_EmptyList() throws Exception {
        List<Long> taskIds = Collections.emptyList();
        
        mockMvc.perform(delete("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskIds)))
                .andExpect(status().isBadRequest());
    }
    
    // Test removing a single task successfully
    @Test
    public void testRemoveTask_Success() throws Exception {
        doNothing().when(tasksServices).removeTasks(anyList());
        
        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
        
        verify(tasksServices).removeTasks(List.of(1L));
    }
    
    // Test searching tasks by description with results
    @Test
    public void testSearchByDescription_WithResults() throws Exception {
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(testTask));
        when(tasksServices.searchByDescription(eq("Test"), any(Pageable.class))).thenReturn(taskPage);
        
        mockMvc.perform(get("/api/tasks/search")
                .param("description", "Test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(testTask.getId()))
                .andExpect(jsonPath("$.content[0].name").value(testTask.getName()));
    }
}
   

