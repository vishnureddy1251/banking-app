package com.banking.app.repository;

import com.banking.app.model.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountEventRepository extends JpaRepository<AccountEvent, Long> {
}
