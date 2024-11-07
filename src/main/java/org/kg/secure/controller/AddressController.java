package org.kg.secure.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.models.Address;
import org.kg.secure.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/address")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/get_all")
    public ResponseEntity<List<Address>> getAll(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(addressService.getAddresses());
    }


}
