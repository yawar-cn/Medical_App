package com.medapp.inventory.repository;

import com.medapp.inventory.entity.ReservationStatus;
import com.medapp.inventory.entity.StockReservation;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {

    List<StockReservation> findByOrderIdAndStatus(UUID orderId, ReservationStatus status);

    void deleteByStatusAndExpiresAtBefore(ReservationStatus status, Instant cutoff);
}
