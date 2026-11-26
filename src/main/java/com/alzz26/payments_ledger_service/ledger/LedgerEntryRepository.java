package com.alzz26.payments_ledger_service.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    @Query("select coalesce(sum(e.amount), 0) from LedgerEntry e where e.account.id = :accountId")
    long getBalanceOf(@Param("accountId") Long accountId);
}