package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.UserRepository;

import java.util.List;

@Service
public class UserService
{
    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Transactional
    public User find(final int id)
    {
        return this.userRepository.find(id);
    }

    @Transactional
    public List<User> findAll()
    {
        return this.userRepository.findAll();
    }

    @Transactional
    public void delete(final int id)
    {
        this.userRepository.delete(id);
    }

    @Transactional
    public void save(final User user)
    {
        this.userRepository.save(user);
    }

    @Transactional
    public User findByUsername(String username)
    {
        return this.userRepository.findByUsername(username);
    }
}
