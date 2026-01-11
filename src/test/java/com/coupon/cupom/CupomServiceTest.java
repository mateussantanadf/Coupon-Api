package com.coupon.cupom;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.repository.CupomRepository;
import com.coupon.cupom.request.CreateCupomRequest;
import com.coupon.cupom.request.CupomResponse;
import com.coupon.cupom.service.CupomService;
import com.coupon.cupom.util.CupomStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CupomServiceTest {

    @InjectMocks
    private CupomService service;

    @Mock
    private CupomRepository repository;

    public Cupom criarCupom() {
        CreateCupomRequest cupom = new CreateCupomRequest();
        cupom.setCode("NOVO26");
        cupom.setDescription("Novo cupom 2026");
        cupom.setDiscountValue(BigDecimal.valueOf(10.00));
        cupom.setExpirationDate(LocalDateTime.parse("2026-11-04T17:14:45.180"));
        cupom.setPublished(true);

        when(repository.save(any(Cupom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return service.salvarCupom(cupom);
    }

    @Test
    void DeveRetornarUmCupomCriado() {
        Cupom cupomCriado = criarCupom();
        String cupomEsperado = "NOVO26";

        assertEquals(cupomEsperado, cupomCriado.getCode());
        assertEquals("Novo cupom 2026", cupomCriado.getDescription());
        assertTrue(cupomCriado.isPublished());
        assertEquals(CupomStatus.ACTIVE, cupomCriado.getStatus());
    }

    @Test
    void deveCriarCupomComStatusActive() {
        Cupom cupom = criarCupom();

        assertEquals(CupomStatus.ACTIVE, cupom.getStatus());
    }
}
