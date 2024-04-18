package com.pagacz.flatflex.domain.repository;

import com.pagacz.flatflex.domain.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByLink(String link);

    @Query(nativeQuery = true, value = "SELECT * FROM OFFER o WHERE o.kafka_send = ?1 LIMIT 50")
    List<Offer> getOffersByStatus(char sendStatus);

    @Query(nativeQuery = true, value = "SELECT * FROM OFFER o WHERE o.kafka_send = ?1 AND o.last_modified_time < :thresholdDate")
    List<Offer> getOffersByStatusAndDate(char sendStatus, @Param("thresholdDate") LocalDateTime thresholdDate);
}
