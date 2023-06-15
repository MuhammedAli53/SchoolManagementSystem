package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.StudentInfoRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
}
