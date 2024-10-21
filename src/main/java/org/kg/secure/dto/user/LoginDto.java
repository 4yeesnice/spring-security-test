package org.kg.secure.dto.user;

import lombok.Data;

@Data
public class LoginDto {

    private String username;
    private String password;
    private String nickname;

}
