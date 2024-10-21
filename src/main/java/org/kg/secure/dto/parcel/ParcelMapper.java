package org.kg.secure.dto.parcel;

import org.kg.secure.models.Parcel;
import org.springframework.stereotype.Component;

@Component
public class ParcelMapper {

    private ParcelDTO parcelDTO;

    public ParcelDTO toDTO(Parcel parcel) {
        ParcelDTO parcelDTO = new ParcelDTO();
        parcelDTO.setId(parcel.getId());
        parcelDTO.setUserId(parcel.getUser().getId());
        parcelDTO.setDeliveryAddress(parcel.getDeliveryAddress());
        parcelDTO.setPickupAddress(parcel.getPickupAddress());
        parcelDTO.setParcelStatus(parcel.getStatus());
        parcelDTO.setWeight(parcel.getWeight());
        parcelDTO.setCourierId(parcel.getCourier() != null ? parcel.getCourier().getId() : null);
        return parcelDTO;
    }
}
