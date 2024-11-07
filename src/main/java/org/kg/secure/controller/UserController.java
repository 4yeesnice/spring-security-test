package org.kg.secure.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.kg.secure.dto.parcel.ParcelDTO;
import org.kg.secure.dto.user.UserGetInfoDto;
import org.kg.secure.exceptions.parcel.InvalidTokenException;
import org.kg.secure.exceptions.parcel.ParcelNotFoundException;
import org.kg.secure.exceptions.parcel.UserNotAllowedException;
import org.kg.secure.exceptions.parcel.UserNotFoundException;
import org.kg.secure.models.Address;
import org.kg.secure.models.Parcel;
import org.kg.secure.models.Users;
import org.kg.secure.response.TokenResponse;
import org.kg.secure.service.AddressService;
import org.kg.secure.service.JwtTokenCreate;
import org.kg.secure.service.ParcelService;
import org.kg.secure.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class UserController {

    private ParcelService parcelService;

    private UserService userService;

    private AuthenticationManager authenticationManager;

    private AddressService addressService;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String Greetings() {
        return "Hello ADMIN";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public String Greetings2() {
        return "Hello USER";
    }

    @GetMapping("/courier")
    @PreAuthorize("hasAuthority('COURIER')")
    public String Greetings3() {
        return "Hello COURIER";
    }


    @PostMapping("/sign_in")
    public String registry(@RequestBody Users user) {
        userService.addUser(user);
        log.info("User registry: {}", user);
        return "User %s added successfully. Welcome %s!".formatted(user.getUsername(), user.getAuthorities());
    }

    @PostMapping("/log_in")
    public ResponseEntity<?> login(@RequestBody Users user) {
        try {
            log.info("User login: {}", user.getUsername());

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Users user1 = (Users) userService.loadUserByUsername(user.getUsername());

            JwtTokenCreate jwtTokenCreate = new JwtTokenCreate();
            TokenResponse tokenResponse = jwtTokenCreate.create(user1);
            return ResponseEntity.ok(Arrays.asList(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken()));

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            log.error("An error occurred during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }

    }

    // Update user
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody Users userNew, @RequestHeader("Authorization") String token) {
        try {

            userService.updateUser(userNew, token);
            return ResponseEntity.status(HttpStatus.OK).body("User updated");

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", userNew.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
        }
    }

    // Receive information
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token){
        UserGetInfoDto userGetInfoDto = userService.getMe(token);
        return ResponseEntity.ok(userGetInfoDto);
    }

    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(@RequestBody Address address, @RequestHeader("Authorization") String token){
        addressService.addAddress(address);
        userService.addAddress(address, token);
        return ResponseEntity.ok("Address added");
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddress(@RequestHeader("Authorization") String token){
        Address address = userService.getAddress(token);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id){
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found or doesn't exist");
        }
        addressService.removeAddress(address);
        return ResponseEntity.ok("Address removed");
    }

    @PostMapping("/parcel")
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> addParcel(@RequestBody Parcel parcel, @RequestHeader("Authorization") String token){
        parcelService.addParcel(parcel, token);
        return ResponseEntity.ok("Parcel added");
    }

    @GetMapping("/parcel")
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> getParcel(@RequestHeader("Authorization") String token){
        List<ParcelDTO> listParcels = parcelService.getAllUserParcel(token);
        return ResponseEntity.ok(listParcels);
    }

    @GetMapping("/parcel/{id}")
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> getParcel(@PathVariable UUID id, @RequestHeader("Authorization") String token){
        Object parcel = parcelService.getParcel(id, token);
        return ResponseEntity.ok(parcel);
    }

    @PutMapping("/parcel/{id}/cancel")
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    public ResponseEntity<?> cancelParcel(@PathVariable UUID id, @RequestHeader("Authorization") String token){
        String answer = parcelService.cancelParcel(id, token);
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





