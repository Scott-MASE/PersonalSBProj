package com.tus.proj.note_managment.dto;

import java.time.LocalDate;


import com.tus.proj.note_managment.Access;
import com.tus.proj.note_managment.Priority;


import lombok.Data;

@Data
public class CreateNoteRequestDTO {
    private String title;
    private String content;
    private String tag;
    private Priority priority;
    private LocalDate deadline;
    private Access access;
    private boolean pinned;


}
