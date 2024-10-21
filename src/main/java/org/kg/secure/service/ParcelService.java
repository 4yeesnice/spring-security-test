package org.kg.secure.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.dto.parcel.ParcelDTO;
import org.kg.secure.dto.parcel.ParcelMapper;
import org.kg.secure.dto.parcel.ParcelStatus;
import org.kg.secure.models.Parcel;
import org.kg.secure.models.UserParcelCourier;
import org.kg.secure.models.Users;
import org.kg.secure.repository.ParcelRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ParcelService {

    private ParcelRepository parcelRepository;
    private UserService userService;
    private UserParcelCourierService userParcelCourierService;
    private ParcelMapper parcelMapper;

    public void addParcel(Parcel parcel, String token) {
        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);
        log.info("Adding parcel to: {}", username);
        Users user = (Users) userService.loadUserByUsername(username);
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
        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);
        log.info("Trying to find parcel's of: {}", username);
        Users user = (Users) userService.loadUserByUsername(username);
        UUID id = user.getId();
        List<ParcelDTO> parcelsDTO = parcelRepository.findByUser_Id(id).stream()
                .map(o -> parcelMapper.toDTO(o)).toList();
        return parcelsDTO;

    }

    public Object getParcel(UUID parcelId, String token) {
        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);
        log.info("Trying to find parcel: {}", username);
        Users user = (Users) userService.loadUserByUsername(username);
        UUID userId = user.getId();

        Parcel parcel = parcelRepository.findById(parcelId).orElse(null);
        ParcelDTO parcelDTO = parcelMapper.toDTO(parcel);


        if(parcelDTO == null) {
            return "parcel not found";
        }
        if(userId != parcelDTO.getUserId()) {
            return "you're not allow to see this parcel";
        }
        return parcelDTO;

    }

    public List<ParcelDTO> getParcelByCourierId(String token) {
        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);
        log.info("Trying to find parcel: {}", username);
        Users user = (Users) userService.loadUserByUsername(username);
        UUID courierID = user.getId();
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
        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);
        log.info("Trying to find parcel: {}", username);
        Users user = (Users) userService.loadUserByUsername(username);
        UUID userId = user.getId();

        Parcel parcel = parcelRepository.findById(parcelId).orElse(null);

        if(parcel == null) {
            return "parcel not found";
        }
        if(userId != parcel.getUser().getId()) {
            return "you're not allow to cancel this parcel";
        }

        parcel.setStatus(Parcel.ParcelStatus.CANCELED);
        parcelRepository.save(parcel);
        return "cancelled";
    }

    public String updateParcel(UUID parcelId, String token, ParcelStatus parcelStatus) {
        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);
        log.info("Trying to find parcel: {}", username);
        Users user = (Users) userService.loadUserByUsername(username);
        UUID userId = user.getId();
        log.info("ID0 {}", userId);
        Parcel parcel = parcelRepository.findById(parcelId).orElse(null);

        if(parcel == null) {
            return "parcel not found";
        }
        if(userId != parcel.getCourier().getId()) {
            log.info("ID1 {}", userId);
            log.info("ID2 {}", parcel.getCourier().getId());
            return "you're not allow to update this parcel";
        }

        parcel.setStatus(parcelStatus.getParcelStatus());
        parcelRepository.save(parcel);
        return "Parcel updated to %s".formatted(parcelStatus.getParcelStatus());
    }
}
