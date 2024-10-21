package org.kg.secure.dto.user;

import lombok.Data;
import org.kg.secure.models.Address;
import org.kg.secure.models.Parcel;

import java.util.List;

@Data
public class UserGetInfoDto {

    private String username;
    private String email;
    private String phone;
    private Address address;
    private List<Parcel> parcel;


}
