package com.coupon.cupom.entity;

import com.coupon.cupom.util.CupomStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "cupons")
public class Cupom {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CupomStatus status;

    private boolean published;

    private boolean redeemed;

    public static Cupom criar(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDateTime expirationDate,
            boolean published
    ) {
        String codeTratado = filtrarCode(code);

        if (codeTratado.length() != 6) {
            throw new IllegalArgumentException(
                    "Código do cupom deve possuir 6 caracteres");
        }

        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Data de expiração não pode ser no passado"
            );
        }

        if (discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new IllegalArgumentException(
                    "Valor mínimo de desconto é 0,5"
            );
        }

        Cupom cupom = new Cupom();
        cupom.code = codeTratado;
        cupom.description = description;
        cupom.discountValue = discountValue;
        cupom.expirationDate = expirationDate;
        cupom.published = published;
        cupom.redeemed = false;
        cupom.status = published ? CupomStatus.ACTIVE : CupomStatus.INACTIVE;

        return cupom;
    }

    public void deletar() {
        if (this.status == CupomStatus.DELETED) {
            throw new IllegalStateException("Cupom já deletado");
        }
        this.status = CupomStatus.DELETED;
    }

    private static String filtrarCode(String code) {
        return code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }
}
