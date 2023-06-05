package com.schoolmanagement.controller;

import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.payload.request.LoginRequest;
import com.schoolmanagement.payload.response.AuthResponse;
import com.schoolmanagement.security.jwt.JwtUtils;
import com.schoolmanagement.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
    // login olan islemler icin bu class kullanilacak.

    public final JwtUtils jwtUtils;

    public final AuthenticationManager authenticationManager;

    @PostMapping("/login") // login methodu bu.
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){

        //Gelen requestin icinden kullanici adi ve parola bilgisini aliyoruz.
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        //authenticationmanager uzerinden kullaniciyi valide ediyoruz.
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        //valide edilen kullanici contexte atiliyor.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //Jwt Token olusturuluyor.
        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        //currently login islemini gerceklestiren security katmanindaki userDetailsi bana gonderir. Userdetails oldugu icin onuın rolleri yok.
        // grantedAuth. si var. Bunu string turune cevirmemiz lazim.

        Set<String> roles = userDetails.getAuthorities() //burdan collection yapi gelir bize
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Optional<String> role = roles.stream().findFirst();

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder(); //objeyi once bos olusturduk. simdi setlicez. Bunu da gorme ihtimalimiz var.
        authResponse.username(userDetails.getUsername());
        authResponse.token(token);
        authResponse.name(userDetails.getName());

        if (role.isPresent()){
            authResponse.role(role.get());
            if (role.get().equalsIgnoreCase(RoleType.TEACHER.name())){
                authResponse.isAdvisor(userDetails.getIsAdvisor().toString());
            }
        }
        //authresponse nesnesini response entitye ceviriyoruz.
        return ResponseEntity.ok(authResponse.build());
    }

}
