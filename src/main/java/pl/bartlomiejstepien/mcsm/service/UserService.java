package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import java.util.List;

public interface UserService
{
    UserDto find(int id);

    List<UserDto> findAll();

    void delete(int id);

    void save(UserDto userDto);

    UserDto findByUsername(String username);
}
