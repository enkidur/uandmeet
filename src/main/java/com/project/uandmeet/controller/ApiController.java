package com.project.uandmeet.controller;



import com.project.uandmeet.api.OpenApiManager;
import com.project.uandmeet.api.OpenApiResponseParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiController {
    private final OpenApiManager openApiManager;

    @PostMapping("/allow_info/basic")
    public List<OpenApiResponseParams> fetch() throws UnsupportedEncodingException, ParseException {
        return openApiManager.fetch();

    }

    @GetMapping("/allow_info/dataRequest")
    public List<OpenApiResponseParams> dataRequest() {
        return openApiManager.dataRequest();
    }
}

