package com.medapp.medicine.repository;

import com.medapp.medicine.entity.Medicine;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {

    Optional<Medicine> findByIdAndDeletedAtIsNull(UUID id);

    @Query("""
            select m from Medicine m
            where m.deletedAt is null
              and m.active = true
              and (:q is null or lower(m.name) like lower(concat('%', :q, '%'))
              or lower(m.genericName) like lower(concat('%', :q, '%')))
            """)
    Page<Medicine> searchActive(String q, Pageable pageable);
}
