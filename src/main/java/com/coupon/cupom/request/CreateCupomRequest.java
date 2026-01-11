package com.coupon.cupom.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCupomRequest {

    @NotBlank(message = "Código é obrigatório")
    private String code;
    @NotBlank(message = "Descrição é obrigatória")
    private String description;
    @NotNull(message = "Valor de desconto é obrigatório")
    @DecimalMin(value = "0.5", inclusive = true,
            message = "Desconto mínimo é de 0.5")
    private BigDecimal discountValue;
    @NotNull(message = "Data de expiração é obrigatória")
    @Future(message = "Data de expiração deve ser futura")
    private LocalDateTime expirationDate;
    private boolean published;
}
