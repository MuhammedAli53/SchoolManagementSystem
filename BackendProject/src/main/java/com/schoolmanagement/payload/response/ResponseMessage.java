package com.schoolmanagement.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Json icindeki null olanlarin gozukmemesini sagliyoruz

public class ResponseMessage<E>{//bu bizim response entity classimiz olacak. Hep bu classi kullanicaz. Generic yapida.
    //istersek admin classimizi veririz, istersek baska classlarimizi. Esnek yapi kuruyoruz.

    //Bu classimizi neden olusturduk? ResponseEntity yapisinda bir classtir. Bu nedenle biz kendimiz bir ResponseEntity classi
    // ile islem yapmak istersek icerigi bu sekilde olacak. Hatta daha da spesifik sekilde setleme yapmamiza imkan saglar.
    //cok daha esnek bir yapi olusturur bizlere.
    private E object ;
    private String message;
    private HttpStatus httpStatus;
}
