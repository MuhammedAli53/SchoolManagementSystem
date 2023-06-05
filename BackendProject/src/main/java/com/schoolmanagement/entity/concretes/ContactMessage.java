package com.schoolmanagement.entity.concretes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true) // bir nesnemiz var ve 10 tane fieldi var. bu 10 fieldin 8 tanesi ayni, sadece 2 tane degistiricez.
// bunu clonable ile yapmistik. Lombok lar bu islemi arka planda yapiyor.
// bu classimiz tum tablolardan bagimsizdir yani herhangi bir tablo ile veya security ile bagli olmayacak.
// Db ile iliskili olmali ama. Cunku anonim kullanici tarafindan gonderilecek mesajlar kaydedilmeli. Ayrica bu segmentte security katmanimiz da olmayacak.
// sitenin iletisim bolumunde bir mesaj yazma yeri. kayit olmaya gerek yok, ananim kullanici olabilir yani.
public class ContactMessage implements Serializable {// serializable dan implement olarak ayarladik. Cunku Json datayi kaybetmeden almaliyiz. Tum entity classlarimiz serializable
    // sinifindan impl. edilir. Best practice budur. Json datayi en iyi sekilde almak istiyoruz. Serializableden impl etmezsek bazen sikinti cikabiliyor.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name ;
    @NotNull
    private String email ;
    @NotNull
    private String subject ;
    @NotNull
    private String message ;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    // formatsiz yazdigimizda milisaniyeye kadar bize getirir. Ancak bu sekilde yazarsak formatlamis oluruz.
    private LocalDate date;

}
