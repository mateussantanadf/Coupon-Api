package com.coupon.cupom.controller;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.mapper.CupomMapper;
import com.coupon.cupom.request.CreateCupomRequest;
import com.coupon.cupom.request.CupomResponse;
import com.coupon.cupom.service.CupomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Cupom", description = "Endpoints de gerenciamento de cupons")
public class CupomController {

    private final CupomService service;
    private CupomMapper mapper;

    public CupomController(CupomService service) {
        this.service = service;
    }

    @Operation(summary = "Buscar cupom por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Cupom> buscarPorId(@Parameter(description = "ID do cupom") @PathVariable UUID id) {
        Cupom cupom = service.buscarPorId(id);
        return ResponseEntity.ok(cupom);
    }

    @Operation(
            summary = "Criar cupom",
            description = "Cria um novo cupom"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    @PostMapping
    public ResponseEntity<CupomResponse> salvarCupom(
            @RequestBody(
                    required = true,
                    description = "Dados necessários para criar o cupom",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCupomRequest.class)
                    )
            )
            @Valid CreateCupomRequest request) {
        Cupom cupom = service.salvarCupom(request);
        CupomResponse response = mapper.toResponse(cupom);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Deletar cupom por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<CupomResponse> deleteCupom(@Parameter(description = "ID do cupom") @PathVariable UUID id) {
        Cupom cupom = service.deleteCupom(id);
        CupomResponse response = mapper.toResponse(cupom);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar cupom",
            description = "Atualiza um novo cupom"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CupomResponse> atualizarCupom(
            @PathVariable UUID id,
            @RequestBody(
                    required = true,
                    description = "Dados para atualizar um cupom",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCupomRequest.class)
                    )
            )
            @Valid CreateCupomRequest request) {
        Cupom cupom = service.atualizarCupom(id, request);
        CupomResponse response = mapper.toResponse(cupom);
        return ResponseEntity.ok(response);
    }
}
