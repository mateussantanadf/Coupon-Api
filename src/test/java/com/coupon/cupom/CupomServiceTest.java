package com.coupon.cupom;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.repository.CupomRepository;
import com.coupon.cupom.request.CreateCupomRequest;
import com.coupon.cupom.service.CupomService;
import com.coupon.cupom.util.CupomStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
        cupom.setDiscountValue(BigDecimal.valueOf(10.0));
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
    void DeveCriarCupomComStatusActive() {
        Cupom cupom = criarCupom();

        assertEquals(CupomStatus.ACTIVE, cupom.getStatus());
    }

    @Test
    void DeveRetornarUmCupomPeloId() {
        UUID id = UUID.randomUUID();

        Cupom cupom = criarCupom();

        when(repository.findById(id))
                .thenReturn(Optional.of(cupom));

        Cupom cupomEncontrado = service.buscarPorId(id);

        assertNotNull(cupomEncontrado);
        assertEquals(cupom.getCode(), cupomEncontrado.getCode());
        assertEquals(cupom.getDescription(), cupomEncontrado.getDescription());
        assertEquals(cupom.getStatus(), cupomEncontrado.getStatus());
    }


    @Test
    void DeveLancarExcecaoQuandoCodigoNaoPossuir6Caracteres() {
        CreateCupomRequest request = new CreateCupomRequest();
        request.setCode("BEMV");
        request.setDescription("Cupom teste caracteres");
        request.setDiscountValue(new BigDecimal("0.5"));
        request.setExpirationDate(LocalDateTime.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.salvarCupom(request)
        );

        assertEquals(
                "Código do cupom deve possuir 6 caracteres",
                ex.getMessage()
        );
    }

    @Test
    void DeveRemoverCaracteresEspeciaisDoCodigo() {
        CreateCupomRequest request = new CreateCupomRequest();
        request.setCode("B@E#M$V%IN");
        request.setDescription("Cupom teste limpar code");
        request.setDiscountValue(new BigDecimal("0.5"));
        request.setExpirationDate(LocalDateTime.now().plusDays(1));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cupom cupom = service.salvarCupom(request);

        assertEquals("BEMVIN", cupom.getCode());
    }

    @Test
    void DeveDeletarUmCupomAtivo() {
        Cupom cupom = criarCupom();

        when(repository.findById(cupom.getId()))
                .thenReturn(Optional.of(cupom));

        when(repository.save(any(Cupom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cupom cupomDeletado = service.deleteCupom(cupom.getId());

        assertEquals(CupomStatus.DELETED, cupomDeletado.getStatus());
    }

    @Test
    void DeveLancarExcecaoQuandoCupomJaFoiDeletado() {
        Cupom cupom = criarCupom();
        cupom.setStatus(CupomStatus.DELETED);

        when(repository.findById(cupom.getId()))
                .thenReturn(Optional.of(cupom));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.deleteCupom(cupom.getId())
        );

        assertEquals(
                "Cupom já deletado",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoDescontoCupomForMenorQue05() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Cupom.criar(
                        "BAD001",
                        "Desconto inválido",
                        BigDecimal.valueOf(0.49),
                        LocalDateTime.now().plusDays(1),
                        true
                )
        );

        assertEquals(
                "Valor mínimo de desconto é 0,5",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoDataDeExpiracaoForNoPassado() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Cupom.criar(
                        "EXP001",
                        "Cupom expirado",
                        BigDecimal.valueOf(1.0),
                        LocalDateTime.now().minusDays(1),
                        true
                )
        );

        assertEquals(
                "Data de expiração não pode ser no passado",
                exception.getMessage()
        );
    }
}
