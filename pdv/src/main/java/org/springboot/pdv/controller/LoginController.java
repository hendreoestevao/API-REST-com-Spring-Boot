package org.springboot.pdv.controller;

import jakarta.validation.Valid;
import org.springboot.pdv.dto.LoginDTO;
import org.springboot.pdv.dto.ResponseDTO;
import org.springboot.pdv.dto.TokenDTO;
import org.springboot.pdv.security.CustomUserDetailService;
import org.springboot.pdv.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    private CustomUserDetailService userDetailService;

    private JwtService jwtService;

    @Value("${security.jwt.expiration}")
    private String expiration;

    public LoginController(CustomUserDetailService userDetailService, JwtService jwtService) {
        this.userDetailService = userDetailService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity login(@Valid @RequestBody LoginDTO loginData) {
        try {
            userDetailService.verifyUserCredentials(loginData);
            String token = jwtService.generateToken(loginData.getUsername());
            return new ResponseEntity(new TokenDTO(token,expiration),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(new ResponseDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
        }

    }
}
