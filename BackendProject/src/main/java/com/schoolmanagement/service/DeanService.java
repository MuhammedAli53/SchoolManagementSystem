package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.DeanDto;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.DeanRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanService {
    private final DeanRepository deanRepository;
    private final AdminService adminService;
    private final DeanDto deanDto;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final FieldControl fieldControl;

    public ResponseMessage<DeanResponse> save(DeanRequest deanRequest) {
        fieldControl.checkDuplicate(deanRequest.getUsername(), // bu uc data unique olmali.
                deanRequest.getSsn(),
                deanRequest.getPhoneNumber());

        //DTO POJO donusumu.
        Dean dean = createDtoForDean(deanRequest);
        //burda role ve password eklememiz lazim. passwordu encode etmemiz lazim. Cunku bu bilgiler donen dto classinda yok.
        dean.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        dean.setPassword(passwordEncoder.encode(dean.getPassword()));
        Dean savedDean = deanRepository.save(dean);


        return ResponseMessage.<DeanResponse>builder().message("Dean saved").object(createDeanResponse(savedDean)).httpStatus(HttpStatus.CREATED).build();
    }

    private Dean createDtoForDean(DeanRequest deanRequest) {
        return deanDto.dtoDean(deanRequest);
    }

    private DeanResponse createDeanResponse(Dean dean) {
        return DeanResponse.builder().userId(dean.getId()).
                username(dean.getUsername()).
                name(dean.getName()).
                surname(dean.getSurname()).
                birthDay(dean.getBirthDay()).
                birthPlace(dean.getBirthPlace()).
                phoneNumber(dean.getPhoneNumber()).
                gender(dean.getGender()).
                ssn(dean.getSsn()).build();
    }

    public ResponseMessage<DeanResponse> update(DeanRequest newDean, Long deanId) {
        Optional<Dean> dean = deanRepository.findById(deanId); // orElseThrow yazarak direk handle edebilirsin.

        if (!dean.isPresent()) {   //isEmpty de kullanabiliriz. dean objesi bos ise exception firlaticaz. yukarda olElseThrow yapsakdik ici dolu mu diye kontrol etmeyecektik.
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
            //simdi bizim hem unique hem de unique olmayan fieldlerimiz var. Eger unique bilgiler degismisse checkDublicate methodumuz var zaten, onu cagiririz hallederiz.
            //ama unique olmayan datalarda degisim olduysa ne olacak? checkDublicate yazalim mi oraya ? bosuna performanstan yeriz. Buna gerek yok. Bu nedenle checkDublicate
            //yapmadan once kontrol etmemiz lazim.
        } else if (!CheckParameterUpdateMethod.checkParameter(dean.get(), newDean)) { // burda ilk dean parametresini verince kizardi. get() ile cagirmamiz lazim. Optrional yapidaki
            //deanin icindeki deani getir demis oluyoruz.
            fieldControl.checkDuplicate(newDean.getUsername(), newDean.getSsn(), newDean.getPhoneNumber());
        }
        //guncellenen yeni bilgilerle dean objesini kaydedelim.
        Dean updatedDean = createUpdatedDean(newDean, deanId); // deanrequest dean olarak dbye kaydedildi.
        updatedDean.setPassword(passwordEncoder.encode(newDean.getPassword())); // password encode ettik ve setledik.
        deanRepository.save(updatedDean);

        return ResponseMessage.<DeanResponse>builder().message("Dean updated successfully").httpStatus(HttpStatus.OK).object(createDeanResponse(updatedDean)).build();
    }

    private Dean createUpdatedDean(DeanRequest deanRequest, Long managerId) { // requestin icinde id yok, ama update islemi id uzerinden yapmamiz lazim.
        return Dean.builder().
                id(managerId).
                username(deanRequest.getUsername()).
                ssn(deanRequest.getSsn()).
                name(deanRequest.getName()).
                surname(deanRequest.getSurname()).
                birthPlace(deanRequest.getBirthPlace()).
                birthDay(deanRequest.getBirthDay()).
                phoneNumber(deanRequest.getPhoneNumber()).
                gender(deanRequest.getGender()).
                userRole(userRoleService.getUserRole(RoleType.MANAGER)). //UserRoleServiceden setlememiz lazim. Cunku service kati uzerinden islemlerimizi yapmamiz lazim.
                        build();
    }

        public ResponseMessage<?> deleteDean(Long deanId) {

           /* Optional<Dean> dean = deanRepository.findById(deanId);

            if(!dean.isPresent()) { // isEmpty() de kullanilabilir

                throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
            }*/
            chechIsEmpty(deanId);

            deanRepository.deleteById(deanId);

            return ResponseMessage.builder()
                    .message("Dean Deleted")
                    .httpStatus(HttpStatus.OK)
                    .build(); // burda neden objeyi gondermiyrouz? cunku sildik :D obje yok.
    }

    // Not :  getById() ************************************************************************
    public ResponseMessage<DeanResponse> getDeanById(Long deanId) {

        // ODEV : asagida goz kanatan kod grubu methoid haline cevrilip cagirilacak
         /* Optional<Dean> dean = deanRepository.findById(deanId);

            if(!dean.isPresent()) { // isEmpty() de kullanilabilir

                throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
            }*/
        Dean dean = chechIsEmpty(deanId);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean Successfully found")
                .httpStatus(HttpStatus.OK)
                .object(createDeanResponse(dean))
                .build();

    }

    // Not :  getAll() *************************************************************************
    public List<DeanResponse> getAllDean() {

        return deanRepository.findAll()
                .stream()
                .map(this::createDeanResponse)
                .collect(Collectors.toList());
    }

    public Page<DeanResponse> search(int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());

        if(Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return deanRepository.findAll(pageable).map(this::createDeanResponse);
    }

    //checkOptionalData
    private Dean chechIsEmpty(Long deanId){
        Optional<Dean> dean = deanRepository.findById(deanId);

        if(!dean.isPresent()) {

            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, deanId));
        }
        return dean.get();
    }

}