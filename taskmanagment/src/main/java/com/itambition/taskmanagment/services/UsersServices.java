package com.itambition.taskmanagment.services;

import com.itambition.taskmanagment.models.User;
import com.itambition.taskmanagment.models.Task;
import com.itambition.taskmanagment.repositories.TaskRepository;
import com.itambition.taskmanagment.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsersServices {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Add a new user
    public User addUser(User user) {
        return usersRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    // Search user by id
    public Optional<User> getUserById(Long id) {
        return usersRepository.findById(id);
    }

    // Search user by username (assuming such a method exists in UsersRepository)
    public Optional<User> getUserByUserName(String userName) {
        return usersRepository.findByUserName(userName);
    }

    // Get tasks of a user
    public List<Task> getTasksOfUser(Long userId) {

        return usersRepository.findById(userId).map(user -> taskRepository.findAllByUser(user)).orElse(null);
    }
}
