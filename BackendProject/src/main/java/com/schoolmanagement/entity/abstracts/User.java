package com.schoolmanagement.entity.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass // Db de user tablosu olusmadan bu sinifin anac sinif olarak kullanilmasini sagliyor.Tum entity ler bu classtan tureyecek.
//Bunu hibernate e bu annotation ile bildiriyoruz.
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder // Alt siniflarin user sinifinin builder ozelligini kullanabilmesine izin verir. mesela id yi diger classlar burdaki ozellikleri
// de kullanacak.
// !!! @SuperBuilder ile @Builder arasindaki temel fark :https://www.baeldung.com/lombok-builder-inheritance
// !!! @SuperBuilder in duzgun calismasi icin hem parent a hem de childa @SuperBuilder eklenmeli
public abstract class User implements Serializable {// bu classimizda, her entityde olan datalari setlicez.Cok fazla ortak field var.
    // bunlari her entityde tekrar yazmak yerine bu sekilde yapiyoruz. BaseEntity yapimiz bu. olusacak olan tum entityler buradan
    //turetilsin.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String ssn;

    private String name;

    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // donen deger localdate turunde olmasin da String olarak donsun.
    private LocalDate birthDay;

    private String birthPlace;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // on tarafa her zaman dto dondurucez ve dto icinde pass olmayacak. Ama kazara dondurduk diyelim. nolcak? bir daha burda
    //kontrol yapiyoruz. Clientten DBye giden seye write islemidir. Dbden cliente giden islem ise read islemidir. Bu annotationla birlikte passwordumuz sadece write islemlerinde
    //onay vermis oluyoruz. Json data write isleminde password  alir ama read isleminde okumaz.
    // hassas data oldugu icin okuma islemlerinde kullanilmasin
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @OneToOne// her userin 1 rolu olacak. Bu nedenle onetoone.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)// kullanicinin role bilgisi on tarafa gitmesin.Neden gitmesin?
    //DBdeki yapimizi user gormesin.
    private UserRole userRole;

    private Gender gender;








}
