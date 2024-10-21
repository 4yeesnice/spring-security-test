package org.kg.secure.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Users courier;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @ManyToOne
    private Address pickupAddress;
    @ManyToOne
    private Address deliveryAddress;

    private Date creationDate;
    private Double weight;

    private ParcelStatus status;



    public enum ParcelStatus {
        PENDING, IN_PROGRESS, DELIVERED, CANCELED
    }


}
