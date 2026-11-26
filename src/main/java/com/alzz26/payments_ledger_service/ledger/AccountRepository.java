package com.alzz26.payments_ledger_service.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Database access for Account. Spring Data provides save, findById, findAll, etc.
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Finds the account with the given type and currency (used to find the SYSTEM account)
    Optional<Account> findByTypeAndCurrency(AccountType type, String currency);
}
