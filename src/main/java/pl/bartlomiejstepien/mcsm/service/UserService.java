package pl.bartlomiejstepien.mcsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.UserConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService
{
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Autowired
    public UserService(final UserRepository userRepository, final UserConverter userConverter)
    {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    public UserDto find(final int id)
    {
        return this.userConverter.convertToDto(this.userRepository.find(id));
    }

    public List<UserDto> findAll()
    {
        return this.userRepository.findAll().stream().map(this.userConverter::convertToDto).collect(Collectors.toList());
    }

    public void delete(final int id)
    {
        this.userRepository.delete(id);
    }

    public void save(final UserDto userDto)
    {
        this.userRepository.save(userConverter.convertToDs(userDto));
    }

    public UserDto findByUsername(String username)
    {
        return this.userConverter.convertToDto(this.userRepository.findByUsername(username));
    }
}
