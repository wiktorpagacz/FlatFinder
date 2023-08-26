package com.pagacz.flatflex.domain.repository;

import com.pagacz.flatflex.domain.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, OfferRepositoryCustom {

    Optional<Offer> findByLink(String link);
}
