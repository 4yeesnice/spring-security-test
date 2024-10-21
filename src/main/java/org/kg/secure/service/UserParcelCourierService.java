package org.kg.secure.service;

import lombok.AllArgsConstructor;
import org.kg.secure.models.UserParcelCourier;
import org.kg.secure.repository.ParcelRepository;
import org.kg.secure.repository.UserParcelCourierRepository;
import org.kg.secure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserParcelCourierService {

    private UserParcelCourierRepository userParcelCourierRepository;

    private UserRepository userRepository;

    private ParcelRepository parcelRepository;

    public void add(UserParcelCourier userParcelCourier) {
        userParcelCourierRepository.save(userParcelCourier);
    }




}
