package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final FieldControl fieldControl;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public ResponseMessage<StudentResponse> save(StudentRequest studentRequest) {
        //burda yapacagimiz sey su. Requestte advisorteacherle ilgili datamiz var. once onu kontrol etmemiz lazim.
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherById(studentRequest.getAdvisorTeacherId()).orElseThrow(()->
        {throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE,studentRequest.getAdvisorTeacherId()));
        });
        // idli advisoru getir, datayi da request ten alicaz.

        //simdi dublicate kontrolu yapicaz.
        fieldControl.checkDuplicate(studentRequest.getUsername(), studentRequest.getSsn(), studentRequest.getPhoneNumber());

        //simdi DTO - > Pojo
        Student student = studentRequestToDto(studentRequest);
        //studentnumber setlicez. yardimci method olusturucaz.
        student.setStudentNumber(lastNumber());
        //advisor teacher setlemesi
        student.setAdvisorTeacher(advisorTeacher);
        //role setleme
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        //active setlicez.
        student.setActive(true);
        //password set
        student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));

        //lessonProgramlar nerde? Requeste lessonprogram eklemedik. bu nedenle cekemedik. Farkli bir method ile buraya dallanicaz. Once student create edilecek
        //sonra 2. request olrak bu ogrenciye bu dersler setlensin diye bir endpoint eklicez.

        //studentRepository.save(student);   return icinde save islemi yaptik.

        return ResponseMessage.<StudentResponse>builder().object(createStudentResponse(studentRepository.save(student)))
                .message("Student save successfully").build();
    }

    private Student studentRequestToDto(StudentRequest studentRequest){
        return Student.builder().fatherName(studentRequest.getFatherName()).motherName(studentRequest.getMotherName())
                .birthDay(studentRequest.getBirthDay()).birthPlace(studentRequest.getBirthPlace()).name(studentRequest.getName())
                .surname(studentRequest.getSurname()).username(studentRequest.getUsername()).ssn(studentRequest.getSsn())
                .email(studentRequest.getEmail()).phoneNumber(studentRequest.getPhoneNumber()).password(studentRequest.getPassword())
                .gender(studentRequest.getGender()).build();
    }
    public int lastNumber(){
        if (!studentRepository.findStudent()){
            return 1000;
        }
        return studentRepository.getMaxStudentNumber()+1;
    }
    private StudentResponse createStudentResponse(Student student){
        return StudentResponse.builder().userId(student.getId()).username(student.getUsername())
                .name(student.getName()).surname(student.getSurname()).birthDay(student.getBirthDay()).birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber()).gender(student.getGender()).email(student.getEmail()).motherName(student.getMotherName())
                .fatherName(student.getFatherName()).studentNumber(student.getStudentNumber()).isActive(student.isActive()).build();
    }

    public ResponseMessage<?> changeStatus(Long id, boolean status) {

        Student student = studentRepository.findById(id).orElseThrow(()-> {throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);});
        student.setActive(status);
        studentRepository.save(student);
        return ResponseMessage.builder().message("Student is "+(status ? "active" : "passive")).httpStatus(HttpStatus.OK)
                .build();
    }

    public List<StudentResponse> getAllStudent() {
        return studentRepository.findAll().stream().map(this::createStudentResponse).collect(Collectors.toList());
    }

    public ResponseMessage<StudentResponse> updateStudent(Long userId, StudentRequest studentRequest) {

        Student student = studentRepository.findById(userId).orElseThrow(()-> {throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);});
        //advisor teacheri kontrol etmemiz lazim. var mi yok mu diye. Mutlaka olmali. NotNull zaten
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherById(studentRequest.getAdvisorTeacherId()).orElseThrow(()->
        {throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE, studentRequest.getAdvisorTeacherId()));});

        //dublicate controlu
        fieldControl.checkDuplicate(studentRequest.getUsername(),studentRequest.getSsn(),studentRequest.getPhoneNumber(),studentRequest.getEmail());


        //dto -> pojo. yukard ayazmistik aslinda dto pojo donusumu ama orda id yok. rolu dto pojo donusum methodunda setledik. Burda ayri bir yerde
        //de setleyebilirdik.
        Student updatedStudent = createUpdatedStudent(studentRequest,userId);
        updatedStudent.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        updatedStudent.setAdvisorTeacher(advisorTeacher);
        updatedStudent.setStudentNumber(student.getStudentNumber());
        updatedStudent.setActive(true);
        studentRepository.save(updatedStudent);

        return ResponseMessage.<StudentResponse>builder().message("Student updated successfully").httpStatus(HttpStatus.OK)
                .object(createStudentResponse(updatedStudent)).build();

    }

    private Student createUpdatedStudent(StudentRequest studentRequest, Long userId){
        return Student.builder().id(userId).fatherName(studentRequest.getFatherName()).motherName(studentRequest.getMotherName())
                .birthDay(studentRequest.getBirthDay()).birthPlace(studentRequest.getBirthPlace()).name(studentRequest.getName())
                .surname(studentRequest.getSurname()).username(studentRequest.getUsername()).ssn(studentRequest.getSsn())
                .email(studentRequest.getEmail()).phoneNumber(studentRequest.getPhoneNumber()).password(studentRequest.getPassword())
                .gender(studentRequest.getGender()).userRole(userRoleService.getUserRole(RoleType.STUDENT)).build();
    }
}
