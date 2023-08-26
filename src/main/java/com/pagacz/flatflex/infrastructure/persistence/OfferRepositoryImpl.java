package com.pagacz.flatflex.infrastructure.persistence;

import com.pagacz.flatflex.domain.model.Offer;
import com.pagacz.flatflex.domain.repository.OfferRepositoryCustom;
import com.pagacz.flatflex.domain.utils.CommonHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Repository
public class OfferRepositoryImpl implements OfferRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public OfferRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Offer> getNotWroteOffers(char status) {
        TypedQuery<Offer> offerTypedQuery = entityManager.createQuery("select o from offer o where o.writeToDocs =: status", Offer.class)
                .setParameter("status", status)
                .setMaxResults(CommonHelper.GOOGLE_API_QUERY_LIMIT);
        return offerTypedQuery.getResultList();
    }

    @Override
    public void updateErrorSendOffersToNotSend(char notSendStatus, char errorStatus) {
        entityManager.createQuery("update offer o set o.sendByEmail =: notSendStatus where o.sendByEmail =: sendError")
                .setParameter("notSendStatus", notSendStatus)
                .setParameter("sendError", errorStatus)
                .executeUpdate();
    }

    @Override
    public void updateErrorWriteToNotWrote(char notWroteStatus, char errorStatus) {
        entityManager.createQuery("update offer o set o.writeToDocs =: notWroteStatus where o.writeToDocs =: errorStatus")
                .setParameter("notWroteStatus", notWroteStatus)
                .setParameter("errorStatus", errorStatus)
                .executeUpdate();
    }

    @Override
    public List<Offer> getOffersBySendByEmailStatus(char offerStatus) {
        TypedQuery<Offer> offerTypedQuery = entityManager.createQuery("select o from offer o where o.sendByEmail =: status", Offer.class)
                .setParameter("status", offerStatus);
        return offerTypedQuery.getResultList();
    }

    @Override
    public void updateWroteOffers(List<Long> offerListId, char status, LocalDateTime writeToDocsTime) {
        entityManager.createQuery("update offer o set o.writeToDocs =: status, o.writeToDocsTime = :writeToDocsTime where o.id in :offerListIds")
                .setParameter("status", status)
                .setParameter("offerListIds", offerListId)
                .setParameter("writeToDocsTime", writeToDocsTime)
                .executeUpdate();
    }

    @Override
    public List<Offer> getLastWroteOffers(int limit, char status) {
        TypedQuery<Offer> offerTypedQuery = entityManager.createQuery("select o from offer o where o.writeToDocs =: status order by o.lastModifiedTime - o.writeToDocsTime desc", Offer.class)
                .setParameter("status", status)
                .setMaxResults(limit);
        return offerTypedQuery.getResultList();
    }

    @Override
    public void updateWriteToDocsTime(List<Long> updatedOffersId, LocalDateTime writeToDocsTime) {
        entityManager.createQuery("update offer set writeToDocsTime = :writeToDocsTime where id in :updatedOffersId")
                .setParameter("writeToDocsTime", writeToDocsTime)
                .setParameter("updatedOffersId", updatedOffersId)
                .executeUpdate();
    }
}
