package com.cloudops.FileItemWriter.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "student") //name is provided because it will start from that student tag. In our students.xml the root tag from where it will start reading is <student>
public class StudentXml {
	private Long id;
	private String first_Name;
	private String lastName;
	private String email;
}
