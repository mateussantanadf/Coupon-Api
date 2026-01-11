package com.coupon.cupom.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCupomRequest {

    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private boolean published;
}
