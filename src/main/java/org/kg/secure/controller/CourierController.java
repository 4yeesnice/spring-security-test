package org.kg.secure.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.dto.parcel.ParcelDTO;
import org.kg.secure.dto.parcel.ParcelStatus;
import org.kg.secure.models.Parcel;
import org.kg.secure.models.Users;
import org.kg.secure.service.ParcelService;
import org.kg.secure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class CourierController {

    private ParcelService parcelService;

    @GetMapping("/parcels")
    @PreAuthorize("hasAuthority('COURIER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllParcels() {
        List<ParcelDTO> parcelList = parcelService.getAllParcelsWithoutCourier();
        return ResponseEntity.ok(parcelList);
    }


    @GetMapping("/my_parcels")
    @PreAuthorize("hasAuthority('COURIER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> getMyParcels(@RequestHeader("Authorization") String token) {
        List<ParcelDTO> courierParcel = parcelService.getParcelByCourierId(token);
        if (courierParcel.isEmpty()) {
            return ResponseEntity.ok("You don't have any active parcels");
        }
        return ResponseEntity.ok(courierParcel);
    }


    @PutMapping("/parcels/{id}/status")
    @PreAuthorize("hasAuthority('COURIER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> updateParcels(@RequestHeader("Authorization") String token,
                                           @RequestBody ParcelStatus parcelStatus,
                                           @PathVariable UUID id) {
        String answer = parcelService.updateParcel(id, token, parcelStatus);
        return ResponseEntity.ok(answer);
    }
}
