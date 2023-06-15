package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.StudentInfoRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;

    private final StudentService studentService;

    private final TeacherService teacherService;

    private final LessonService lessonService;

    private final EducationTermService educationTermService;

    @Value("${midterm.exam.impact.percentage}") // datalari properties den setledik.
    private Double midtermExamPercentage;

    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;

    public ResponseMessage<StudentInfoResponse> save(String username, StudentInfoRequestWithoutTeacherId studentInfoRequest) {
        // DTO ve requestten gelen bir student, teacher, lesson ve educationterm datalarimizi almamiz lazim. Bu nedenle bu classlarin servicelerini bu classa enjekte ettik.
        Student student = studentService.getStudentByIdForResponse(studentInfoRequest.getStudentId()); // id vericez ve bize pojo donmesi lazim. cunku request veya response lerde id yok.
        Teacher teacher = teacherService.getTeacherByUsername(username); //donen deget httpservletten aldigimiz username.
        Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());
        EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());
        //lesson cakismasi var mi kontrolu yapmamiz lazim.
        if (checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName())){
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_LESSON_MESSAGE,lesson.getLessonName()));
        }

        //dersnotu ortalamasi aliniyor.
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam());
        //ders notu alfabetik olarak hesaplaniyor.
        Note note = checkLetterGrade(noteAverage);
        //elimizde tum veriler var artik. dto pojo donusumu yapicaz.
        StudentInfo studentInfo = createDto(studentInfoRequest,note, noteAverage);
        //burayi direk kaydedemeyiz. yukarda cagirdigimiz datalari setlememiz lazim. yukarda cagirdigimiz bilgiler id bilgileri. yani dtoda olmayan fieldlerin setlenmesi lazim.
        studentInfo.setStudent(student);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);
        studentInfo.setTeacher(teacher);
        //simdi kayit islemi yapicaz.
        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo); //

        return ResponseMessage.<StudentInfoResponse>builder().message("Student Info save successfully").httpStatus(HttpStatus.CREATED).object(createResponse(savedStudentInfo)).build();

    }

    private boolean checkSameLesson(Long studentId, String lessonName){
        return studentInfoRepository.getAllByStudentId_Id(studentId) // sondaki idyi silersek kendisi turer. Ancak biz kendimiz yazmak istedik.
                .stream() // simdi bize studentInfo lar donecek bu methoddan.
                .anyMatch((e)->e.getLesson().getLessonName().equalsIgnoreCase(lessonName)); // gelen datanin lessonlarina bak ve yukarda eslesen data va mi kontrol et. varsa true.
        //bu nedenle anyMatch
    }

    private Double calculateExamAverage(Double midtermExam, Double finalExam){
        //farkli yapğilar kullanabiliriz. Mesele dersler zorunluysa yuzde 50 50 olsun sinavlar gibi.
        return ((midtermExam*midtermExamPercentage)+(finalExam*finalExamPercentage)); // datalari properties dosyamiza ekledik. alacagimiz datalari  32,35 satirda setledik.
    }
    private Note checkLetterGrade(Double average){
        if (average<50) {
            return Note.FF;
        } else if (average>=50 && average<55) {
            return Note.DD;
        }else if (average>=55 && average<60) {
            return Note.DC;
        }else if (average>=60 && average<65) {
            return Note.CC;
        }else if (average>=65 && average<70) {
            return Note.CB;
        }else if (average>=70 && average<75) {
            return Note.BB;
        }else if (average>=75 && average<80) {
            return Note.BA;
        }else
            return Note.AA;
        }
    private StudentInfo createDto(StudentInfoRequestWithoutTeacherId studentInfoRequest, Note note, Double average){
        return StudentInfo.builder().infoNote(studentInfoRequest.getInfoNote()).absentee(studentInfoRequest.getAbsentee()).midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam()).examAverage(average).letterGrade(note).build();
    }

    private StudentInfoResponse createResponse(StudentInfo studentInfo){
        return StudentInfoResponse.builder()
                .lessonName(studentInfo.getLesson().getLessonName())
                .creditScore(studentInfo.getLesson().getCreditScore())
                .isCompulsory(studentInfo.getLesson().getIsCompulsory())
                .educationTerm(studentInfo.getEducationTerm().getTerm())
                .id(studentInfo.getId())
                .absentee(studentInfo.getAbsentee())
                .midtermExam(studentInfo.getMidtermExam())
                .finalExam(studentInfo.getFinalExam())
                .infoNote(studentInfo.getInfoNote())
                .note(studentInfo.getLetterGrade())
                .average(studentInfo.getExamAverage())
                .studentResponse(createStudentResponse(studentInfo.getStudent()))
                .build();

    }
    public StudentResponse createStudentResponse(Student student){
        return StudentResponse.builder()
                .userId(student.getId())
                .username(student.getUsername())
                .name(student.getName())
                .surname(student.getSurname())
                .birthDay(student.getBirthDay())
                .birthPlace(student.getBirthPlace())
                .phoneNumber(student.getPhoneNumber())
                .gender(student.getGender())
                .email(student.getEmail())
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .studentNumber(student.getStudentNumber())
                .isActive(student.isActive())
                .build();
    }

    public ResponseMessage<?> deleteStudentInfo(Long id) {
        if (!studentInfoRepository.existsByIdEquals(id)){
            throw new ResourceNotFoundException(Messages.STUDENT_INFO_NOT_FOUND);
        }
        studentInfoRepository.deleteById(id);

        return ResponseMessage.builder().message("Student Info deleted successfully").httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<StudentInfoResponse> update(UpdateStudentInfoRequest studentInfoRequest, Long studentInfoId) {
        Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());
    // neden donen degeri pojo aliyoruz farkli servicelerde? cunku db ile islemimiz var. DTO olarak alirsak yine dto pojo donusumu yapmamiz lazim.
        StudentInfo getStudentInfo = getStudentInfoById(studentInfoId);
        EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());

        //not ortalamasi hesaplaniyor.
        Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam());
        //alfabetik not
        Note note = checkLetterGrade(noteAverage);

        //dto --> pojo
        StudentInfo studentInfo = createUpdatedStudent(studentInfoRequest,studentInfoId,lesson,educationTerm,note,noteAverage);
        studentInfo.setTeacher(getStudentInfo.getTeacher());
        studentInfo.setStudent(getStudentInfo.getStudent());

        StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfo);
        return ResponseMessage.<StudentInfoResponse>builder()
                .message("Student Info Updated Successfully")
                .httpStatus(HttpStatus.OK)
                .object(createResponse(updatedStudentInfo))
                .build();
    }
    private StudentInfo getStudentInfoById(Long studentInfoId){
        if (!studentInfoRepository.existsByIdEquals(studentInfoId)){
            throw new ResourceNotFoundException(Messages.STUDENT_INFO_NOT_FOUND);
        }
        return studentInfoRepository.findByIdEquals(studentInfoId);
    }
    private StudentInfo createUpdatedStudent(UpdateStudentInfoRequest studentInfoRequest,
                                             Long studentInfoRequestId,
                                             Lesson lesson,
                                             EducationTerm educationTerm,
                                             Note note,
                                             Double average){
        return StudentInfo.builder()
                .id(studentInfoRequestId)
                .infoNote(studentInfoRequest.getInfoNote())
                .midtermExam(studentInfoRequest.getMidtermExam())
                .finalExam(studentInfoRequest.getFinalExam())
                .absentee(studentInfoRequest.getAbsentee())
                .lesson(lesson)
                .educationTerm(educationTerm)
                .examAverage(average)
                .letterGrade(note)
                .build();

    }


    public Page<StudentInfoResponse> getAllForAdmin(Pageable pageable) {
        return studentInfoRepository.findAll(pageable).map(this::createResponse);
    }

    public Page<StudentInfoResponse> getAllForTeacher(Pageable pageable, String username) {
        return studentInfoRepository.findByTeacherId_UsernameEquals(username,pageable).map(this::createResponse);
    }

    public Page<StudentInfoResponse> getAllStudentInfoByStudent(String username, Pageable pageable) {
        boolean student = studentService.existByUsername(username);
        if (!student){
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        }
        return studentInfoRepository.findByStudentId_UsernameEquals(username,pageable);
    }

    public List<StudentInfoResponse> getStudentInfoByStudentId(Long studentId) {
        if(!studentService.existById(studentId)) {
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, studentId));
        }
        if(!studentInfoRepository.existsByStudent_IdEquals(studentId)) {
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID, studentId));
        }

        return studentInfoRepository.findByStudent_IdEquals(studentId)
                .stream()
                .map(this::createResponse)
                .collect(Collectors.toList());
    }

    // Not: getStudentInfoById()*******************************************************

    public StudentInfoResponse findStudentInfoById(Long id) {

        if(!studentInfoRepository.existsByIdEquals(id)) {
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND,id));
        }

        return createResponse(studentInfoRepository.findByIdEquals(id));

    }
}
