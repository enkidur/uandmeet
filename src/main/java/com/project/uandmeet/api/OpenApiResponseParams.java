package com.project.uandmeet.api;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OpenApiResponseParams {
    private String city;
    private List<String> gu = new ArrayList<>();

    public OpenApiResponseParams(String city, List<String> gu)
    {
        this.city = city;
        this.gu = gu;
    }
}