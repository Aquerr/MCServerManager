package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.model.UserRegUpdatePayload;

import java.util.List;

public interface UserService
{
    UserDto find(int id);

    List<UserDto> findAll();

    void delete(int id);

    UserDto findByUsername(String username);

    void register(UserRegUpdatePayload userRegUpdatePayload);

    void update(Integer userId, UserRegUpdatePayload userRegUpdatePayload);
}
