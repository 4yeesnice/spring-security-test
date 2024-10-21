package org.kg.secure.dto.user;

import lombok.Data;
import org.kg.secure.models.Users;

@Data
public class AllUsersDto {

    private String username;
    private String nickname;
    private Users.Roles role;
}
