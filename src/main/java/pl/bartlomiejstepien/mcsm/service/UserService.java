package pl.bartlomiejstepien.mcsm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.model.UserRegUpdatePayload;

import java.util.List;

public interface UserService
{
    UserDto find(int id);

    List<UserDto> findAll();

    @Transactional(readOnly = true)
    Page<UserDto> findPaginated(Pageable pageable);

    void delete(int id);

    UserDto findByUsername(String username);

    void register(UserRegUpdatePayload userRegUpdatePayload);

    void update(Integer userId, UserRegUpdatePayload userRegUpdatePayload);
}
