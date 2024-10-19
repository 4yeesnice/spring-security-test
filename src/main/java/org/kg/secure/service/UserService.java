package org.kg.secure.service;
import org.springframework.context.annotation.Bean;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.kg.secure.models.Users;
import org.kg.secure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private String secret = "gsync";


    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void addUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(Users userToUpdate, Users updatedUser) {
        if (updatedUser.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getUsername() != null) {
            userToUpdate.setUsername(updatedUser.getUsername());
        }
        userRepository.save(userToUpdate);
    }

    public void deleteUser(Users user) {
        userRepository.delete(user);
    }

    public void getUser(Users user) {
        userRepository.findByUsername(user.getUsername());
    }

    public void getAllUser(){
        userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public String decodeToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getSubject();

        List<? extends GrantedAuthority> authorities = decodedJWT
                .getClaims()
                .get("authorities")
                .asList(SimpleGrantedAuthority.class);

        return username;
    }

}
