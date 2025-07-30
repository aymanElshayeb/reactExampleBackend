package com.itambition.taskmanagment.rests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itambition.taskmanagment.models.Task;
import com.itambition.taskmanagment.models.User;
import com.itambition.taskmanagment.services.UsersServices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersController.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersServices usersServices;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Task testTask;

    @Before
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole("ROLE_USER");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDeadline(LocalDateTime.now().plusDays(1));
        testTask.setUser(testUser);
    }

    @Test
    public void testAddUser_Success() throws Exception {
        when(usersServices.addUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(usersServices, times(1)).addUser(any(User.class));
    }

    @Test
    public void testAddUser_InternalServerError() throws Exception {
        when(usersServices.addUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isInternalServerError());
    }

    // Other test methods - change them all from void to public void
    // and continue with the same pattern for all remaining tests
    
    @Test
    public void testGetAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(usersServices.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userName").value("testuser"));

        verify(usersServices, times(1)).getAllUsers();
    }
    
    @Test
    public void testGetAllUsers_NoContent() throws Exception {
        when(usersServices.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNoContent());

        verify(usersServices, times(1)).getAllUsers();
    }

    @Test
    public void testGetAllUsers_InternalServerError() throws Exception {
        when(usersServices.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        when(usersServices.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(usersServices, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        when(usersServices.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(usersServices, times(1)).getUserById(999L);
    }

    @Test
    public void testGetUserById_InternalServerError() throws Exception {
        when(usersServices.getUserById(1L)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetUserByUserName_Success() throws Exception {
        when(usersServices.getUserByUserName("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(usersServices, times(1)).getUserByUserName("testuser");
    }

    @Test
    public void testGetUserByUserName_NotFound() throws Exception {
        when(usersServices.getUserByUserName("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/username/nonexistent"))
                .andExpect(status().isNotFound());

        verify(usersServices, times(1)).getUserByUserName("nonexistent");
    }

    @Test
    public void testGetUserByUserName_InternalServerError() throws Exception {
        when(usersServices.getUserByUserName("testuser")).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetTasksOfUser_Success() throws Exception {
        List<Task> tasks = Arrays.asList(testTask);
        when(usersServices.getTasksOfUser(1L)).thenReturn(tasks);

        mockMvc.perform(get("/api/users/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Task"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));

        verify(usersServices, times(1)).getTasksOfUser(1L);
    }

    @Test
    public void testGetTasksOfUser_NoContent() throws Exception {
        when(usersServices.getTasksOfUser(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/1/tasks"))
                .andExpect(status().isNoContent());

        verify(usersServices, times(1)).getTasksOfUser(1L);
    }

    @Test
    public void testGetTasksOfUser_NotFound() throws Exception {
        when(usersServices.getTasksOfUser(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999/tasks"))
                .andExpect(status().isNotFound());

        verify(usersServices, times(1)).getTasksOfUser(999L);
    }

    @Test
    public void testGetTasksOfUser_InternalServerError() throws Exception {
        when(usersServices.getTasksOfUser(1L)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/users/1/tasks"))
                .andExpect(status().isInternalServerError());
    }
}
