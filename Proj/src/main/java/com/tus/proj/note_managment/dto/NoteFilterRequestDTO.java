package com.tus.proj.note_managment.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class NoteFilterRequestDTO {
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private String tag;


}
