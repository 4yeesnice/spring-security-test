package org.kg.secure.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.exceptions.parcel.InvalidTokenException;
import org.kg.secure.exceptions.parcel.ParcelNotFoundException;
import org.kg.secure.exceptions.parcel.UserNotAllowedException;
import org.springframework.stereotype.Service;

// local packages
import org.kg.secure.dto.parcel.ParcelDTO;
import org.kg.secure.dto.parcel.ParcelMapper;
import org.kg.secure.dto.parcel.ParcelStatus;
import org.kg.secure.exceptions.parcel.UserNotFoundException;
import org.kg.secure.models.Parcel;
import org.kg.secure.models.UserParcelCourier;
import org.kg.secure.models.Users;
import org.kg.secure.repository.ParcelRepository;

// Utils
import java.util.UUID;
import java.util.Date;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class ParcelService {

    private ParcelRepository parcelRepository;
    private UserService userService;
    private UserParcelCourierService userParcelCourierService;
    private ParcelMapper parcelMapper;

    public void addParcel(Parcel parcel, String token) {
        UUID userId = decodeTokenAndFindUUID(token);
        Users user = userService.findUserByUUID(userId);

        if (user == null) {
            throw new UserNotFoundException("User not found for provided token");
        }

        parcel.setUser(user);
        parcel.setCreationDate(new Date());
        parcel.setStatus(Parcel.ParcelStatus.PENDING);
        parcelRepository.save(parcel);

        UserParcelCourier upc = new UserParcelCourier();
        upc.setUser(user);
        upc.setParcel(parcel);
        userParcelCourierService.add(upc);
    }



    public List<ParcelDTO> getAllUserParcel(String token) {
        UUID userId = decodeTokenAndFindUUID(token);
        return parcelRepository.findByUser_Id(userId).stream()
                .map(o -> parcelMapper.toDTO(o)).toList();
    }

    public Object getParcel(UUID parcelId, String token) {
        UUID userId = decodeTokenAndFindUUID(token);

        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ParcelNotFoundException("Parcel not found"));

        ParcelDTO parcelDTO = parcelMapper.toDTO(parcel);

        if (!userId.equals(parcelDTO.getUserId())) {
            throw new UserNotAllowedException("You are not allowed to view this parcel.");
        }
        return parcelDTO;

    }

    public List<ParcelDTO> getParcelByCourierId(String token) {
        UUID courierID = decodeTokenAndFindUUID(token);
        return parcelRepository.findAll()
                .stream()
                .filter( t -> t.getCourier() != null && t.getCourier().getId().equals(courierID))
                .map(o -> parcelMapper.toDTO(o))
                .toList();
    }

    public List<ParcelDTO> getAllParcelsWithoutCourier() {
        return parcelRepository.findAll().stream()
                .filter(t -> t.getCourier() == null && t.getStatus() == Parcel.ParcelStatus.PENDING)
                .map(o -> parcelMapper.toDTO(o))
                .toList();
    }


    public String cancelParcel(UUID parcelId, String token) {
        UUID userId = decodeTokenAndFindUUID(token);

        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ParcelNotFoundException("Parcel not found with ID: " + parcelId));

        if (!userId.equals(parcel.getUser().getId())) {
            throw new UserNotAllowedException("You are not allowed to cancel this parcel.");
        }

        parcel.setStatus(Parcel.ParcelStatus.CANCELED);
        parcelRepository.save(parcel);
        return "cancelled";
    }

    public String updateParcel(UUID parcelId, String token, ParcelStatus parcelStatus) {
        UUID userId = decodeTokenAndFindUUID(token);
        log.info("ID0 {}", userId);
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ParcelNotFoundException("Parcel not found with ID: " + parcelId));

        if (parcel.getCourier() == null || !userId.equals(parcel.getCourier().getId())) {
            throw new UserNotAllowedException("You are not allowed to update this parcel.");
        }

        parcel.setStatus(parcelStatus.getParcelStatus());
        parcelRepository.save(parcel);
        return "Parcel updated to %s".formatted(parcelStatus.getParcelStatus());
    }

    // Assigning to a courier
    public void updateParcel(UUID parcelId, String token) {
        UUID userId = decodeTokenAndFindUUID(token);
        Users courier = userService.findUserByUUID(userId);
        log.info("ID Courier: {}", userId);

        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new ParcelNotFoundException("Parcel not found with ID: " + parcelId));

        if (courier == null) {
            throw new UserNotFoundException("Courier not found for the provided token.");
        }

        parcel.setCourier(courier);
        parcelRepository.save(parcel);
    }

    private UUID decodeTokenAndFindUUID(String token) {
        try {
            String jwtToken = token.substring(7);
            String username = userService.decodeToken(jwtToken);
            log.info("Trying to find parcel: {}", username);
            Users user = (Users) userService.loadUserByUsername(username);
            if (user == null) {
                throw new UserNotFoundException("User not found for the provided token.");
            }
            return user.getId();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token format or token could not be decoded.");
        }
    }

}
