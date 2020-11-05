package ru.louamphibi.nextbasin.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.louamphibi.nextbasin.entity.ConfirmationToken;

public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);

    ConfirmationToken findByEmployeeId(Long id);
}
