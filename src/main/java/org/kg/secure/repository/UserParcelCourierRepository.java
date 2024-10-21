package org.kg.secure.repository;

import org.kg.secure.models.UserParcelCourier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserParcelCourierRepository extends JpaRepository<UserParcelCourier, UUID> {
}
