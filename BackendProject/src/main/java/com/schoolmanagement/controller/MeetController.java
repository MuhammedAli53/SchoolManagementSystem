package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.MeetRequestWithoutId;
import com.schoolmanagement.payload.request.UpdateMeetRequest;
import com.schoolmanagement.payload.response.MeetResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meet")
public class MeetController {

    private final MeetService meetService;

    // ******************* save *******************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save") // bu tur datayi pathVariable ile alamam. Endpointte teacherlerden birisi id numarasinmi degistirir endpointte hoop kendisine meet atar. Pegasus sitesinde
    // yaptigimiz gibi.
    public ResponseMessage<MeetResponse> save(HttpServletRequest httpServletRequest, @Valid @RequestBody MeetRequestWithoutId meetRequest){
        String username = (String) httpServletRequest.getAttribute("username");
        return meetService.save(username,meetRequest);
    }

    //**************** getAll ******************
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAll")
    public List<MeetResponse> getAll(){
        return meetService.getAll();
    }
    //*********** getMeetById ****************
    //spesifik bir meet nesnesini getiricez.
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getMeetById/{meetId}")
    public ResponseMessage<MeetResponse> getMeetById(@PathVariable Long meetId){
        return meetService.getMeetById(meetId);
    }
    // bir rehber ogretmenin tum meetlerini getiricem. Page yapisinda.
    //********* getAllMeetByAdvisorAsPage ***************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllMeetByAdvisorAsPage")
    //teacher username almamiz lazim. Almazsak getAll gibi calisir. Spesifik olarak bir advisora ait olan meetler gelsin.
    // Bu nedenle advisorun username sini alicaz.
    public ResponseEntity<Page<MeetResponse>> getAllMeetByAdvisorAsPage(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        String username = (String) httpServletRequest.getAttribute("username");
        Pageable pageable = PageRequest.of(page,size, Sort.by("date").descending());
        Page<MeetResponse> meet = meetService.getAllMeetByAdvisorTeacherAsPage(username,pageable);
        return ResponseEntity.ok(meet);
    }
    // ************* getAllMeetAdvisorTeacherAsList **************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllMeetAdvisorTeacherAsList")
    public ResponseEntity<List<MeetResponse>> getAllMeetAdvisorTeacherAsList(HttpServletRequest servletRequest){
        String username = (String) servletRequest.getAttribute("username");
        List<MeetResponse> meet = meetService.getAllMeetByAdvisorTeacherAsList(username);
        return ResponseEntity.ok(meet);
    }
    // Not :  delete() ***********************************************************************
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN' )")
    @DeleteMapping("/delete/{meetId}")
    public ResponseMessage<?> delete(@PathVariable Long meetId){
        return meetService.delete(meetId);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN' )")
    @PutMapping("/update/{meetId}")
    public ResponseMessage<MeetResponse> update(@RequestBody @Valid UpdateMeetRequest meetRequest,
                                                @PathVariable Long meetId){
        return meetService.update(meetRequest,meetId);
    }
    // Not :  getAllMeetByStudent() **********************************************************
    @PreAuthorize("hasAnyAuthority('STUDENT')") // spesifik olarak bir ogrencinin username bilgisi ile tum meetlerini getiricem.
    @GetMapping("/getAllMeetByStudent")
    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest){
        String username = (String) httpServletRequest.getAttribute("username");
        return meetService.getAllMeetByStudentByUsername(username);
    }
    // Not :  getAllWithPage() **********************************************************
    @PreAuthorize("hasAnyAuthority( 'ADMIN')")
    @GetMapping("/search")
    public Page<MeetResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return meetService.search(page,size);
    }
}
