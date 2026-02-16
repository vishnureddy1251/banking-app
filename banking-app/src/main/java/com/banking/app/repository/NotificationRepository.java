package com.banking.app.repository;

import com.banking.app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    List<Notification> findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(Long accountId);

    List<Notification> findByAccountIdAndCategory(Long accountId, String category);

    long countByAccountIdAndIsReadFalse(Long accountId);
}
