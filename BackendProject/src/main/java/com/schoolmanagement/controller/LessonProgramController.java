package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.payload.response.LessonProgramResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lessonPrograms")
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;


    // ****************** save() *********************
    @PostMapping("/save")// http://localhost:8080/lessonPrograms/save
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    public ResponseMessage<LessonProgramResponse> save(@Valid @RequestBody LessonProgramRequest lessonProgramRequest){
        return lessonProgramService.save(lessonProgramRequest);
    }

    // ******************** getAll() *****************************
    @GetMapping("/getAll")// http://localhost:8080/lessonPrograms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER', 'TEACHER', 'STUDENT')")
    public List<LessonProgramResponse> getAll(){
        return lessonProgramService.getAllLessonProgram();
    }

    // Not :  getById() ************************************************************************

    @GetMapping("/getById/{id}") //http://localhost:8080/lessonPrograms/getById/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public LessonProgramResponse getById(@PathVariable Long id) {
        return lessonProgramService.getByLessonProgramId(id);
    }
    // Not :  getAllLessonProgramUnassigned() **************************************************
    // atamasi yapilmamis ders programinin ogretmeni.
    @GetMapping("/getAllUnassigned") //http://localhost:8080/lessonPrograms/getAllUnassigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned() {
        return lessonProgramService.getAllLessonProgramUnassigned();
    }

    // Not :  getAllLessonProgramAssigned() **************************************************
    //ogretmeni atanmis ders programini getir.
    @GetMapping("/getAllAssigned") //http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramService.getAllLessonProgramAssigned();
    }



    // Not :  Delete() *************************************************************************
    @DeleteMapping("/delete/{id}") //http://localhost:8080/lessonPrograms/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public ResponseMessage delete (@PathVariable Long id) {
        return lessonProgramService.deleteLessonProgram(id);
    }



    // Not :  getLessonProgramByTeacher() ******************************************************
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN','MANAGER','ASSISTANTMANAGER')")
    @GetMapping("/getAllLessonProgramByTeacher")  //http://localhost:8080/lessonPrograms/getAllLessonProgramByTeacher
    public Set<LessonProgramResponse> getAllLessonProgramByTeacherId(HttpServletRequest httpServletRequest) {
        //bize bir teacher gelecek. teacher datasini requestten gelen bir fieldi cekerek de alabiliriz.
        // bu zamana kadar bu bilgiyi endpointten aldik. Simdi ise HttpServletRequestten alicaz.
        //onun attbibute methodu var. onun icerisine almak istedigimiz degisken adini yazariz. olay biter.
        //HttpServletRequest bize gelen requesti gorme sansi verir. Bu requeste bak, username fieldi ile girilen datayi cek ve String bir deger icine ata dedik.
        String username = (String) httpServletRequest.getAttribute("username"); // cektigin dataya cast islemi istiyor. Emin misin diye.
        //currently login olan kullaniciya ulas da diyebiliriz. getPrincipal(). Ancak admin giris yapti diyelim, ahmet adli kullanici isteniyor bizden, bu bilgiyi
        //alamazdık. Bu nedenle getPrincipal methodu calismaz.
        return lessonProgramService.getLessonProgramByTeacher(username);
    }
    //************************** getAllLessonProgramByStudent() ****************************
    // spesifik bir studentin id ile ders programini getirmek.
    @GetMapping("/getAllLessonProgramByStudent")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER', 'TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest httpServletRequest){

        String username = (String) httpServletRequest.getAttribute("username");
        return lessonProgramService.getLessonProgramByStudent(username);
    }

    //**************************** getAllWithPage **************************************
    @GetMapping("/search")// http://localhost:8080/lessonPrograms/search
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER', 'TEACHER', 'STUDENT')")
    public Page<LessonProgramResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return lessonProgramService.search(page,size,sort,type);
    }

}
