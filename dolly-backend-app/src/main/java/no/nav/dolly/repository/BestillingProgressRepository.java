package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import no.nav.dolly.domain.jpa.BestillingProgress;

public interface BestillingProgressRepository extends Repository<BestillingProgress, Long> {

    Optional<BestillingProgress> save(BestillingProgress bestillingProgress);

    List<BestillingProgress> findByBestillingId(Long bestillingId);

    void deleteByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from BestillingProgress bp where bp.bestillingId in (select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    int deleteByIdent(String ident);

    @Query("")
    List<BestillingProgress> findByIdent(String ident);
}
