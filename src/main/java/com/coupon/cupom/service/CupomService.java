package com.coupon.cupom.service;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.repository.CupomRepository;
import com.coupon.cupom.request.CreateCupomRequest;
import com.coupon.cupom.util.CupomStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CupomService {

    private final CupomRepository repository;

    public CupomService(CupomRepository repository) {
        this.repository = repository;
    }

    public Cupom buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID do cupom não encontrado"));
    }

    public Cupom salvarCupom(CreateCupomRequest request) {
        Cupom cupom = new Cupom();
        cupom.setCode(filtrarCode(request.getCode()));
        cupom.setDescription(request.getDescription());
        cupom.setDiscountValue(request.getDiscountValue());
        cupom.setExpirationDate(request.getExpirationDate());
        cupom.setPublished(request.isPublished());
        cupom.setRedeemed(false);
        cupom.setStatus(CupomStatus.ACTIVE);

        return repository.save(cupom);
    }

    private String filtrarCode(String code) {
        return code.replace("-", "").toUpperCase();
    }
}
