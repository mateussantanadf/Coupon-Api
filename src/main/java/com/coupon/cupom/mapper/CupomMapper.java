package com.coupon.cupom.mapper;

import com.coupon.cupom.entity.Cupom;
import com.coupon.cupom.request.CupomResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CupomMapper {
    CupomResponse toResponse(Cupom cupom);
    Cupom toEntity(CupomResponse response);
}