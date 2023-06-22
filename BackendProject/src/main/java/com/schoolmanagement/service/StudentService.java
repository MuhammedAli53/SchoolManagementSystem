package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.CheckSameLessonProgram;
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
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final FieldControl fieldControl;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final LessonProgramService lessonProgramService;

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
        if (!CheckParameterUpdateMethod.checkParameter(student,studentRequest)){
            fieldControl.checkDuplicate(studentRequest.getUsername(),studentRequest.getSsn(),studentRequest.getPhoneNumber(),studentRequest.getEmail());
        }



        //dto -> pojo. yukarida yazmistik aslinda dto pojo donusumu ama orda id yok. rolu dto pojo donusum methodunda setledik. Burda ayri bir yerde
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

    public ResponseMessage<?> deleteStudent(Long id) {
        Student student =studentRepository.findById(id).orElseThrow(()->  new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        studentRepository.deleteById(id);
        return ResponseMessage.builder().message("Student deleted successfully").httpStatus(HttpStatus.OK).build();
    }

    public List<StudentResponse> getStudentByName(String studentName) {
        return studentRepository.getStudentByNameContaining(studentName) // findByName olarak da turetebilirdik.
                .stream().map(this::createStudentResponse) // stream yapida dtolar geliyor, bunlari liste cevirmemiz lazim.
                .collect(Collectors.toList());
    }

    public Student getStudentByIdForResponse(Long id) { // eger service de bir method pojo donuyorsa bu neden olabilir?
        //baska service de kullanilacagi icin Pojo dondururuz. Service katmani icin gecerli bu dedigimiz. bu methodu studentInfoService in save methodunda kullaniyoruz.
        return studentRepository.findById(id).orElseThrow(()->  new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
         //
    }

    public Page<StudentResponse> search(int page, int size, String sort, String type) {
        //Pageable pageable = PageRequest.of(page,size, Sort.by(type,sort)); ayni isi yapar.
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if (Objects.equals(type,"desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return studentRepository.findAll(pageable).map(this::createStudentResponse);
    }

    public ResponseMessage<StudentResponse> chooseLesson(String username, ChooseLessonProgramWithId chooseLessonProgramRequest) {

        //student ve lessonprogramlar kontrolu yapicaz.
        Student student = studentRepository.findByUsername(username).orElseThrow(()->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        //talep edilen lessonprogram
        Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(chooseLessonProgramRequest.getLessonProgramId());
        // donen yapi pojo olmali. Bu nednele bu methodu kullaniyoruz.
        if (lessonPrograms.size()==0){
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        }
        // ogrencinin mevcut lesson programini getiriyoruz.
        Set<LessonProgram> studentLessonProgram = student.getLessonsProgramList();

        //lesson icin dublicate kontrolu
        CheckSameLessonProgram.checkDuplicateLessonPrograms(studentLessonProgram,lessonPrograms);
        studentLessonProgram.addAll(lessonPrograms);
        student.setLessonsProgramList(studentLessonProgram);
        Student savedStudent = studentRepository.save(student);

        return ResponseMessage.<StudentResponse>builder().message("Lessons added to Student").object(createStudentResponse(savedStudent))
                .httpStatus(HttpStatus.CREATED).build();
    }

    public List<StudentResponse> getAllStudentByTeacher_Username(String username) {
        return studentRepository.getStudentByAdvisorTeacher_Username(username)
                .stream().map(this::createStudentResponse).collect(Collectors.toList());
    }

    public boolean existByUsername(String username) {
        return studentRepository.existsByUsername(username);
    }

    public boolean existById(Long studentId) {
        return studentRepository.existsById(studentId);
    }

    public List<Student> getStudentByIds(Long[] studentIds) {
        return studentRepository.findByIdsEquals(studentIds);
    }

    public Optional<Student> getSudentByUsernameForOptional(String username) {
        return studentRepository.findByUsernameEqualsForOptional(username);
    }

    public Set<Student> getStudentByIds(List<Long> studentIds ){
        return studentRepository.findByIdsEquals(studentIds);
    }

}
