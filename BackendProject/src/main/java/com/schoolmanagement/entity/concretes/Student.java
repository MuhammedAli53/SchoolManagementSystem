package com.schoolmanagement.entity.concretes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.schoolmanagement.entity.abstracts.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder// farkli parametrelerle const uretmemizi saglar. Ayrica tum parametreli cons uretmemizi de saglar. Bu nedenle @AllArgsConst yazmadik.
//super builder, parent child olan tum classlar icin yazilir. Tek sade bir classa Builder yapistir. Turetilen veya tureyen classlar icin superBuilder.
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)// equals ve hashcode methodunu active eder. 2 objeyi birbiri ile kiyaslamak = equals.
// bu 2 classin referanslarinin ayni oldugunu nasil anlariz? burdaki fieldler
//ayniysa bu ogrenci ayni ogrencidir. Ancak burda eksik field var. Id falan. Parent classtaki farkliliklari da karsilastir diyorsak bu annotation. 2 objenin ayni mi farkli mi
//oldugunu anlamamiza yarar.   onlyExplicitlyIncluded bu ozelligi actigimizda her field ile karsilastirma yapmaz. Bizim belirledigimiz unique
// datalar uzerinden karsilastirma yapabiliriz.oldukca hizli calisir.
@ToString(callSuper = true)// burdan bir instance olusturup soutladigimizda yapistirir. Ayrica parentteki field leri de sout icine alir.
//@Data kullanmadik. nedeni ise @ToStriing kullandik callsuper icin. bu nedenle data koymadik.
public class Student extends User {

    private String motherName;

    private String fatherName;

    private int studentNumber;

    private boolean isActive ;

    @Column(unique = true)
    private String email;

    @ManyToOne(cascade = CascadeType.PERSIST) // !!! buradaki persist kaldirilacak
    @JsonIgnore
    private AdvisorTeacher advisorTeacher;

    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<StudentInfo> studentInfos;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "student_lessonprogram",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonsProgramList;

    @JsonIgnore
    @ManyToMany
    @JoinTable(// hem studentte hem de meette @JoinTable yazdik. sikinti olmaz. iki classtaki tablo ismi de ayni.
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "meet_id")
    )
    private List<Meet> meetList;


}
