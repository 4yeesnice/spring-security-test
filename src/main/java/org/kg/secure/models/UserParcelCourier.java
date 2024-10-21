package org.kg.secure.models;

import jakarta.persistence.*;
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
