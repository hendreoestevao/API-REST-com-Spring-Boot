package org.springboot.pdv.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private long userId;
    @Column(length = 100, nullable = false)
    @NotBlank(message = "Campo nome é obrigatório")
    private String name;

    @Column(length = 30, nullable = false, unique = true)
    @NotBlank(message = "O campo username é obrigatorio")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "O campo password é obrigatorio")
    private String password;
    private boolean enabled;

}
