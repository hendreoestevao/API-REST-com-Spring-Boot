package org.springboot.pdv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleInfoDTO {

    private long saleId;
    private String user;
    private String date;
    private BigDecimal total;
    private List<ProductInfoDTO> products;
}
