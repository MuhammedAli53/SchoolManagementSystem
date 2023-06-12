package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("students")
@RequiredArgsConstructor
@RestController
public class StudentController {

    private final StudentService studentService;

    //*************************** save *************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @PostMapping("/save") // http://localhost:8080/students/save
    public ResponseMessage<StudentResponse> save(@Valid @RequestBody StudentRequest studentRequest){
        return studentService.save(studentRequest);
    }
    // ******************** changeActiveStatus *************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/changeStatus") // http://localhost:8080/students/changeStatus
    public ResponseMessage<?> changeStatus(@RequestParam Long id, @RequestParam boolean status){
        return studentService.changeStatus(id,status);
    }

    //********************** getAllStudent *********************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/getAll") // http://localhost:8080/students/getAll
    public List<StudentResponse> getAllStudent(){
        return studentService.getAllStudent();
    }

    //******************** updateStudent **********************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @PutMapping("/update/{id}")
    public ResponseMessage<StudentResponse> updateStudent( @PathVariable Long userId, @Valid @RequestParam StudentRequest studentRequest){
        return studentService.updateStudent(userId,studentRequest);
    }

    //deleteStudent

    //getStudentByName

    //getStudentById

    //getAllStudentWithPage

    //chooseLessonProgramById

    //getAllStudentByAdvisor : bir advisorun tum ogrencilerini getir.
}
