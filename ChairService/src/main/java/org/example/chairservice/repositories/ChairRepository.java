package org.example.chairservice.repositories;

import org.example.chairservice.entity.Chair;
import org.example.chairservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChairRepository extends JpaRepository<Chair, Long> {
    Optional<Chair> findByHolder_TelegramUsername(String telegramUsername);
    List<Chair> findByHolderIsNull();

    boolean findByHolder(User holder);

    boolean findByHolderId(Long holderId);

    boolean existsByHolder_Id(Long holderId);

    boolean existsByHolder_TelegramUsername(String holderTelegramUsername);
}
