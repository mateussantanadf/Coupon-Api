package com.coupon.cupom.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum que representa os possíveis estados de um cupom
 */
public enum CupomStatus {
    /**
     * Cupom está ativo e pronto para uso
     */
    ACTIVE("Ativo"),
    
    /**
     * Cupom foi desativado temporariamente
     */
    INACTIVE("Inativo");

    private final String description;

    CupomStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    /**
     * Converte uma String em CupomStatus
     */
    public static CupomStatus fromString(String value) {
        try {
            return CupomStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + value);
        }
    }

    /**
     * Verifica se o cupom está ativo
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
}