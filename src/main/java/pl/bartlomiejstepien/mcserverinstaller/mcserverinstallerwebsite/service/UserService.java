package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.UserRepository;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.UserDto;

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

    public User find(final int id)
    {
        return this.userRepository.find(id);
    }

    public List<User> findAll()
    {
        return this.userRepository.findAll();
    }

    public void delete(final int id)
    {
        this.userRepository.delete(id);
    }

    public void save(final User user)
    {
        this.userRepository.save(UserDto.fromUser(user));
    }

    public User findByUsername(String username)
    {
        return this.userRepository.findByUsername(username);
    }
}
