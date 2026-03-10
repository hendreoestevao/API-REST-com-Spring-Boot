package org.springboot.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Campo nome é obrigatório")
    private String name;

    @Column(length = 30, nullable = false, unique = true)
    @NotBlank(message = "O campo username é obrigatorio")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "O campo password é obrigatorio")
    private String password;

    private boolean isEnabled;

    @OneToMany(mappedBy = "user")
    private List<Sale>  saleList;

}
