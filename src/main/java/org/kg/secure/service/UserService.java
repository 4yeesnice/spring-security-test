package org.kg.secure.service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.kg.secure.dto.courier.CourierUpdateDto;
import org.kg.secure.dto.user.UserGetInfoDto;
import org.kg.secure.models.Address;
import org.kg.secure.models.Users;
import org.kg.secure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
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


    public void addAddress(Address address, String token) {
        String jwtToken = token.substring(7);
        String username = decodeToken(jwtToken);
        Users user = (Users) loadUserByUsername(username);
        user.setAddress(address);
        userRepository.save(user);
    }

    public Address getAddress(String token) {
        String jwtToken = token.substring(7);
        String username = decodeToken(jwtToken);
        Users user = (Users) loadUserByUsername(username);
        return user.getAddress();
    }


    public void updateUser(Users updatedUser, String token) {
        String jwtToken = token.substring(7);
        String username = decodeToken(jwtToken);

        log.info("User trying to update: {}", username);

        Users userToUpdate = (Users) loadUserByUsername(username);

        if (updatedUser.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getUsername() != null) {
            userToUpdate.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getPhone() != null) {
            userToUpdate.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getEmail() != null) {
            userToUpdate.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getAddress() != null) {
            userToUpdate.setAddress(updatedUser.getAddress());
        }
        userRepository.save(userToUpdate);
    }

    public UserGetInfoDto getMe(String token){
        String jwtToken = token.substring(7);
        String username = decodeToken(jwtToken);
        log.info("User trying to receive information: {}", username);


        Users user = (Users) loadUserByUsername(username);
        UserGetInfoDto userinfo = new UserGetInfoDto();
        userinfo.setUsername(user.getUsername());
        userinfo.setEmail(user.getEmail());
        userinfo.setPhone(user.getPhone());
        userinfo.setAddress(user.getAddress());
        userinfo.setParcel(user.getParcel());
        return userinfo;
    }


    public String updateCourier(CourierUpdateDto updatedUser, UUID id) {
        Users userToUpdate = (Users) findUserByUUID(id);
        if (userToUpdate == null) {
            return "Not found";
        }
        if (updatedUser.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getUsername() != null) {
            userToUpdate.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            userToUpdate.setEmail(updatedUser.getEmail());
        }
        userRepository.save(userToUpdate);
        return "User updated";

    }

    public void deleteUser(Users user) {
        userRepository.delete(user);
    }

    public void getUser(Users user) {
        userRepository.findByUsername(user.getUsername());
    }

    public List<Users> getAllUser(){
        return userRepository.findAll();
    }

    public Users findUserByUUID(UUID uuid) {
        return userRepository.findById(uuid).get();
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
