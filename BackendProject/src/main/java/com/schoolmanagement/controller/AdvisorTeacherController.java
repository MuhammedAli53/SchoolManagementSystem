package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.payload.response.AdvisorTeacherResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.AdvisorTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/advisorTeacher")
public class AdvisorTeacherController {

    private final AdvisorTeacherService advisorTeacherService;

    // ************************ deleteAdvisorTeacher *********************

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> deleteAdvisorTeacher(@PathVariable Long id){
        return advisorTeacherService.deleteAdvisorTeacher(id);
    }


    // ************************* getAllAdvisorTeacher **********************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/getAllAdvisorTeacher")
    public List<AdvisorTeacherResponse> getAllAdvisorTeacher(){
        return advisorTeacherService.getAllAdvisorTeacher();
    }


    // ******************* getAllAdvisorTeacherWithPage *******************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<AdvisorTeacherResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return advisorTeacherService.search(page,size,sort,type);
    }
}
