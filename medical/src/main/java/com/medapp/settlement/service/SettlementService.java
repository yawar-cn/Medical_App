package com.medapp.settlement.service;

import com.medapp.audit.service.AuditService;
import com.medapp.common.dto.PageResponse;
import com.medapp.common.event.OrderStatusChangedEvent;
import com.medapp.delivery.domain.DeliveryDomain;
import com.medapp.delivery.entity.DeliveryAssignment;
import com.medapp.order.entity.Order;
import com.medapp.order.entity.OrderStatus;
import com.medapp.order.service.OrderService;
import com.medapp.settlement.domain.SettlementDomain;
import com.medapp.settlement.dto.SettlementDto;
import com.medapp.settlement.dto.SettlementMarkPaidRequest;
import com.medapp.settlement.entity.SettlementRecord;
import com.medapp.settlement.entity.SettlementStatus;
import com.medapp.settlement.mapper.SettlementMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {

    private static final BigDecimal PLATFORM_COMMISSION_PCT = BigDecimal.valueOf(12);

    private final SettlementDomain settlementDomain;
    private final OrderService orderService;
    private final DeliveryDomain deliveryDomain;
    private final AuditService auditService;

    public SettlementService(SettlementDomain settlementDomain,
                             OrderService orderService,
                             DeliveryDomain deliveryDomain,
                             AuditService auditService) {
        this.settlementDomain = settlementDomain;
        this.orderService = orderService;
        this.deliveryDomain = deliveryDomain;
        this.auditService = auditService;
    }

    @EventListener
    @Transactional
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        if (event.to() == OrderStatus.DELIVERED) {
            generateSettlement(event.orderId(), event.userId());
        }

        if (event.to() == OrderStatus.REFUNDED && settlementDomain.existsByOrder(event.orderId())) {
            SettlementRecord record = settlementDomain.getByOrder(event.orderId());
            record.setStatus(SettlementStatus.ADJUSTED);
            record.setPharmacyPayout(BigDecimal.ZERO);
            record.setRiderPayout(BigDecimal.ZERO);
            settlementDomain.save(record);
            auditService.record(event.userId(), "REFUND_SETTLEMENT_ADJUSTED", "SETTLEMENT", record.getId(), "Settlement adjusted due to refund");
        }
    }

    @Transactional
    public SettlementDto generateSettlement(UUID orderId, UUID actorUserId) {
        if (settlementDomain.existsByOrder(orderId)) {
            return SettlementMapper.toDto(settlementDomain.getByOrder(orderId));
        }

        Order order = orderService.getOrderEntity(orderId);
        DeliveryAssignment assignment = null;
        try {
            assignment = deliveryDomain.assignmentByOrder(orderId);
        } catch (Exception ignored) {
        }

        BigDecimal gross = order.getTotalAmount();
        BigDecimal commissionAmount = gross.multiply(PLATFORM_COMMISSION_PCT).divide(BigDecimal.valueOf(100));
        BigDecimal riderPayout = assignment == null ? BigDecimal.ZERO : assignment.getEarningAmount();
        BigDecimal pharmacyPayout = gross.subtract(commissionAmount).subtract(riderPayout).max(BigDecimal.ZERO);

        SettlementRecord record = new SettlementRecord();
        record.setOrder(order);
        record.setPharmacyId(order.getPharmacy().getId());
        record.setRiderId(order.getRiderId());
        record.setGrossAmount(gross);
        record.setCommissionPercentage(PLATFORM_COMMISSION_PCT);
        record.setCommissionAmount(commissionAmount);
        record.setPharmacyPayout(pharmacyPayout);
        record.setRiderPayout(riderPayout);
        record.setStatus(SettlementStatus.PENDING);

        SettlementRecord saved = settlementDomain.save(record);
        auditService.record(actorUserId, "SETTLEMENT_CREATED", "SETTLEMENT", saved.getId(), "Settlement generated from delivered order");
        return SettlementMapper.toDto(saved);
    }

    @Transactional
    public SettlementDto markSettled(UUID actorUserId, UUID orderId, SettlementMarkPaidRequest request) {
        SettlementRecord record = settlementDomain.getByOrder(orderId);
        record.setStatus(SettlementStatus.SETTLED);
        record.setSettledAt(Instant.now());
        SettlementRecord saved = settlementDomain.save(record);
        auditService.record(actorUserId, "SETTLEMENT_MARKED_PAID", "SETTLEMENT", saved.getId(), request.reference());
        return SettlementMapper.toDto(saved);
    }

    public PageResponse<SettlementDto> all(int page, int size) {
        Page<SettlementDto> data = settlementDomain.all(PageRequest.of(page, size)).map(SettlementMapper::toDto);
        return new PageResponse<>(data.getContent(), data.getNumber(), data.getSize(), data.getTotalElements(), data.getTotalPages(), data.isLast());
    }

    public PageResponse<SettlementDto> byPharmacy(UUID pharmacyId, int page, int size) {
        Page<SettlementDto> data = settlementDomain.byPharmacy(pharmacyId, PageRequest.of(page, size)).map(SettlementMapper::toDto);
        return new PageResponse<>(data.getContent(), data.getNumber(), data.getSize(), data.getTotalElements(), data.getTotalPages(), data.isLast());
    }

    public PageResponse<SettlementDto> byRider(UUID riderId, int page, int size) {
        Page<SettlementDto> data = settlementDomain.byRider(riderId, PageRequest.of(page, size)).map(SettlementMapper::toDto);
        return new PageResponse<>(data.getContent(), data.getNumber(), data.getSize(), data.getTotalElements(), data.getTotalPages(), data.isLast());
    }

    public PageResponse<SettlementDto> byDateRange(LocalDate fromDate, LocalDate toDate, int page, int size) {
        Instant from = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = toDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Page<SettlementDto> data = settlementDomain.byDateRange(from, to, PageRequest.of(page, size)).map(SettlementMapper::toDto);
        return new PageResponse<>(data.getContent(), data.getNumber(), data.getSize(), data.getTotalElements(), data.getTotalPages(), data.isLast());
    }
}
