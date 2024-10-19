package org.kg.secure.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.models.Users;
import org.kg.secure.response.TokenResponse;
import org.kg.secure.service.JwtTokenCreate;
import org.kg.secure.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;

    private AuthenticationManager authenticationManager;

    @GetMapping("/hi")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String Greetings() {
        return "Hello ADMIN";
    }

    @GetMapping("/hi2")
    @PreAuthorize("hasAuthority('USER')")
    public String Greetings2() {
        return "Hello USER";
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        System.out.println("123");
        return ResponseEntity.ok("test");
    }

    @PostMapping("/registry")
    public String registry(@RequestBody Users user) {
        System.out.println("213");
        userService.addUser(user);
        log.info("User registry: {}", user);
        return "user";
    }

    @PostMapping("/login")
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

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody Users userNew, @RequestHeader("Authorization") String token) {
        try {

            String jwtToken = token.substring(7);
            String username = userService.decodeToken(jwtToken);

            log.info("User trying to update: {}", username);

            Users userToUpdate = (Users) userService.loadUserByUsername(username);

            userService.updateUser(userToUpdate, userNew);
            return ResponseEntity.status(HttpStatus.OK).body("User updated");

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", userNew.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred ");
        }

    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token){

        String jwtToken = token.substring(7);
        String username = userService.decodeToken(jwtToken);

        log.info("User trying to receive information: {}", username);

        Users user = (Users) userService.loadUserByUsername(username);

        return ResponseEntity.ok(user);
    }

}





