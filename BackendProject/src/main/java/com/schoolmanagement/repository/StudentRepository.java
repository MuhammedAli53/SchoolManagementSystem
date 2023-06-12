package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Student findByUsernameEquals(String username);

    boolean existsByEmail(String email);

    @Query(value = "Select (count(s>0)) From Student s "  ) // value degerini ister yaz ister yazma.
    boolean findStudent();

    @Query(value = "Select MAX(s.studentNumber) From Student s")
    int getMaxStudentNumber();
}
