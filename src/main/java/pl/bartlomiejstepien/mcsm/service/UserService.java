package pl.bartlomiejstepien.mcsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.List;

@Service
@Transactional
public class UserService
{
    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public UserDto find(final int id)
    {
        return this.userRepository.find(id);
    }

    public List<UserDto> findAll()
    {
        return this.userRepository.findAll();
    }

    public void delete(final int id)
    {
        this.userRepository.delete(id);
    }

    public void save(final UserDto userDto)
    {
        this.userRepository.save(User.fromUser(userDto));
    }

    public UserDto findByUsername(String username)
    {
        return this.userRepository.findByUsername(username).toUser();
    }
}
