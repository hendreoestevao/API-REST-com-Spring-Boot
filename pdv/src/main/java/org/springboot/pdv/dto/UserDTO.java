package org.springboot.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private long userId;
    private String username;
    private boolean enabled;
    private List<ProductInfoDTO> productInfo;

}
