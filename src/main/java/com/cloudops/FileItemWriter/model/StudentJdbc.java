package com.cloudops.FileItemWriter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StudentJdbc {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;

    public Long getid() {return id;}
    public void setid(Long id) {this.id = id;}

    public String getfirst_name() {return first_name;}
    public void setfirst_name(String first_name) {this.first_name = first_name;}

    public String getlast_name() {return last_name;}
    public void setlast_name(String last_name) {this.last_name = last_name;}

    public String getemail() {return email;}
    public void setemail(String email) {this.email = email;}
}
