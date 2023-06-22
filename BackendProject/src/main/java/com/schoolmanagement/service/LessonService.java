package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.LessonRequest;
import com.schoolmanagement.payload.response.LessonResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.LessonRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    public ResponseMessage<LessonResponse> save(LessonRequest lessonRequest) {

        //conflict kontrolu

        if (existsLessonByLessonName(lessonRequest.getLessonName())){
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_LESSON_MESSAGE, lessonRequest.getLessonName()));
            // belirtecli mesaj gondereceksek de String format.
        }
        Lesson lesson = createLessonObject(lessonRequest);

        return ResponseMessage.<LessonResponse>builder()
                .object(createLessonResponse(lessonRepository.save(lesson)))
                .message("Lesson Created Successfully")
                .httpStatus(HttpStatus.CREATED)
                .build();

    }

    private boolean existsLessonByLessonName(String lessonName){
        return lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName); // bu method turetilir.field isimlerimiz ve keywordlerden kurulu bir yapi oldu.
    }

    private Lesson createLessonObject(LessonRequest lessonRequest){
        return Lesson.builder().lessonName(lessonRequest.getLessonName())
                .creditScore(lessonRequest.getCreditScore()).isCompulsory(lessonRequest.getIsCompulsory()).build();
    }
    private LessonResponse createLessonResponse(Lesson lesson){
        return LessonResponse.builder().creditScore(lesson.getCreditScore()).lessonName(lesson.getLessonName())
                .isCompulsory(lesson.getIsCompulsory()).lessonId(lesson.getLessonId()).build();
    }

    public ResponseMessage deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(()->{ // lesson objesi varsa lesson icine at, yoksa excp firlat.
            return new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,id));
        });
        lessonRepository.deleteById(id);
        return ResponseMessage.builder().message("Lesson is deleted successfully").httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {
        Lesson lesson = lessonRepository.getLessonByLessonName(lessonName).orElseThrow(()->{
            return new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE,lessonName));
        });
        return ResponseMessage.<LessonResponse>builder().message("Lesson successfully found").object(createLessonResponse(lesson)).build();
    }

    public List<LessonResponse> getAllLesson() {

        return lessonRepository.findAll().stream().map(this::createLessonResponse).collect(Collectors.toList());
    }

    // Not :  getAllWithPage() **********************************************************
    public Page<LessonResponse> search(int page, int size, String sort, String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if(Objects.equals(type,"desc")) {
            pageable = PageRequest.of(page,size,Sort.by(sort).descending());
        }

        return lessonRepository.findAll(pageable).map(this::createLessonResponse); // findAll pojo doner!!! response cevirmemiz lazim.
    }

    public Set<Lesson> getLessonByLessonIdList(Set<Long> lessons) { // burda lesson dondurduk.

        return lessonRepository.getLessonByLessonIdList(lessons);
    }

    // Not: StudentInfoService icin yazildi
    public Lesson getLessonById(Long lessonId) {

        if(!lessonRepository.existsByLessonIdEquals(lessonId))
            throw  new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE, lessonId));

        return lessonRepository.findByLessonIdEquals(lessonId);
    }

    public List<Lesson> getLessonByLessonIdList(List<Long> lessons) {

        return lessonRepository.getLessonByLessonIdList(lessons);
    }

}
