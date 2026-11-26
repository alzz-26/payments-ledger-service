package com.alzz26.payments_ledger_service.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Database access for LedgerTransaction. Spring Data provides save, findById, findAll, etc.
@Repository
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {

}
