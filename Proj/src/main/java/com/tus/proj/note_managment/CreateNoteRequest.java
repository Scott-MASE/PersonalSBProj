package com.tus.proj.note_managment;

import java.time.LocalDateTime;

import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRole;

public class CreateNoteRequest {
	private String title;
	private String content;
	private String tag;
	private Priority priority;
	private LocalDateTime deadline;
	private int userId;

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

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	public int getUser() {
		return userId;
	}

	public void setUser(int userId) {
		this.userId = userId;
	}

}
