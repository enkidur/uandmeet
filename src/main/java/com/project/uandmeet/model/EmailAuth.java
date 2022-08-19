package com.project.uandmeet.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailAuth {
    private String email;
    private String authNumber;
    private int isAuth;
}
