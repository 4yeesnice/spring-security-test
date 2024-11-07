package org.kg.secure.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "parcel_delivery")
public class UserParcelCourier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Users courier;

    @ManyToOne
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

}
