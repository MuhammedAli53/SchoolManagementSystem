package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.payload.response.EducationTermResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.EducationTermRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;

    public ResponseMessage<EducationTermResponse> save(EducationTermRequest request) {

        //son kayit tarihi, ders doneminin baslangic tarihinden sonra olmamali. Eger boyle olduysa excp firlaticaz.
        if (request.getLastRegistrationDate().isAfter(request.getStartDate())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }

        //bitis tarihi baslangic tarihinden once olmamali.
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }

        // ayni term ve baslangic tarihine sahip birden fazla kayit var mi kontrol et. burda senelik kontrol yapicaz. 2023 senesinde 1 tane guz donemi olur.
        if (educationTermRepository.existsByTermAndYear(request.getTerm(), request.getStartDate().getYear())) {
            // bu method turemez. year data turu yok bizim fieldlerde. Mecburen native query yazicaz.
            throw new ResourceNotFoundException(Messages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }

        //save methoduna dto pojo donusumu yapip gonderiyoruz.
        EducationTerm savedEducationTerm = educationTermRepository.save(createEducationTerm(request));

        //response objesi olusturuluyor.
        return ResponseMessage.<EducationTermResponse>builder()
                .message("Education Term created")
                .httpStatus(HttpStatus.CREATED)
                .object(createEducationTermResponse(savedEducationTerm))
                .build();
    }

    private EducationTerm createEducationTerm(EducationTermRequest request) {
        return EducationTerm.builder().
                term(request.getTerm()).
                startDate(request.getStartDate()).
                endDate(request.getEndDate()).
                lastRegistrationDate(request.getLastRegistrationDate()).
                build();
    }

    private EducationTerm createEducationTerm(Long id, EducationTermRequest request ) { // *******
        return EducationTerm.builder()
                .id(id)
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .build();
    }

    private EducationTermResponse createEducationTermResponse(EducationTerm response) {
        return EducationTermResponse.builder()
                .id(response.getId())
                .term(response.getTerm())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .lastRegistrationDate(response.getLastRegistrationDate())
                .build();

    }

    public EducationTermResponse get(Long id) {

        // ya id yoksa ?
        checkEducationTermExists(id);
        /*if (!educationTermRepository.existsByIdEquals(id)) {
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
        } //existsById isimizi gorur aslinda. ama query yazalim dedik.*/

        return createEducationTermResponse(educationTermRepository.findByIdEquals(id));

    }

    public List<EducationTermResponse> getAll() {
        return educationTermRepository.findAll()
                .stream()
                .map(this::createEducationTermResponse)
                .collect(Collectors.toList());
    }


    public Page<EducationTermResponse> getAllWithPage(int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        if (Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }
        return educationTermRepository.findAll(pageable).map(this::createEducationTermResponse); // findAll Pojo dondurur !!!!!
    }

    public ResponseMessage<?> delete(Long id) {

        checkEducationTermExists(id);
        /*if (!educationTermRepository.existsById(id))
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
        // idli data donduruyorsak String.formatta yazmamiz gerekir.*/

        educationTermRepository.deleteById(id);

        return ResponseMessage.builder().message("Education Term deleted").httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<EducationTermResponse> update( Long id, EducationTermRequest request) {
       /* if (!educationTermRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id));
        }*/
        checkEducationTermExists(id);
        if (request.getStartDate()!= null && request.getLastRegistrationDate()!=null){
            if (request.getLastRegistrationDate().isAfter(request.getStartDate())){
                throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
            }
        }
        if (request.getStartDate()!=null && request.getEndDate()!=null){
            if (request.getEndDate().isBefore(request.getStartDate())){
                throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
            }
        }
        ResponseMessage.ResponseMessageBuilder<EducationTermResponse> responseMessageBuilder = ResponseMessage.builder(); // bos obje olusturduk

        EducationTerm updated = createEducationTerm(id, request);
        educationTermRepository.save(updated);
        return responseMessageBuilder.message("Education term updated")
                .object(createEducationTermResponse(updated)).build();

    }
        // bu method lessonservice classinda kullanildi. getById methodunda
    public EducationTerm getById(Long educationTermId) {

        checkEducationTermExists(educationTermId);
       /* if (!educationTermRepository.existsByIdEquals(educationTermId)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, educationTermId));
        }*/
        return educationTermRepository.findByIdEquals(educationTermId);
    }

    private void checkEducationTermExists(Long id){
        if (!educationTermRepository.existsByIdEquals(id)){
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
        }
    }
}