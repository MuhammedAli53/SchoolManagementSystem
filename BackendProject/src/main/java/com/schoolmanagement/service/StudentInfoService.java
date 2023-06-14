package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.repository.StudentInfoRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
        // DTO ve requestten gelen bir student, teacher, lesson ve educationterm datalarimizi almamiz lazim.
        Student student = studentService.getStudentByIdForResponse(studentInfoRequest.getStudentId()); // id vericez ve bize pojo donmesi lazim
        Teacher teacher = teacherService.getTeacherByUsername(username);
        Lesson lesson = lessonService.getLessonById(studentInfoRequest.getLessonId());
        EducationTerm educationTerm = educationTermService.getById(studentInfoRequest.getEducationTermId());
        //lesson cakismasi var mi kontrolu
        if (checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName())){
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_LESSON_MESSAGE,lesson.getLessonName()));
        }

        //dersnotu ortalamasi aliniyor.
        Double notAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam());
        //ders notu alfabetik olarak hesaplaniyor.
    }

    private boolean checkSameLesson(Long studentId, String lessonName){
        return studentInfoRepository.getAllByStudentId_Id(studentId).stream().anyMatch((e)->e.getLesson().getLessonName().equalsIgnoreCase(lessonName));
    }

    private Double calculateExamAverage(Double midtermExam, Double finalExam){
        return ((midtermExam*midtermExamPercentage)+(finalExam*finalExamPercentage));
    }
}
