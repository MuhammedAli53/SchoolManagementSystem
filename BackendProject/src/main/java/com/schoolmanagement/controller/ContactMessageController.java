package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("contactMessages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;



    /*
        {
            "name" : "Mirac",
            "email" : "xxx@yyy.com",
            "subject" : "RestFull API",
            "message" : "RestFull API"
        }
     */ // Ornek JSON


    // Not: **************************** save() **********************************************
    @PostMapping("/save") // http://localhost:8080/contactMessages/save

    //response entity de mesaj ve status code gonderilirdi. Biz burda hazir bu yapiyi kullanmicaz. Kendimiz bir ResponseEntity turunde
    //bir class olusturucaz ve olusturdugumuz bu classi projemizin her katmaninda kullanicaz. Bunun yarari ne? Generic yapida olacak.
    //farkli farkli classlardan turetebilicez.bunu payload package icinde yaptik.
    public ResponseMessage<ContactMessageResponse> save(@Valid @RequestBody ContactMessageRequest contactMessageRequest) {

        return contactMessageService.save(contactMessageRequest);

    }



    // Not: *********************************  getAll() ********************************************
    //Admin, mudur ve mudur yardimcisinin erisebilecegi bir method.
    @GetMapping("/getAll")  // http://localhost:8080/contactMessages/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')") // ASSISTANT_MANAGER bu sekilde mepleme yapar. ASSISTANTMANAGER bu sekilde mepleyemiyor. defaultta 2 kelime varsa
    // arasa alt cizgi.
    // birazdan sana gonderecegim rollerden herhangi biri demek oluyor hasAnyAuthority. bu methodu birden cok role getirebilecek. Bu nedenle bu sekilde yaptik
    // bu methodda cok fazla data olabilir. Bu tur methodlar page ile yazilir. Page icinde one gonderecegimiz DTO muzu
    //yolluyoruz.
    public Page<ContactMessageResponse> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
            //normal bootta yaptigimizdan farkli bir yapi kullanicaz.normalde bu datalarin girilmesini zorunlu kilmistik.
            // simdi ise default bir deger verdik, istersek setler degistirir.
    ) {
        return contactMessageService.getAll(page,size,sort,type);

    }


    // Not: ***************************** searchByEmail() *************************************
    @GetMapping("/searchByEmail") // http://localhost:8080/contactMessages/searchByEmail?email=xxx@yyy.com&page=0&size=1&sort=date&type=desc
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public Page<ContactMessageResponse> searchByEmail(
            @RequestParam(value = "email") String email, // parametre degiskenini bu sekilde de verebiliriz.
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return contactMessageService.searchByEmail(email,page,size,sort,type);
    }


    // Not: ************************** searchBySubject()************************************
    @GetMapping("/searchBySubject") // http://localhost:8080/contactMessages/searchBySubject?subject=RestFull API&page=0&size=1&sort=date&type=desc
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    public Page<ContactMessageResponse> searchBySubject(
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "date") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return contactMessageService.searchBySubject(subject,page,size,sort,type);
    }

}

// ODEV : POSTMAN de END-POINTLER test edilecek