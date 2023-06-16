package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studentInfo")
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    //******************** save *************************
    @PreAuthorize("hasAnyAuthority('TEACHER')") // sinav notlarimizi kim girebilir?
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> save(HttpServletRequest httpServletRequest,
                                                     @Valid @RequestBody StudentInfoRequestWithoutTeacherId studentInfoRequestWithoutTeacherId) {
        //farkli senaryolarla insa edebilirdik. Parametre olarak studentid alabiliriz. Student koyabiliriz. Lesson id alabiliriz. Ancak teacher id almamiz lazim
        // cunku teacher islem yapcak. Bunun icin httpServletRequest uzerinden datayi alaibliriz. attribute methoduna ogretmenin unique bir degerini koyariz ve olay biter.
        //isi on tarafa atmis oluyoruz. O react ile datayi alacak.
        // attribute olarak alicaz datayi. teacher username alicaz. bu nedenle teacher id almiyrouz dtoda.
        String username = (String) httpServletRequest.getAttribute("username"); // bu kismi service de de yazabiliriz. Bu sekilde olursa donen data olarak username degil de
        //httpServletRequest gondericez.
        return studentInfoService.save(username, studentInfoRequestWithoutTeacherId);
    }

    // ***************** delete *********************
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @DeleteMapping("/delete/{studentInfoId}")
    public ResponseMessage<?> delete(@PathVariable Long id) {
        return studentInfoService.deleteStudentInfo(id);
    }
    // **************** update ***************

    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @PutMapping("/update/{studentInfoId}") // zoru secerek putmapping kullandik. patchmapping daha kolay.
    public ResponseMessage<StudentInfoResponse> update(@RequestBody @Valid UpdateStudentInfoRequest studentInfoRequest, @PathVariable Long studentInfoId) {
        return studentInfoService.update(studentInfoRequest, studentInfoId);
    }

    // ************** getAllForAdmin *************
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAllForAdmin")
    public ResponseEntity<Page<StudentInfoResponse>> getAll(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()); // pageable olusturma islemi service de yazilirsa bestpractice.
        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllForAdmin(pageable);
        return new ResponseEntity<>(studentInfoResponse, HttpStatus.OK);
    }

    // *************** getAllForStudent **************
    //ogretmen kendi ogrenci bilgilerini almak istedigi zaman bu method calisicak. GetAll methodunu spesifik kullanicilar arasinda farkli farkli calistiricaz.
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllForTeacher")
    public ResponseEntity<Page<StudentInfoResponse>> getAllForTeacher(
            HttpServletRequest httpServletRequest, // bu datayi aliyoruz. nedeni ise login olan kullanciyi almamiz lazim.
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String username = (String) httpServletRequest.getAttribute("username"); // bu username bilgisini kim tetiklicek bize? react ile yapicaz. Frontend yapicak yani.
        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllForTeacher(pageable, username);
        return ResponseEntity.ok(studentInfoResponse);
    }

    //*************** getAllForStudent *********************
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllByStudent")
    public ResponseEntity<Page<StudentInfoResponse>> getAllByStudent(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size

    ){
        // Pageable obje olusturma islemini Service katinda yazilmasi best-practice
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String username = (String) httpServletRequest.getAttribute("username");
        Page<StudentInfoResponse> studentInfoResponse = studentInfoService.getAllStudentInfoByStudent(username,pageable);
        return ResponseEntity.ok(studentInfoResponse);
    }
    // ********************** getStudentInfoByStudentId **************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/getByStudentId/{studentId}")
    public ResponseEntity<List<StudentInfoResponse>> getStudentId(@PathVariable Long studentId){

        List<StudentInfoResponse> studentInfoResponse = studentInfoService.getStudentInfoByStudentId(studentId);
        return ResponseEntity.ok(studentInfoResponse);
    }
    // Not: getStudentInfoById()*******************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER','TEACHER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<StudentInfoResponse> get(@PathVariable Long id){

        StudentInfoResponse studentInfoResponse = studentInfoService.findStudentInfoById(id);
        return ResponseEntity.ok(studentInfoResponse);
    }
    // *********************** getAllWithPage *********************
     @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'ASSISTANTMANAGER')")
    @GetMapping("/search")
    public Page<StudentInfoResponse> search (
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
     ){
        return studentInfoService.search(page, size, sort, type);
     }


}