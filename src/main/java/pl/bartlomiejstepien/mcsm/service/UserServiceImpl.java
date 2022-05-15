package pl.bartlomiejstepien.mcsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.dto.Role;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.exception.UsernameAreadyExistsException;
import pl.bartlomiejstepien.mcsm.domain.model.UserRegUpdatePayload;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.UserConverter;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository,
                           final UserConverter userConverter,
                           final PasswordEncoder passwordEncoder,
                           final AuthenticationFacade authenticationFacade)
    {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.passwordEncoder = passwordEncoder;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(final int id)
    {
        return this.userConverter.convertToDto(this.userRepository.find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll()
    {
        return this.userRepository.findAll().stream().map(this.userConverter::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findPaginated(Pageable pageable)
    {
        List<UserDto> userDtos = this.userRepository.findPaginated(pageable).stream()
                .map(this.userConverter::convertToDto)
                .collect(Collectors.toList());
        Long totalUsers = this.userRepository.countAllUsers();

        return new PageImpl<>(userDtos, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), totalUsers);
    }

    @Override
    @Transactional
    public void delete(final int id)
    {
        this.userRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username)
    {
        return this.userConverter.convertToDto(this.userRepository.findByUsername(username));
    }

    @Override
    @Transactional
    public void register(UserRegUpdatePayload userRegUpdatePayload)
    {
        if(findByUsername(userRegUpdatePayload.getUsername()) != null)
        {
            throw new UsernameAreadyExistsException(userRegUpdatePayload.getUsername() + " is already is use!");
        }

        User user = new User();
        user.setUsername(userRegUpdatePayload.getUsername());
        user.setPassword(this.passwordEncoder.encode(userRegUpdatePayload.getPassword()));
        user.setRoleId(Role.valueOf(userRegUpdatePayload.getRole()).getId());
        this.userRepository.register(user);
    }

    @Override
    @Transactional
    public void update(Integer userId, UserRegUpdatePayload userRegUpdatePayload)
    {
        final UserDto existingUser = find(userId);
        if (existingUser == null)
        {
            throw new UsernameNotFoundException("User not found!");
        }

        AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        User user = new User();
        user.setId(existingUser.getId());
        user.setUsername(userRegUpdatePayload.getUsername());
        user.setPassword(this.passwordEncoder.encode(userRegUpdatePayload.getPassword()));
        user.setServersIds(existingUser.getServerIds());
        if (authenticatedUser.getId().equals(existingUser.getId()))
        {
            user.setRoleId(existingUser.getRole().getId());
            this.userRepository.update(user);
        }
        else if(authenticatedUser.getRole().hasMorePrivilegesThan(existingUser.getRole()))
        {
            user.setRoleId(Role.valueOf(userRegUpdatePayload.getRole()).getId());
            this.userRepository.update(user);
        }
    }
}
