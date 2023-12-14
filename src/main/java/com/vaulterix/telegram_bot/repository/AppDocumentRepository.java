package com.vaulterix.telegram_bot.repository;

import com.vaulterix.telegram_bot.model.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {
}
