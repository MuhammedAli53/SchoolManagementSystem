package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.AdminService;
import com.schoolmanagement.service.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("dean")
public class DeanController {
    private final DeanService deanService;



    //save methodu yazicaz.Dean role de olan bir dean kaydedicez.
    @PostMapping("/save") //http://localhost:8080/dean/save
    @PreAuthorize("hasAuthority('ADMIN')") // hasAuthority bu sekilde de kullanabiliriz. Tek role yazabiliriz icine.
    public ResponseMessage<DeanResponse> save (@Valid @RequestBody DeanRequest deanRequest){
        return deanService.save(deanRequest); // burda donen deger ResponseMessage. biz burda service classina attik bu isi. Save methodu responsemessage tipinde dondurucek.
    }

    // ************** updateById *************************
    @PutMapping("/update/{userId}")//http://localhost:8080/dean/update/1
    //normalde putmapping tehlikelidir. Girilmeyen datalar icin null dondurur. Ancak biz zaten dto olarak calistigimiz icin bir problem yok.
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> update (@Valid @RequestBody DeanRequest deanRequest, @PathVariable Long userId){
      /*
      //PreAuthorize kontrol. Eger anlik kullanici ile login olan data eslesiyorsa yetkilendirmeyi bu sekilde ya.
        private boolean checkPreAuthorize(Authentication authentication, DeanRequest request){
            Dean dean = (Dean) authentication.getPrincipal();
            if (dean.getUsername().equalsIgnoreCase(request.getUsername())){
                @PreAuthorize("hasAnyAuthority('ADMIN,MANAGER')")

            }else  @PreAuthorize("hasAuthority('ADMIN')")
        }*/

        return deanService.update(deanRequest,userId); // burda ana method icindeki parametlerin hepsini gondeririz genelde ama farkliliklar olabilir.
    }
    // ******************* Delete() *******************
    @DeleteMapping("/delete/{userId}")//http://localhost:8080/dean/delete/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<?> delete(@PathVariable Long userId){ // oylesine ? koyduk, normalde DeanReequest donduruyorduk
        return deanService.deleteDean(userId);
    }

    //*********************** getById ******************************
    @GetMapping("/getManagerById/{userId}")//http://localhost:8080/dean/getManagerById/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> getDeanById(@PathVariable Long userId){
        return deanService.getDeanById(userId);
    }

    // ************** getAll ()***********************
    @GetMapping("/getAll")//http://localhost:8080/dean/getAll
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<DeanResponse> getAll(){
        return deanService.getAllDean();
    }

    // ********************* Search() *********************
    @GetMapping("/search")//http://localhost:8080/dean/search
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<DeanResponse> search (
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){

        return deanService.search(page,size,sort,type);
    }


}
