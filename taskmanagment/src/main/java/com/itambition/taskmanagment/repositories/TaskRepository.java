package com.itambition.taskmanagment.repositories;
import com.itambition.taskmanagment.models.Task;
import com.itambition.taskmanagment.models.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByUser(User user);
    // You can add custom query methods here if needed

    Page<Task> findByDescriptionContainingIgnoreCase(String descriptionPart, Pageable pageable);
}
