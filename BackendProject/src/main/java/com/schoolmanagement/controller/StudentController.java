package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> deleteStudent(@PathVariable Long id){
        return studentService.deleteStudent(id);
    }

    //getStudentByName
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/getStudentByName")
    public List<StudentResponse> getStudentByName(@RequestParam(name = "name") String studentName){
        return studentService.getStudentByName(studentName);
    }

    //getStudentById
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/getStudentById")
    public Student getStudentById(@RequestParam(name = "id") Long id){ // donen deger pojo olmaz.
        return studentService.getStudentByIdForResponse(id);
        // burda sikinti var, pojo donduren method yazmamiz lazim. sonra yazicaz.

    }
    //getAllStudentWithPage
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<StudentResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
            ){
        return studentService.search(page,size,sort,type);
    }
    //chooseLessonProgramById
    @PreAuthorize("hasAnyAuthority('STUDENT')") // ogrenci kendi dersini kendisi secebilir. Sadece kendisi yapabilir.
    //bu bir create islemidir. Yeni bir ders seciliyor ve ogrenci datasi uzerinde degisiklikler yapicaz biz. Get de yazabilirdik aslinda. Ama post yazmamiz daha saglikli.
    @PostMapping("/chooseLesson")
    public ResponseMessage<StudentResponse> chooseLesson(HttpServletRequest request,
                                                         @RequestBody @Valid ChooseLessonProgramWithId chooseLessonProgramRequest){
        //burdaki yontem degisebilir. PathVariable ile de alabilirdik. burda yapiyi zorladik biraz. gittik farkli class uzerinden requesti aldik.
        //bu kisim service de yazilirsa daha iyi olur.

        // simdi ogrenci kendine ders eklicek ama hangi ogrenci. Bunu username uzerinden aliyoruz. Ayrica bir tane de ders idsi almamiz lazim
        // ki dersi getirebilelim.. Bunu da yeni request classimiz araciligiyla aliyoruz.
        String username = (String) request.getAttribute("username"); // bu requesti username olarak aldik.
        return studentService.chooseLesson(username,chooseLessonProgramRequest);
    }

    //getAllStudentByAdvisor : bir advisorun tum ogrencilerini getir. advisor ekndi yapacak bu islemi.
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllByAdvisorId")
    public List<StudentResponse> getAllByAdvisorId(HttpServletRequest request){
        String username = (String) request.getAttribute("username");
        return studentService.getAllStudentByTeacher_Username(username);
    }
}
