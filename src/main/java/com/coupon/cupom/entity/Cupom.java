package com.coupon.cupom.entity;

import com.coupon.cupom.exception.CupomInvalidoException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um cupom de desconto
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cupons")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CupomStatus status;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean redeemed;

    @Column(nullable = true)
    private LocalDateTime redeemedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Factory Method - Cria um novo cupom com validações
     */
    public static Cupom criar(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDateTime expirationDate,
            boolean published
    ) {
        validarCode(code);
        validarExpirationDate(expirationDate);
        validarDiscountValue(discountValue);
        validarDescription(description);

        Cupom cupom = new Cupom();
        cupom.code = tratarCode(code);
        cupom.description = description.trim();
        cupom.discountValue = discountValue;
        cupom.expirationDate = expirationDate;
        cupom.published = published;
        cupom.redeemed = false;
        cupom.status = published ? CupomStatus.ACTIVE : CupomStatus.INACTIVE;

        return cupom;
    }

    /**
     * Publica um cupom inativo
     */
    public void publicar() {
        if (this.published) {
            throw new IllegalStateException("Cupom já foi publicado");
        }
        if (this.isDeleted()) {
            throw new IllegalStateException("Não é possível publicar um cupom deletado");
        }
        this.published = true;
        this.status = CupomStatus.ACTIVE;
    }

    /**
     * Desativa um cupom publicado
     */
    public void desativar() {
        if (!this.published) {
            throw new IllegalStateException("Cupom não está publicado");
        }
        this.status = CupomStatus.INACTIVE;
    }

    /**
     * Resgate o cupom (soft delete lógico)
     */
    public void resgatar() {
        validarPodeSerResgatado();
        this.redeemed = true;
        this.redeemedAt = LocalDateTime.now();
        this.status = CupomStatus.INACTIVE;
    }

    /**
     * Deleta logicamente um cupom (soft delete)
     */
    public void deletar() {
        if (this.isDeleted()) {
            throw new IllegalStateException("Cupom já foi deletado");
        }
        if (this.redeemed) {
            throw new IllegalStateException("Não é possível deletar um cupom já resgatado");
        }
        this.deletedAt = LocalDateTime.now();
        this.status = CupomStatus.INACTIVE;
    }

    /**
     * Verifica se está expirado
     */
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.expirationDate);
    }

    /**
     * Verifica se foi deletado (soft delete)
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Verifica se pode ser resgatado
     */
    public boolean podeSerResgatado() {
        return !this.redeemed && !this.isDeleted() && !this.isExpirado() && this.published;
    }

    /**
     * Atualiza as informações do cupom
     */
    public void atualizar(String description, BigDecimal discountValue) {
        if (this.isDeleted()) {
            throw new IllegalStateException("Não é possível atualizar um cupom deletado");
        }
        if (this.redeemed) {
            throw new IllegalStateException("Não é possível atualizar um cupom já resgatado");
        }
        
        validarDescription(description);
        validarDiscountValue(discountValue);
        
        this.description = description.trim();
        this.discountValue = discountValue;
    }

    private static void validarCode(String code) {
        if (code == null || code.isBlank()) {
            throw new CupomInvalidoException("Código não pode ser vazio");
        }
    }

    private static void validarDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new CupomInvalidoException("Descrição não pode ser vazia");
        }
        if (description.length() > 255) {
            throw new CupomInvalidoException("Descrição não pode exceder 255 caracteres");
        }
    }

    private static void validarExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            throw new CupomInvalidoException("Data de expiração não pode ser nula");
        }
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new CupomInvalidoException("Data de expiração não pode ser no passado");
        }
    }

    private static void validarDiscountValue(BigDecimal discountValue) {
        if (discountValue == null) {
            throw new CupomInvalidoException("Valor de desconto não pode ser nulo");
        }
        if (discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new CupomInvalidoException("Valor mínimo de desconto é 0,50");
        }
        if (discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new CupomInvalidoException("Valor máximo de desconto é 100");
        }
    }

    private void validarPodeSerResgatado() {
        if (this.redeemed) {
            throw new IllegalStateException("Cupom já foi resgatado");
        }
        if (this.isDeleted()) {
            throw new IllegalStateException("Cupom foi deletado");
        }
        if (this.isExpirado()) {
            throw new IllegalStateException("Cupom expirou");
        }
        if (!this.published) {
            throw new IllegalStateException("Cupom não foi publicado");
        }
    }

    public static String tratarCode(String code) {
        String tratado = code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (tratado.length() != 6) {
            throw new CupomInvalidoException("Código deve possuir exatamente 6 caracteres alfanuméricos");
        }
        return tratado;
    }
}