package org.kg.secure.dto.parcel;

import lombok.Data;
import org.kg.secure.models.Address;
import org.kg.secure.models.Parcel;
import org.kg.secure.models.Users;

import java.util.Date;
import java.util.UUID;

@Data
public class ParcelDTO {

    private UUID id;
    private UUID userId;
    private Address pickupAddress;
    private Address deliveryAddress;
    private Parcel.ParcelStatus parcelStatus;
    private Date creationDate;
    private Double weight;
    private UUID courierId;

}
