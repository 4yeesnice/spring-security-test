package org.kg.secure.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.dto.LoginDto;
import org.kg.secure.response.TokenResponse;
import org.kg.secure.service.JwtTokenCreate;
import org.kg.secure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
@Slf4j
public class GreetingController {

    private UserService userService;
    private AuthenticationManager authenticationManager;

    @GetMapping("/hi")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String Greetings(){
        return "Hello World";
    }

    @GetMapping("/hi2")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String Greetings2(){
        return "Hello World";
    }

    @PostMapping("/new")
    public String registry(@RequestBody Users user){

        userService.addUser(user);
        log.info("User registry: {}", user);
        return "User added";
    }

    @PostMapping("/login")
    public List<String> login(@RequestBody Users user){
        log.info("User login: {}", user.getUsername());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Users user1 = (Users) userService.loadUserByUsername(user.getUsername());



        JwtTokenCreate jwtTokenCreate = new JwtTokenCreate();
        TokenResponse tokenResponse = jwtTokenCreate.create(user1);
        return Arrays.asList(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
//        return "Succes";
    }




}
