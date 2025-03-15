package com.tus.proj.note_managment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tus.proj.user_managment.User;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    
    List<Note> findByPriority(Priority priority);

    List<Note> findByUserId(Long userId);
    
    List<Note> findByTag(String tag);
    
    List<Note> findByUser(User user);
    
    void deleteByUserId(Long userId);
    
    @Query("SELECT DISTINCT n.tag FROM Note n WHERE n.tag IS NOT NULL AND n.user.id = :userId")
    List<String> findDistinctTagsByUserId(@Param("userId") Long userId);
    
    



}