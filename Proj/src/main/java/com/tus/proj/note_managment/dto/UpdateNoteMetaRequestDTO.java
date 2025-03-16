package com.tus.proj.note_managment.dto;

import java.time.LocalDate;

import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Priority;

public class UpdateNoteMetaRequestDTO {
    private String title;
    private String content;
    private Priority priority;
    private LocalDate deadline;
    private String tag;
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

