package org.springboot.pdv.controller;

import jakarta.validation.Valid;
import org.springboot.pdv.dto.ResponseDTO;
import org.springboot.pdv.dto.UserDTO;
import org.springboot.pdv.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sing-up")
public class SingUpController {

    public UserService userService;

    public SingUpController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO user) {
        try {
            user.setEnabled(true);
            return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ResponseDTO(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}
