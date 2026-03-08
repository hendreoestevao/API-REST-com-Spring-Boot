package org.springboot.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springboot.pdv.entity.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private long userId;
    private String name;
    private boolean enabled;
    private List<ProductInfoDTO> productInfo;

}
