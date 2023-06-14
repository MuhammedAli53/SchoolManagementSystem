package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studentInfo")
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    //******************** save *************************
    @PreAuthorize("hasAnyAuthority('TEACHER')") // sinav notlarimizi kim girebilir?
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> save(HttpServletRequest httpServletRequest, StudentInfoRequestWithoutTeacherId studentInfoRequestWithoutTeacherId){
        // attribute olarak alicaz datayi. teacher username alicaz. bu nedenle teacher id almiyrouz dtoda.
        String username = (String) httpServletRequest.getAttribute("username"); // bu kismi service de de yazabiliriz. Bu sekilde olursa donen data olarak username degil de
        //httpServletRequest gondericez.
        return studentInfoService.save(username, studentInfoRequestWithoutTeacherId);
    }


}
