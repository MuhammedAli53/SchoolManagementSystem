package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.TeacherResponse;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("teachers")
@RequiredArgsConstructor
@RestController
public class TeacherController {

    private final TeacherService teacherService;

    //*********************** save() **************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @PostMapping("/save") // http://localhost:8080/teachers/save
    public ResponseMessage<TeacherResponse> save(@Valid @RequestBody TeacherRequest teacher){
        return teacherService.save(teacher);

    }
}
