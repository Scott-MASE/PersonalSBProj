package com.tus.proj.note_managment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Priority;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;

public class CreateNoteRequestDTO {
	private String title;
	private String content;
	private String tag;
	private Priority priority;
	private LocalDate deadline;
	private Access access;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}



}
