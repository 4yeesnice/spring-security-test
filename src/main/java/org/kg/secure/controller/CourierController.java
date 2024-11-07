package org.kg.secure.controller;

// annotation
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

// packages
import org.kg.secure.dto.parcel.ParcelDTO;
import org.kg.secure.dto.parcel.ParcelStatus;
import org.kg.secure.exceptions.parcel.InvalidTokenException;
import org.kg.secure.exceptions.parcel.ParcelNotFoundException;
import org.kg.secure.exceptions.parcel.UserNotAllowedException;
import org.kg.secure.exceptions.parcel.UserNotFoundException;
import org.kg.secure.service.ParcelService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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


    @PostMapping("/parcels/{parcelID}")
    @PreAuthorize("hasAuthority('COURIER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> assignParcel(@RequestHeader("Authorization") String token, @PathVariable UUID parcelID) {
        parcelService.updateParcel(parcelID, token);
        return ResponseEntity.ok("Successfully assigned parcel to Courier");
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

    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<String> handleParcelNotFound(ParcelNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotAllowedException.class)
    public ResponseEntity<String> handleUserNotAllowed(UserNotAllowedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
