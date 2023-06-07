package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.payload.response.EducationTermResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//yaz donemi guz donemi gibi.
@RestController
@RequestMapping("educationTerms")
@RequiredArgsConstructor
public class EducationTermController {

    private final EducationTermService educationTermService;

    // ************************** save() **************************

    @PostMapping("/save") // http://localhost:8080/educationTerms/save
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<EducationTermResponse> save(@Valid @RequestBody EducationTermRequest educationTermRequest) {
        return educationTermService.save(educationTermRequest);
    }

    //****************************** getById **************************************

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER', 'TEACHER')") // student eklenebilir.
    @GetMapping("/{id}") // http://localhost:8080/educationTerms/1
    public EducationTermResponse get(@PathVariable Long id) {
        return educationTermService.get(id);
    }

    // ******************************** getAll *************************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER', 'TEACHER')")
    @GetMapping("/getAll") // http://localhost:8080/educationTerms/getAll
    public List<EducationTermResponse> getAll() {
        return educationTermService.getAll();
    }

    // Not :  getAllWithPage() ******************************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/search") // http://localhost:8080/educationTerms/search?page=0&size=10&sort=startDate&type=desc
    public Page<EducationTermResponse> getAllWithPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "startDate") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return educationTermService.getAllWithPage(page,size,sort,type);
    }


    // ********************** delete() ***************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/delete/{id}") // http://localhost:8080/educationTerms/delete/1
    public ResponseMessage<?> delete(@PathVariable Long id){
        return educationTermService.delete(id);
    }


    // **************************** updateById() **********************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PutMapping("/update/{id}") // http://localhost:8080/educationTerms/update/1
    public ResponseMessage<EducationTermResponse> update(@Valid @RequestBody EducationTermRequest educationTermRequest, @PathVariable Long id){
        return educationTermService.update(id,educationTermRequest);
    }
}