package com.coupon.cupom.repository;

import com.coupon.cupom.entity.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CupomRepository extends JpaRepository<Cupom, UUID> {
}
