package org.kg.secure.service;

import lombok.AllArgsConstructor;
import org.kg.secure.models.Address;
import org.kg.secure.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AddressService {

    private AddressRepository addressRepository;

    public void addAddress(Address address) {
        addressRepository.save(address);
    }

    public void removeAddress(Address address) {
        addressRepository.delete(address);
    }

    public void updateAddress(Address address) {
        addressRepository.save(address);
    }

    public List<Address> getAddresses() {
        return addressRepository.findAll();
    }

    public Address getAddressById(UUID id) {
        return addressRepository.findById(id).orElse(null);
    }

}
