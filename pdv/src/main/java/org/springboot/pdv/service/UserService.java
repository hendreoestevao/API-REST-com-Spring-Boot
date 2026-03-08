package org.springboot.pdv.service;

import org.springboot.pdv.dto.ProductInfoDTO;
import org.springboot.pdv.dto.UserDTO;
import org.springboot.pdv.entity.User;
import org.springboot.pdv.exceptions.NoItemException;
import org.springboot.pdv.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getId());
            userDTO.setUsername(user.getName());
            userDTO.setEnabled(user.isEnabled());

            List<ProductInfoDTO> products = user.getSaleList().stream().flatMap(sale -> sale.getItemSales().stream().map(itemSale -> {
                ProductInfoDTO productInfoDTO = new ProductInfoDTO();
                productInfoDTO.setId(itemSale.getProduct().getId());
                productInfoDTO.setDescription(itemSale.getProduct().getDescription());
                productInfoDTO.setQuantity(itemSale.getQuantity());
                return productInfoDTO;
            })).toList();

            userDTO.setProductInfo(products);
            return userDTO;
        }).collect(Collectors.toList());
    }

    public UserDTO save(User user) {

        User savedUser = userRepository.save(user);

        return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.isEnabled(), null);
    }

    public UserDTO findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NoItemException("Usuario nao encontrado");
        }

        User user = optionalUser.get();
        return new UserDTO(user.getId(), user.getName(), user.isEnabled(), null);
    }

    public UserDTO update(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (!optionalUser.isPresent()) {
            throw new NoItemException("Usuario nao encontrado");
        }

        userRepository.save(user);
        return new UserDTO(user.getId(), user.getName(), user.isEnabled(), null);
    }


    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoItemException("Usuario nao encontrado");
        }
        userRepository.deleteById(id);
    }

}
