package com.schoolmanagement.entity.concretes;

import com.schoolmanagement.entity.abstracts.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
@Getter
@Setter// toString kullanmicaz bu nedenle data kullanmadik.
@NoArgsConstructor
@SuperBuilder
public class Admin extends User {

    private boolean built_in;// silinemez, hatta silinmesi dahi teklif edilemez. silinmemesi lazim. Bu nedenle built_in yapicaz bunu.
    //neden yapi bu sekilde? cunku sistemde her zaman bir tane adminimiz kalmali. Tum adminleri sildik diyelim, sonrasinda nasil admin
    // atamasi yapicaz? tekrar kodlari acip setlememiz lazim. Bu nedenle burda built_in fieldi true olan admin silinemez diyecegiz ayarlarimizi
    //yaparken.
}
