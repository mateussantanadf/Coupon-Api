package com.coupon.cupom;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.entity.CupomStatus;
import com.coupon.cupom.exception.CupomInvalidoException;
import com.coupon.cupom.exception.CupomNotFoundException;
import com.coupon.cupom.repository.CupomRepository;
import com.coupon.cupom.request.CreateCupomRequest;
import com.coupon.cupom.service.CupomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("CupomService - Testes Unitários")
class CupomServiceTest {

    @InjectMocks
    private CupomService service;

    @Mock
    private CupomRepository repository;

    private UUID cupomId;
    private CreateCupomRequest validRequest;
    private Cupom cupomValido;

    @BeforeEach
    void setup() {
        cupomId = UUID.randomUUID();
        
        validRequest = new CreateCupomRequest();
        validRequest.setCode("NOVO26");
        validRequest.setDescription("Novo cupom 2026");
        validRequest.setDiscountValue(BigDecimal.valueOf(10.0));
        validRequest.setExpirationDate(LocalDateTime.now().plusDays(30));
        validRequest.setPublished(true);

        cupomValido = Cupom.criar(
                validRequest.getCode(),
                validRequest.getDescription(),
                validRequest.getDiscountValue(),
                validRequest.getExpirationDate(),
                validRequest.isPublished()
        );
        cupomValido.getId();
    }

    // ======================== TESTES DE CRIAÇÃO ========================

    @Nested
    @DisplayName("Testes de Criação de Cupom")
    class TestCriarCupom {

        @Test
        @DisplayName("Deve criar um cupom com sucesso")
        void deveCriarCupomComSucesso() {
            when(repository.existsByCode(validRequest.getCode()))
                    .thenReturn(false);
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cupom resultado = service.salvarCupom(validRequest);

            assertNotNull(resultado);
            assertEquals("NOVO26", resultado.getCode());
            assertEquals("Novo cupom 2026", resultado.getDescription());
            assertEquals(BigDecimal.valueOf(10.0), resultado.getDiscountValue());
            assertTrue(resultado.isPublished());
            assertEquals(CupomStatus.ACTIVE, resultado.getStatus());

            verify(repository).existsByCode(validRequest.getCode());
            verify(repository).save(any(Cupom.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando código já existe")
        void deveLancarExcecaoQuandoCodigoJaExiste() {
            when(repository.existsByCode(validRequest.getCode()))
                    .thenReturn(true);

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.salvarCupom(validRequest)
            );

            assertEquals(
                    "Cupom com código NOVO26 já existe",
                    exception.getMessage()
            );
            
            verify(repository).existsByCode(validRequest.getCode());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando código não possui 6 caracteres")
        void deveLancarExcecaoQuandoCodigoInvalido() {
            validRequest.setCode("ABC");

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.salvarCupom(validRequest)
            );

            assertTrue(exception.getMessage().contains("6 caracteres"));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve remover caracteres especiais do código")
        void deveRemoverCaracteresEspeciais() {
            validRequest.setCode("NO#V@O!26");

            when(repository.existsByCode("NOVO26"))
                    .thenReturn(false);
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cupom resultado = service.salvarCupom(validRequest);

            assertEquals("NOVO26", resultado.getCode());
            verify(repository).existsByCode("NOVO26");
        }

        @Test
        @DisplayName("Deve lançar exceção quando desconto é menor que 0.50")
        void deveLancarExcecaoQuandoDescontoMenor() {
            validRequest.setDiscountValue(BigDecimal.valueOf(0.49));

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.salvarCupom(validRequest)
            );

            assertEquals("Valor mínimo de desconto é 0,50", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando desconto é maior que 100")
        void deveLancarExcecaoQuandoDescontoMaior() {
            validRequest.setDiscountValue(BigDecimal.valueOf(100.01));

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.salvarCupom(validRequest)
            );

            assertEquals("Valor máximo de desconto é 100", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando data de expiração é no passado")
        void deveLancarExcecaoQuandoDataNoPassado() {
            validRequest.setExpirationDate(LocalDateTime.now().minusDays(1));

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.salvarCupom(validRequest)
            );

            assertEquals("Data de expiração não pode ser no passado", exception.getMessage());
        }
    }

    // ======================== TESTES DE BUSCA ========================

    @Nested
    @DisplayName("Testes de Busca de Cupom")
    class TestBuscarCupom {

        @Test
        @DisplayName("Deve buscar um cupom por ID com sucesso")
        void deveBuscarCupomPorIdComSucesso() {
            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));

            Cupom resultado = service.buscarPorId(cupomId);

            assertNotNull(resultado);
            assertEquals("NOVO26", resultado.getCode());
            assertEquals("Novo cupom 2026", resultado.getDescription());
            
            verify(repository).findById(cupomId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando cupom não encontrado")
        void deveLancarExcecaoQuandoCupomNaoEncontrado() {
            when(repository.findById(cupomId))
                    .thenReturn(Optional.empty());

            CupomNotFoundException exception = assertThrows(
                    CupomNotFoundException.class,
                    () -> service.buscarPorId(cupomId)
            );

            assertTrue(exception.getMessage().contains(cupomId.toString()));
            verify(repository).findById(cupomId);
        }
    }

    // ======================== TESTES DE ATUALIZAÇÃO ========================

    @Nested
    @DisplayName("Testes de Atualização de Cupom")
    class TestAtualizarCupom {

        @Test
        @DisplayName("Deve atualizar um cupom com sucesso")
        void deveAtualizarCupomComSucesso() {
            CreateCupomRequest updateRequest = new CreateCupomRequest();
            updateRequest.setCode("NOVO26");
            updateRequest.setDescription("Descrição atualizada");
            updateRequest.setDiscountValue(BigDecimal.valueOf(15.0));
            updateRequest.setExpirationDate(LocalDateTime.now().plusDays(60));
            updateRequest.setPublished(true);

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cupom resultado = service.atualizarCupom(cupomId, updateRequest);

            assertNotNull(resultado);
            assertEquals("Descrição atualizada", resultado.getDescription());
            assertEquals(BigDecimal.valueOf(15.0), resultado.getDiscountValue());

            verify(repository).findById(cupomId);
            verify(repository).save(any(Cupom.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar cupom deletado")
        void deveLancarExcecaoAoAtualizarCupomDeletado() {
            cupomValido.deletar();

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.atualizarCupom(cupomId, validRequest)
            );

            assertTrue(exception.getMessage().contains("deletado"));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com código duplicado")
        void deveLancarExcecaoQuandoCodigoDuplicado() {
            CreateCupomRequest updateRequest = new CreateCupomRequest();
            updateRequest.setCode("OUTRO99");
            updateRequest.setDescription("Nova descrição");
            updateRequest.setDiscountValue(BigDecimal.valueOf(10.0));
            updateRequest.setExpirationDate(LocalDateTime.now().plusDays(30));
            updateRequest.setPublished(true);

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));
            when(repository.existsByCode("OUTRO99"))
                    .thenReturn(true);

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.atualizarCupom(cupomId, updateRequest)
            );

            assertTrue(exception.getMessage().contains("já existe"));
            verify(repository, never()).save(any());
        }
    }

    // ======================== TESTES DE DELEÇÃO ========================

    @Nested
    @DisplayName("Testes de Deleção de Cupom")
    class TestDeletarCupom {

        @Test
        @DisplayName("Deve deletar um cupom com sucesso")
        void deveDeletarCupomComSucesso() {
            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            service.deleteCupom(cupomId);

            ArgumentCaptor<Cupom> captor = ArgumentCaptor.forClass(Cupom.class);
            verify(repository).save(captor.capture());
            
            assertTrue(captor.getValue().isDeleted());
            assertEquals(CupomStatus.INACTIVE, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar cupom já deletado")
        void deveLancarExcecaoAoDeletarCupomJaDeletado() {
            cupomValido.deletar();

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> service.deleteCupom(cupomId)
            );

            assertTrue(exception.getMessage().contains("deletado"));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar cupom resgatado")
        void deveLancarExcecaoAoDeletarCupomResgatado() {
            cupomValido.resgatar();

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> service.deleteCupom(cupomId)
            );

            assertTrue(exception.getMessage().contains("resgatado"));
            verify(repository, never()).save(any());
        }
    }

    // ======================== TESTES DE RESGATE ========================

    @Nested
    @DisplayName("Testes de Resgate de Cupom")
    class TestResgatarCupom {

        @Test
        @DisplayName("Deve resgatar um cupom com sucesso")
        void deveResgatarCupomComSucesso() {
            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cupom resultado = service.resgatarCupom(cupomId);

            assertTrue(resultado.isRedeemed());
            assertNotNull(resultado.getRedeemedAt());
            assertEquals(CupomStatus.INACTIVE, resultado.getStatus());

            verify(repository).save(any(Cupom.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao resgatar cupom já resgatado")
        void deveLancarExcecaoAoResgatarCupomJaResgatado() {
            cupomValido.resgatar();

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.resgatarCupom(cupomId)
            );

            assertTrue(exception.getMessage().contains("resgatado"));
            verify(repository, never()).save(any());
        }

//        @Test
//        @DisplayName("Deve lançar exceção ao resgatar cupom expirado")
//        void deveLancarExcecaoAoResgatarCupomExpirado() {
//            CreateCupomRequest expiradoRequest = new CreateCupomRequest();
//            expiradoRequest.setCode("EXP001");
//            expiradoRequest.setDescription("Expirado");
//            expiradoRequest.setDiscountValue(BigDecimal.valueOf(10.0));
//            expiradoRequest.setExpirationDate(LocalDateTime.now().minusDays(1));
//            expiradoRequest.setPublished(true);
//
//            Cupom cupomExpirado = Cupom.criar(
//                    expiradoRequest.getCode(),
//                    expiradoRequest.getDescription(),
//                    expiradoRequest.getDiscountValue(),
//                    expiradoRequest.getExpirationDate(),
//                    expiradoRequest.isPublished()
//            );
//
//            when(repository.findById(cupomId))
//                    .thenReturn(Optional.of(cupomExpirado));
//
//            CupomInvalidoException exception = assertThrows(
//                    CupomInvalidoException.class,
//                    () -> service.resgatarCupom(cupomId)
//            );
//
//            assertTrue(exception.getMessage().contains("Cupom não pode ser resgatado"));
//            verify(repository, never()).save(any());
//        }

        @Test
        @DisplayName("Deve lançar exceção ao resgatar cupom não publicado")
        void deveLancarExcecaoAoResgatarCupomNaoPublicado() {
            CreateCupomRequest naoPublicadoRequest = new CreateCupomRequest();
            naoPublicadoRequest.setCode("NPUB01");
            naoPublicadoRequest.setDescription("Não publicado");
            naoPublicadoRequest.setDiscountValue(BigDecimal.valueOf(10.0));
            naoPublicadoRequest.setExpirationDate(LocalDateTime.now().plusDays(30));
            naoPublicadoRequest.setPublished(false);

            Cupom cupomNaoPublicado = Cupom.criar(
                    naoPublicadoRequest.getCode(),
                    naoPublicadoRequest.getDescription(),
                    naoPublicadoRequest.getDiscountValue(),
                    naoPublicadoRequest.getExpirationDate(),
                    naoPublicadoRequest.isPublished()
            );

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomNaoPublicado));

            CupomInvalidoException exception = assertThrows(
                    CupomInvalidoException.class,
                    () -> service.resgatarCupom(cupomId)
            );

            verify(repository, never()).save(any());
        }
    }

    // ======================== TESTES DE PUBLICAÇÃO ========================

    @Nested
    @DisplayName("Testes de Publicação de Cupom")
    class TestPublicarCupom {

        @Test
        @DisplayName("Deve publicar um cupom com sucesso")
        void devePublicarCupomComSucesso() {
            CreateCupomRequest inativoRequest = new CreateCupomRequest();
            inativoRequest.setCode("INAT01");
            inativoRequest.setDescription("Inativo");
            inativoRequest.setDiscountValue(BigDecimal.valueOf(10.0));
            inativoRequest.setExpirationDate(LocalDateTime.now().plusDays(30));
            inativoRequest.setPublished(false);

            Cupom cupomInativo = Cupom.criar(
                    inativoRequest.getCode(),
                    inativoRequest.getDescription(),
                    inativoRequest.getDiscountValue(),
                    inativoRequest.getExpirationDate(),
                    inativoRequest.isPublished()
            );

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomInativo));
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cupom resultado = service.publicarCupom(cupomId);

            assertTrue(resultado.isPublished());
            assertEquals(CupomStatus.ACTIVE, resultado.getStatus());
            verify(repository).save(any(Cupom.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao publicar cupom já publicado")
        void deveLancarExcecaoAoPublicarCupomJaPublicado() {
            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> service.publicarCupom(cupomId)
            );

            assertTrue(exception.getMessage().contains("publicado"));
            verify(repository, never()).save(any());
        }
    }

    // ======================== TESTES DE DESATIVAÇÃO ========================

    @Nested
    @DisplayName("Testes de Desativação de Cupom")
    class TestDesativarCupom {

        @Test
        @DisplayName("Deve desativar um cupom com sucesso")
        void deveDesativarCupomComSucesso() {
            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomValido));
            when(repository.save(any(Cupom.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            Cupom resultado = service.desativarCupom(cupomId);

            assertEquals(CupomStatus.INACTIVE, resultado.getStatus());
            verify(repository).save(any(Cupom.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao desativar cupom não publicado")
        void deveLancarExcecaoAoDesativarCupomNaoPublicado() {
            CreateCupomRequest naoPublicadoRequest = new CreateCupomRequest();
            naoPublicadoRequest.setCode("NPUB02");
            naoPublicadoRequest.setDescription("Não publicado");
            naoPublicadoRequest.setDiscountValue(BigDecimal.valueOf(10.0));
            naoPublicadoRequest.setExpirationDate(LocalDateTime.now().plusDays(30));
            naoPublicadoRequest.setPublished(false);

            Cupom cupomNaoPublicado = Cupom.criar(
                    naoPublicadoRequest.getCode(),
                    naoPublicadoRequest.getDescription(),
                    naoPublicadoRequest.getDiscountValue(),
                    naoPublicadoRequest.getExpirationDate(),
                    naoPublicadoRequest.isPublished()
            );

            when(repository.findById(cupomId))
                    .thenReturn(Optional.of(cupomNaoPublicado));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> service.desativarCupom(cupomId)
            );

            assertTrue(exception.getMessage().contains("publicado"));
            verify(repository, never()).save(any());
        }
    }
}