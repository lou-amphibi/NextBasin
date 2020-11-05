package ru.louamphibi.nextbasin.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.louamphibi.nextbasin.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

}
