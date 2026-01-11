package com.coupon.cupom.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateCupomRequest {

    @Schema(example = "ABC123", description = "Código do cupom (6 caracteres)")
    @NotBlank(message = "Código é obrigatório")
    private String code;

    @Schema(example = "Cupom de desconto")
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @Schema(example = "10.0", minimum = "0.5")
    @NotNull(message = "Valor de desconto é obrigatório")
    @DecimalMin(value = "0.5", inclusive = true,
            message = "Desconto mínimo é de 0.5")
    private BigDecimal discountValue;

    @Schema(example = "2026-12-31T23:59:59")
    @NotNull(message = "Data de expiração é obrigatória")
    @Future(message = "Data de expiração deve ser futura")
    private LocalDateTime expirationDate;

    @Schema(example = "true")
    private boolean published;
}
