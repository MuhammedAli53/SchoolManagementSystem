package com.schoolmanagement.entity.concretes;

import com.schoolmanagement.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {// burasi bizim ara classimiz. serilestirme yazmadik. neden araya class koyduk. Direk enumtype uzerinden setleme yapmadik da neden userrole olusturduk?
    //ilerde yeni yeni roller eklememiz gerekebilir. DP derslerini hatirla. Isi yapan ile talepte bulunan yapinin arasina bir sey yerlestir.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType roleType;

}
