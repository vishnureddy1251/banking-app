package com.banking.app.repository;

import com.banking.app.model.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountEventRepository extends JpaRepository<AccountEvent, Long> {

    List<AccountEvent> findByAccountIdOrderBySequenceNumberAsc(Long accountId);

    AccountEvent findTopByAccountIdOrderBySequenceNumberDesc(Long accountId);

    @Query("SELECT COALESCE(MAX(e.sequenceNumber), 0) + 1 FROM AccountEvent e WHERE e.accountId = ?1")
    Long getNextSequenceNumber(Long accountId);

    List<AccountEvent> findByAccountIdAndEventTypeOrderBySequenceNumberAsc(Long accountId, String eventType);

    long countByAccountId(Long accountId);
}
