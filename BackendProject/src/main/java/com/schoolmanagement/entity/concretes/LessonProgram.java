package com.schoolmanagement.entity.concretes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.enums.Day;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgram implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime stopTime;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "lesson_program_lesson",
            joinColumns =@JoinColumn(name = "lessonprogram_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_id"))
    private Set<Lesson> lesson; // burda lesson setledik, lessonda lessonprogram setlemedik.
    //burda hibernate otomatik 3. tabloy atar. bu tur seyleri default setlemeye birakma. bu nedenle biz lessonda
    //setlicez.

    @ManyToOne(cascade = CascadeType.PERSIST)
    private EducationTerm educationTerm;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "lessonsProgramList", fetch = FetchType.EAGER)
    private Set<Teacher> teachers;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "lessonsProgramList", fetch = FetchType.EAGER)
    private Set<Student> students;

    @PreRemove// silmeden once yapilmasi gerekenler var. lessonprogrami silmek icin ne yapmamiz lazim. bunu preRemove ile yapmasak da olur ama farkli bakis acilari getiriyoruz.
    private void removeLessonProgramFromStudent(){
        //calculus programini silmek istiyoruz mesela. Matematigi bir alt dali. bunu direk cat diye silemem. Teacherler ve studentler var. bu yapiya baglilar. Once bu bagi ortadan
        //kaldirmam gerekiyor. ders listesinden program siliyoruz yani.
        //burda ders programini siliyoruz. Bu ders programi ile iliskili ogrenci ve ogretmenlerin baglarini koparmamiz lazim.
        teachers.forEach((t)->{// bu methodun cagiirilmasina sebep olan bu instance hangisi ise (this ile belirttik, silmek istedigimiz instance)
            // o nesneyi kaldir. yani ders programindan möatematigi kaldirmak istiyoruz. Bu instance ile bagli teacher ve studenttin, matematik dersi ile bagini burda kopartiyoruz.
            t.getLessonsProgramList().remove(this);
        });

        students.forEach((s)->{
            s.getLessonsProgramList().remove(this);
        });
    }

}
