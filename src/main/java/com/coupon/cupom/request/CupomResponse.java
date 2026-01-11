package com.coupon.cupom.request;

import com.coupon.cupom.util.CupomStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CupomResponse {

    private UUID id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private CupomStatus status;
    private boolean published;
    private boolean redeemed;
}
