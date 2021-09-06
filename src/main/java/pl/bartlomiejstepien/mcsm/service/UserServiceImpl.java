package pl.bartlomiejstepien.mcsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.UserConverter;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final UserConverter userConverter)
    {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserDto find(final int id)
    {
        return this.userConverter.convertToDto(this.userRepository.find(id));
    }

    @Override
    public List<UserDto> findAll()
    {
        return this.userRepository.findAll().stream().map(this.userConverter::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void delete(final int id)
    {
        this.userRepository.delete(id);
    }

    @Override
    public void save(final UserDto userDto)
    {
        this.userRepository.save(this.userConverter.convertToUser(userDto));
//        this.userRepository.save(User.fromUser(userDto));
    }

    @Override
    public UserDto findByUsername(String username)
    {
        return this.userConverter.convertToDto(this.userRepository.findByUsername(username));
    }
}
