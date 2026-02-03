package com.coupon.cupom.service;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.exception.CupomNotFoundException;
import com.coupon.cupom.exception.CupomInvalidoException;
import com.coupon.cupom.repository.CupomRepository;
import com.coupon.cupom.request.CreateCupomRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço de gerenciamento de cupons
 * Contém a lógica de negócio relacionada a cupons
 */
@Service
@Transactional
public class CupomService {

    private final CupomRepository repository;

    public CupomService(CupomRepository repository) {
        this.repository = repository;
    }

    /**
     * Busca um cupom por ID
     * @param id UUID do cupom
     * @return Cupom encontrado
     * @throws CupomNotFoundException se não encontrar
     */
    @Transactional(readOnly = true)
    public Cupom buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new CupomNotFoundException("Cupom com ID " + id + " não encontrado"));
    }

    /**
     * Salva um novo cupom
     * @param request dados para criar o cupom
     * @return Cupom criado
     * @throws CupomInvalidoException se dados inválidos
     */
    public Cupom salvarCupom(CreateCupomRequest request) {
        String codeTratado = Cupom.tratarCode(request.getCode());

        validarCodigoUnico(codeTratado);

        Cupom cupom = Cupom.criar(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.isPublished()
        );

        return repository.save(cupom);
    }

    /**
     * Atualiza um cupom existente
     * @param id UUID do cupom
     * @param request novos dados
     * @return Cupom atualizado
     * @throws CupomNotFoundException se cupom não existir
     * @throws CupomInvalidoException se dados inválidos ou regra de negócio violada
     */
    public Cupom atualizarCupom(UUID id, CreateCupomRequest request) {
        Cupom cupom = buscarPorId(id);

        if (cupom.isDeleted()) {
            throw new CupomInvalidoException("Não é possível atualizar um cupom deletado");
        }
        if (cupom.podeSerResgatado() == false && cupom.isExpirado()) {
            throw new CupomInvalidoException("Não é possível atualizar um cupom expirado");
        }

        if (!cupom.getCode().equals(request.getCode())) {
            validarCodigoUnico(request.getCode());
        }

        cupom.atualizar(request.getDescription(), request.getDiscountValue());

        if (!cupom.getCode().equals(request.getCode())) {
            cupom = Cupom.criar(
                    request.getCode(),
                    request.getDescription(),
                    request.getDiscountValue(),
                    request.getExpirationDate(),
                    request.isPublished()
            );
        }

        return repository.save(cupom);
    }

    /**
     * Deleta logicamente um cupom (soft delete)
     * @param id UUID do cupom
     * @throws CupomNotFoundException se cupom não existir
     * @throws CupomInvalidoException se violou regra de negócio
     */
    public Cupom deleteCupom(UUID id) {
        Cupom cupom = buscarPorId(id);
        cupom.deletar();
        return repository.save(cupom);
    }

    /**
     * Resga um cupom
     * @param id UUID do cupom
     * @return Cupom resgatado
     * @throws CupomNotFoundException se cupom não existir
     * @throws CupomInvalidoException se não puder ser resgatado
     */
    public Cupom resgatarCupom(UUID id) {
        Cupom cupom = buscarPorId(id);
        
        if (!cupom.podeSerResgatado()) {
            throw new CupomInvalidoException("Cupom não pode ser resgatado");
        }
        
        cupom.resgatar();
        return repository.save(cupom);
    }

    /**
     * Publica um cupom inativo
     * @param id UUID do cupom
     * @return Cupom publicado
     */
    public Cupom publicarCupom(UUID id) {
        Cupom cupom = buscarPorId(id);
        cupom.publicar();
        return repository.save(cupom);
    }

    /**
     * Desativa um cupom publicado
     * @param id UUID do cupom
     * @return Cupom desativado
     */
    public Cupom desativarCupom(UUID id) {
        Cupom cupom = buscarPorId(id);
        cupom.desativar();
        return repository.save(cupom);
    }

    /**
     * Valida se o código já existe
     * @param code código do cupom
     * @throws CupomInvalidoException se código duplicado
     */
    private void validarCodigoUnico(String code) {
        if (repository.existsByCode(code)) {
            throw new CupomInvalidoException("Cupom com código " + code + " já existe");
        }
    }
}