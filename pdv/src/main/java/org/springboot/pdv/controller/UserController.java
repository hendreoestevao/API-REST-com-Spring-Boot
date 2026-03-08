package org.springboot.pdv.controller;

import org.springboot.pdv.dto.ResponseDTO;
import org.springboot.pdv.dto.SaleInfoDTO;
import org.springboot.pdv.dto.UserDTO;
import org.springboot.pdv.entity.User;
import org.springboot.pdv.repository.UserRepository;
import org.springboot.pdv.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDTO> save(@RequestBody User user) {
        try {
            user.setEnabled(true);
            return new ResponseEntity<>(userService.save(user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ResponseDTO(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<UserDTO> update(@RequestBody User user) {
        try {
            return new ResponseEntity<>(userService.update(user), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity(new ResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity(new ResponseDTO(e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return new ResponseEntity<>("Usúario removido com sucesso!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
