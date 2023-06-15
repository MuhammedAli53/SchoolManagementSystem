package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studentInfo")
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    //******************** save *************************
    @PreAuthorize("hasAnyAuthority('TEACHER')") // sinav notlarimizi kim girebilir?
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> save(HttpServletRequest httpServletRequest,
                                                     @Valid @RequestBody StudentInfoRequestWithoutTeacherId studentInfoRequestWithoutTeacherId){
        //farkli senaryolarla insa edebilirdik. Parametre olarak studentid alabiliriz. Student koyabiliriz. Lesson id alabiliriz. Ancak teacher id almamiz lazim
        // cunku teacher islem yapcak. Bunun icin httpServletRequest uzerinden datayi alaibliriz. attribute methoduna ogretmenin unique bir degerini koyariz ve olay biter.
        //isi on tarafa atmis oluyoruz. O react ile datayi alacak.
        // attribute olarak alicaz datayi. teacher username alicaz. bu nedenle teacher id almiyrouz dtoda.
        String username = (String) httpServletRequest.getAttribute("username"); // bu kismi service de de yazabiliriz. Bu sekilde olursa donen data olarak username degil de
        //httpServletRequest gondericez.
        return studentInfoService.save(username, studentInfoRequestWithoutTeacherId);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @DeleteMapping("/delete/{studentInfoId}")
    public ResponseMessage<?> delete(@PathVariable Long id){
        return studentInfoService.deleteStudentInfo(id);
    }


}
