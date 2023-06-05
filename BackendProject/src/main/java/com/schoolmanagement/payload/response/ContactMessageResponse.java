package com.schoolmanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageResponse implements Serializable {
    // bu classimiz backendden frontende gidecek. Bu nedenle validation yapmiyoruz. Burasi DTOmuz aslinda. gerek yok.
    //DTOyu one donduruyoruz. Valide islemi yapabiliriz de ama gerek yok. Bos yere performanstan yemesin diyoruz.
    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDate date;
}
