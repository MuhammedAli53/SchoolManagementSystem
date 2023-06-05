package com.schoolmanagement.service;


import com.schoolmanagement.entity.concretes.ContactMessage;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

import static com.schoolmanagement.utils.Messages.ALREADY_SEND_A_MESSAGE_TODAY;


@Service
@RequiredArgsConstructor // normalde bunu yazmayip direk const acabilirdik ama bu annotationu kulllandigimizda final olarak set
//ledigimiz data icin constructor olusturur.
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;


    // Not: save() methodu **********************************************************************
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) {
        // bir kullanici bir gunde sadece 1 message gonderebilsin. Bu bizim requestimiz. Bunu kontrol etmemiz lazim.
        //bunun icin email uzerinden kontrol yapicaz ve gunluk kontrolu de date uzerinden kontrol edicez.
         boolean isSameMessageWithSameEmailForToday =
                 contactMessageRepository.existsByEmailEqualsAndDateEquals(contactMessageRequest.getEmail(),LocalDate.now());
                         //contactMessageRequest da email bilgisi var get yap. date bilgisine nasil ulasicaz? bugunu aliyoruz :D
                         // dbdeki gunle bugun ayni mi. exist ile basliyor methodumuz bu nedenle bu bir boolean type.
                         // peki repodaki bu methodun icine bir sey yaziyor muyuz? hayiz. Exists, by, equals, and, equals. bunlar keywordler.
                         //JPA bu keywordleri anlar.                                                            LocalDate.now());

         if(isSameMessageWithSameEmailForToday) throw new ConflictException(String.format(ALREADY_SEND_A_MESSAGE_TODAY));
        //exception mesajinin adini yaz ve alt enter a bas. Class adini yazmana gerek yok yani. Yukarda import etti zaten.

        // kaydedilmis datayi saveledik ve aldik bu datayi. Response olarak cevirirken de bunu kullanicaz.
        //simdi ise on tarafa dondurucez. Ancak normalde save methodunda on tarafa obje dondurulmez. ResponseEntityde obje dondurme yok.
        //biz burda obje de dondurmek istiyoruz on tarafa. Projenin mimarisi bu sekilde yapilmis.
        ContactMessage contactMessage = createObject(contactMessageRequest);
        ContactMessage savedDate = contactMessageRepository.save(contactMessage);

          return ResponseMessage.<ContactMessageResponse>builder() //bunun da builder annotationu var.
                .message("Contact Message Created Successfully")
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(savedDate))
                .build();
        // neden ContactMessageRequesti dondurmedik? koda bunu yazarsak da calisir ama valide islemleri yapiliyor burda. Bosuna.

    }

    //DTO POJO donusum methodu. Bu methodu service classimizda surekli kullanicaz. Bu nedenle methodunu bir kere yazalim, surekli kullanicaz.
    private ContactMessage createObject(ContactMessageRequest contactMessageRequest){// sadece bu classta kullanicaz bu nedenle private
        // builder annotationu simdi islevsellik kazanacak. Bu annotationu ContactMessage da yazmistik. Tum Pojo classlarimizda yazariz bu annotationu

        return ContactMessage.builder() //builder annotationu ile gelir. Bu method ile istedigimiz datalari setleyebiliriz.
                // normalde parametreli cons acardik hepsini setlerdir falan. Ama builder methodu cok daha rahat. Bu annotation Lomboktan gelir.
                .name(contactMessageRequest.getName())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .date(LocalDate.now())//dtomuzda date fieldi yok. Burda setlicez. direk now'a cekiyoruz.
                .build();// build ile yukarda setledigimiz degerlerle bir obje olusturuyoruz.

    }

    // !!! POJO-DTO donusumu icin yardimci method
    private ContactMessageResponse createResponse(ContactMessage contactMessage){

        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .date(contactMessage.getDate())
                .build();
    }

    // Not:  ***************************  getAll() methodu ****************************************
    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        //default degeri kullanildi mi kullanilmadi mi? bunu kontrol etmemiz lazim.
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.findAll(pageable).map(this::createResponse);//dbye gittik findall dedik, bize burdan ne doner? POJOlar. findAll collection bir yapidir(Page).
        // dtoya cevirme yapicaz. map cevirme islemi yapar.
        //bize normalde pojo geliyor ve bunu map ile dtoya ceviriyoruz. burdaki this, kendinden onceki datayi temsil eder. Yani pageable yapisindaki pojolari.


        // return contactMessageRepository.findAll(pageable).map(r->createResponse(r));   bu yolla da olur.
    }

    // Not: searchByEmail() methodu **********************************************************************
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) {
     Pageable pageable =  PageRequest.of(page,size,Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.findByEmailEquals(email, pageable).map(this::createResponse);

    }

    // Not: searchBySubject() methodu **********************************************************************
    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, String type) {

        Pageable pageable =  PageRequest.of(page,size,Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return contactMessageRepository.findBySubjectEquals(subject,pageable).map(this::createResponse);
    }


}
