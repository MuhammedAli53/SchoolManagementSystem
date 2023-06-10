package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.TeacherResponse;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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



    //********************** getAll ****************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/getAll")
    public List<TeacherResponse> getAllTeacher(){
        return teacherService.getAllTeacher();
    }




    //********************** updateTeacherById ************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @PutMapping("/update/{userId}")
    public ResponseMessage<TeacherResponse> updateTeacher(@RequestBody @Valid TeacherRequest teacherRequest, @PathVariable Long userId){
        return teacherService.updateTeacher(teacherRequest,userId);
    }



    //************************* getTeacherByName ************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/getTeacherByName")
    public List<TeacherResponse> getTeacherByName(@RequestParam(name = "name") String teacherName){
        // name ile bir endpoint gelicek, bunu teachername degiskeni ile setle.
        return teacherService.getTeacherByName(teacherName);

    }


    //*************************** deleteTeacher *****************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> deleteTeacher(@PathVariable Long id){
        return teacherService.deleteTeacher(id);
    }




    //************************** getTeacherById ******************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @DeleteMapping("/getSavedTeacherById/{id}")
    public ResponseMessage<TeacherResponse> getSavedTeacherById(@PathVariable Long id){
        return teacherService.getSavedTeacherById(id);
    }

    //*************************** getAllWithPage *******************************
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<TeacherResponse> getSavedTeacherById(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return teacherService.search(page,size,sort,type);
    }
    //************** addLessonProgramToTeachersLessonsProgram ***************
    //ogretmenin lesson programina yeni bir lessonprogram ekliyoruz. Bir teacherin birden fazla lesson programi oalbilir.
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @PostMapping("/chooseLesson")
    public ResponseMessage<TeacherResponse> chooseLesson(@RequestBody @Valid ChooseLessonTeacherRequest chooseLessonRequest){
        //ogretmene ders eklicez. Lesson ve teacherid ler lazim bize. Cunkku bu methodu teacher yapamiyor. Admin, mudur yada mudur yardimcisi
        //yapyor, bu nedenle teacher id ve lessonid lazim. Bu nedenle bu yapiya uygun bir request olusturmamiz lazim.
        return teacherService.chooseLesson(chooseLessonRequest);
    }
}
