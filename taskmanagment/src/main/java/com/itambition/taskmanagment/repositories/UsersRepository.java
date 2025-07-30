package com.itambition.taskmanagment.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itambition.taskmanagment.models.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
   Optional<User> findByUserName(String userName);
}