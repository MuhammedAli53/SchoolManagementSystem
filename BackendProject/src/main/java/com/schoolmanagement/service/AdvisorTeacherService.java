package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.response.AdvisorTeacherResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.AdvisorTeacherRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdvisorTeacherService {

    private final AdvisorTeacherRepository advisorTeacherRepository;
    private final UserRoleService userRoleService;

    public ResponseMessage<?> deleteAdvisorTeacher(Long id) {

        AdvisorTeacher advisorTeacher = advisorTeacherRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        advisorTeacherRepository.deleteById(id);
        return ResponseMessage.<AdvisorTeacher>builder().message("Advisor Teacher deleted successfully").httpStatus(HttpStatus.OK).build();
    }

    public List<AdvisorTeacherResponse> getAllAdvisorTeacher() {
        return advisorTeacherRepository.findAll().stream().map(this::createResponseObject).collect(Collectors.toList());
    }
    private AdvisorTeacherResponse createResponseObject(AdvisorTeacher advisorTeacher){
        return AdvisorTeacherResponse.builder().advisorTeacherId(advisorTeacher.getId())
                .teacherName(advisorTeacher.getTeacher().getName())
                .teacherSurname(advisorTeacher.getTeacher().getSurname()).teacherSSN(advisorTeacher.getTeacher().getSsn()).build();
    }

    public Page<AdvisorTeacherResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }

        return advisorTeacherRepository.findAll(pageable).map(this::createResponseObject);
    }

    // ************************ Teacher Service icin gerekli methodlar *******************************

    //******************** save ***************************
    public void saveAdvisorTeacher(Teacher teacher) {
        AdvisorTeacher advisorTeacherBuilder = AdvisorTeacher.builder().teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER))
                .build();

        advisorTeacherRepository.save(advisorTeacherBuilder);
    }
    //******************** update ***************************
    public void updateAdvisorTeacher(boolean status, Teacher teacher) {
        Optional<AdvisorTeacher> advisorTeacher = advisorTeacherRepository.getAdvisorTeacherByTeacher_Id(teacher.getId());
        AdvisorTeacher.AdvisorTeacherBuilder advisorTeacherBuilder =
                AdvisorTeacher.builder().teacher(teacher).userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER));

        if (advisorTeacher.isPresent()){
            if (status){
                advisorTeacherBuilder.id(advisorTeacher.get().getId());
                advisorTeacherRepository.save(advisorTeacherBuilder.build());
            }else {
                advisorTeacherRepository.deleteById(advisorTeacher.get().getId());
            }
        }else {
            advisorTeacherRepository.save(advisorTeacherBuilder.build());
        }
    }
}
