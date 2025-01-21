package com.cloudops.FileItemWriter.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentCsv {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
