package com.momarious.query_service.repository;

import org.jooq.DSLContext;
import org.jooq.SelectJoinStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.momarious.query_service.event.Pret;

import lombok.RequiredArgsConstructor;

import static com.momarious.query_service.db.model.Tables.PRETS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class PretRepository {

        private static final int QUERY_HARD_LIMIT = 100;
        private final DSLContext dsl;

        public Optional<Pret> findById(UUID id) {
                return dsl.selectFrom(PRETS)
                                .where(PRETS.ID.eq(id))
                                .fetchOptionalInto(Pret.class);

        }

        public List<Pret> findByStatut(String statut) {
                return dsl.selectFrom(PRETS)
                                .where(PRETS.STATUT.eq(statut))
                                .fetchInto(Pret.class);
        }

        public Pret save(Pret pret) {
                return dsl.insertInto(PRETS)
                                .set(PRETS.ID, pret.getId())
                                .set(PRETS.STATUT, pret.getStatut())
                                .set(PRETS.CLIENT_ID, pret.getClientId())
                                .set(PRETS.DESCRIPTION, pret.getDescription())
                                .set(PRETS.DUREE_EN_MOIS, pret.getDureeEnMois())
                                .set(PRETS.MONTANT, pret.getMontant())
                                .set(PRETS.MONTANT_REMBOURSE, pret.getMontantRembourse())
                                .onConflict(PRETS.ID)
                                .doUpdate()
                                .set(PRETS.STATUT, pret.getStatut())
                                .set(PRETS.CLIENT_ID, pret.getClientId())
                                .set(PRETS.DESCRIPTION, pret.getDescription())
                                .set(PRETS.DUREE_EN_MOIS, pret.getDureeEnMois())
                                .set(PRETS.MONTANT, pret.getMontant())
                                .set(PRETS.MONTANT_REMBOURSE, pret.getMontantRembourse())
                                .returning()
                                .fetchOne()
                                .into(Pret.class);

        }

        public Page<Pret> findAll(Pageable pageable) {
                int effectiveLimit = Math.min(pageable.getPageSize(), QUERY_HARD_LIMIT);

                SelectJoinStep<org.jooq.Record> baseQuery = dsl.select()
                                .from(PRETS);

                int total = dsl.fetchCount(baseQuery);
                total = Math.min(total, QUERY_HARD_LIMIT);
                List<Pret> content = baseQuery
                                .orderBy(PRETS.CREATED_AT.asc())
                                .limit(effectiveLimit)
                                .offset(pageable.getOffset())
                                .fetchInto(Pret.class);

                return new PageImpl<>(content, PageRequest.of(
                                pageable.getPageNumber(),
                                effectiveLimit,
                                pageable.getSort()), total);

        }

}