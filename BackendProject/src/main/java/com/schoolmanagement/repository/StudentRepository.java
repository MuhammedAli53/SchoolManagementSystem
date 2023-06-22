package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Student findByUsernameEquals(String username);

    boolean existsByEmail(String email);

    @Query(value = "Select (count(s)>0) From Student s "  ) // value degerini ister yaz ister yazma.
    boolean findStudent();

    @Query(value = "Select MAX(s.studentNumber) From Student s")
    int getMaxStudentNumber();

    List<Student> getStudentByNameContaining(String studentName);

    Optional<Student> findByUsername(String username);

    @Query(value = "Select s From Student s Where s.advisorTeacher.teacher.username = :username") // ?1 de yazabilirdik.
    //burda aslinda 2 tane inner join islemi yaptik. JPQL in guzelligi burda. 3 tabloya dallandik burda. Studentten advisor teachere, ordan teachere.
        // @Query(value= "SELECT s FROM Student s JOIN s.advisorTeacher at JOIN at.teacher t WHERE t.username=:username")
    List<Student> getStudentByAdvisorTeacher_Username(String username);

    @Query(value = "Select s From Student s Where s.id IN :id")
    List<Student> findByIdsEquals(Long[] id);

    @Query(value = "Select s From Student s Where s.username=:username")
    Optional<Student> findByUsernameEqualsForOptional(String username);

    @Query(value = "Select s From Student s Where s.id IN :id")
    Set<Student> findByIdsEquals(List<Long> id);
}
