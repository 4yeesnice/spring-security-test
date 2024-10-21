package org.kg.secure.repository;

import org.kg.secure.models.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

    List<Parcel> findByUser_Id(UUID userId);
}
