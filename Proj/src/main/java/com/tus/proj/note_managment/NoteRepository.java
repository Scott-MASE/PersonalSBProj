package com.tus.proj.note_managment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tus.proj.user_managment.User;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    
    List<Note> findByPriority(Priority priority);

    List<Note> findByUser(User user);
}