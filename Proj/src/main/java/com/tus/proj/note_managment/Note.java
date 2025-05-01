package com.tus.proj.note_managment;

import java.time.LocalDate;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tus.proj.user_managment.User;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;
	@Column(columnDefinition = "TEXT")
	private String content;
	private String tag;
	
	@Enumerated(EnumType.STRING)
	private Access access;

	@Enumerated(EnumType.STRING)
	private Priority priority;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate deadline;


	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private User user;

	public Note() {
	}

	public Note(String title, String content, Priority priority, LocalDate deadline, User user, String tag, Access access) {
		this.title = title;
		this.content = content;
		this.priority = priority;
		this.deadline = deadline;
		this.user = user;
		this.tag = tag;
		this.setAccess(access);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate localDate) {
		this.deadline = localDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}
}
