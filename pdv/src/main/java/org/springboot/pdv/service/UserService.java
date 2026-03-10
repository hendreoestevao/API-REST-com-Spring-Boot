package org.springboot.pdv.service;

import org.modelmapper.ModelMapper;
import org.springboot.pdv.dto.ProductInfoDTO;
import org.springboot.pdv.dto.UserDTO;
import org.springboot.pdv.dto.UserResponseDTO;
import org.springboot.pdv.entity.User;
import org.springboot.pdv.exceptions.NoItemException;
import org.springboot.pdv.repository.UserRepository;
import org.springboot.pdv.security.SecurityConfig;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(user ->
            new UserResponseDTO(user.getId(), user.getName(), user.getUsername(), user.isEnabled())).
                collect(Collectors.toList());
    }

    public UserDTO save(UserDTO dto) {
        dto.setPassword(SecurityConfig.passwordEncoder().encode(dto.getPassword()));
        User user = modelMapper.map(dto, User.class);
        user.setId(null);

        User savedUser = userRepository.save(user);

        return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getUsername(), dto.getPassword(), savedUser.isEnabled());
    }

    public UserDTO findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NoItemException("Usuario nao encontrado");
        }

        User user = optionalUser.get();
        return new UserDTO(user.getId(), user.getName(), user.getUsername(), user.getPassword(), user.isEnabled());
    }

    public UserDTO update(UserDTO dto) {
        dto.setPassword(SecurityConfig.passwordEncoder().encode(dto.getPassword()));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoItemException("Usuário não encontrado"));

        modelMapper.map(dto, user);

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserDTO.class);
    }


    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoItemException("Usuario nao encontrado");
        }
        userRepository.deleteById(id);
    }

    public User getByUserName(String username){
        return userRepository.findUserByUsername(username);
    }

}
