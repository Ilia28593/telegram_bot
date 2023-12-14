package com.vaulterix.telegram_bot.repository;

import com.vaulterix.telegram_bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByChatIdAndStatusFalse(Long chatId);

    Optional<User> findByChatId(Long chatId);

}
