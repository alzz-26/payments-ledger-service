package com.alzz26.payments_ledger_service.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Database access for LedgerEntry. Spring Data provides save, findById, findAll, etc.
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    // Returns the sum of all entry amounts for one account (its balance); 0 if it has no entries
    @Query("select coalesce(sum(e.amount), 0) from LedgerEntry e where e.account.id = :accountId")
    long getBalanceOf(@Param("accountId") Long accountId);
}
