package com.tus.proj.note_managment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Priority;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;

import lombok.Data;

@Data
public class CreateNoteRequestDTO {
	private String title;
	private String content;
	private String tag;
	private Priority priority;
	private LocalDate deadline;
	private Access access;





}
