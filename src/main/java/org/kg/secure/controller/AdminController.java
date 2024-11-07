package org.kg.secure.controller;

import lombok.AllArgsConstructor;
import org.kg.secure.dto.courier.CourierUpdateDto;
import org.kg.secure.models.Users;
import org.kg.secure.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class AdminController {

    private UserService userService;

    @GetMapping("/all_users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllUsers(){
        List<Users> allUsers = userService.getAllUser();

        List<String> admin = allUsers.stream()
                .filter(users -> users.getRoles() == Users.Roles.ADMIN)
                .map(users -> "UUID: " + users.getId() +
                        "; Username: " + users.getUsername() + "; ROLE: " + users.getRoles() + "\n")
                .toList();

        List<String> user = allUsers.stream()
                .filter(users -> users.getRoles() == Users.Roles.USER)
                .map(users -> "UUID: " + users.getId() +
                        "; Username: " + users.getUsername() + "; ROLE: " + users.getRoles() + "\n")
                .toList();

        List<String> courier = allUsers.stream()
                .filter(users -> users.getRoles() == Users.Roles.COURIER)
                .map(users -> "UUID: " + users.getId() +
                        "; Username: " + users.getUsername() + "; ROLE: " + users.getRoles() + "\n")
                .toList();

        return ResponseEntity.ok("Admin: " + admin + "\nUser: " + user + "\nCourier: " + courier);
    }


    @GetMapping("/couriers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllCouriers(){
        List<Users> allUsers = userService.getAllUser();

        List<String> courier = allUsers.stream()
                .filter(users -> users.getRoles() == Users.Roles.COURIER)
                .map(users -> "UUID: " + users.getId() +
                        "; Username: " + users.getUsername() + "; ROLE: " + users.getRoles() + "\n")
                .toList();

        return ResponseEntity.ok("Courier: " + courier);
    }

    @PostMapping("/couriers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addCourier(Users users){
        userService.addUser(users);
        return ResponseEntity.ok("Courier is added. ID: " + users.getId() + "; Nickname: " + users.getUsername());
    }

    @PostMapping("/couriers/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateCourier(CourierUpdateDto updatedUsers, @PathVariable UUID id){
        userService.updateCourier(updatedUsers, id);
        return ResponseEntity.ok("Courier is updated.");
    }

    @DeleteMapping("/couriers/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCourier(@PathVariable UUID id){
        Users updateCourierToDe = userService.findUserByUUID(id);
        if (updateCourierToDe == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(updateCourierToDe);
        return ResponseEntity.ok("Courier is updated.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        Users user = userService.findUserByUUID(userId);
        userService.deleteUser(user);
        return ResponseEntity.ok("User with id " + userId + " deleted successfully");

    }
}
