package com.coupon.cupom.controller;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.request.CreateCupomRequest;
import com.coupon.cupom.request.CupomResponse;
import com.coupon.cupom.service.CupomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CupomController {

    private final CupomService service;

    public CupomController(CupomService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cupom> buscarPorId(@PathVariable UUID id) {
        Cupom cupom = service.buscarPorId(id);
        return ResponseEntity.ok(cupom);
    }

    @PostMapping
    public ResponseEntity<CupomResponse> salvarCupom(@Valid @RequestBody CreateCupomRequest request) {
        Cupom cupom = service.salvarCupom(request);
        CupomResponse response = toResponse(cupom);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CupomResponse> deleteCupom(@PathVariable UUID id) {
        Cupom cupom = service.deleteCupom(id);
        CupomResponse response = toResponse(cupom);
        return ResponseEntity.ok(response);
    }

    private CupomResponse toResponse(Cupom cupom) {
        CupomResponse response = new CupomResponse();
        response.setId(cupom.getId());
        response.setCode(cupom.getCode());
        response.setDescription(cupom.getDescription());
        response.setDiscountValue(cupom.getDiscountValue());
        response.setExpirationDate(cupom.getExpirationDate());
        response.setStatus(cupom.getStatus());
        response.setPublished(cupom.isPublished());
        response.setRedeemed(cupom.isRedeemed());
        return response;
    }
}
