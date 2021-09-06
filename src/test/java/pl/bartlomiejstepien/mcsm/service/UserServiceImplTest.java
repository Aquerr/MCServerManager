package pl.bartlomiejstepien.mcsm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.UserConverter;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest
{
    private static final Integer USER_ID = 1;
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findShouldInvokeUserRepositoryAndConverterAndReturnUserDto()
    {
        // given
        User user = prepareUser();
        given(userRepository.find(USER_ID)).willReturn(user);
        given(userConverter.convertToDto(user)).willReturn(prepareUserDto());

        // when
        final UserDto userDto = userService.find(USER_ID);

        // then
        verify(userRepository).find(USER_ID);
        verify(userConverter).convertToDto(any(User.class));
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(USER_ID);
    }

    @Test
    void findAllShouldInvokeUserRepositoryAndReturnTheListOfUserDtos()
    {
        // given
        given(userRepository.findAll()).willReturn(Arrays.asList(prepareUser(), prepareUser()));
        given(userConverter.convertToDto(any(User.class))).willReturn(prepareUserDto(), prepareUserDto());

        // when
        final List<UserDto> userDtos = userService.findAll();

        // then
        verify(userConverter, times(2)).convertToDto(any(User.class));
        verify(userRepository).findAll();
        assertThat(userDtos).hasSize(2);
    }

    @Test
    void findAllShouldInvokeUserRepositoryAndReturnEmptyListIfNoUserIsFound()
    {
        // given
        given(userRepository.findAll()).willReturn(Collections.emptyList());

        // when
        final List<UserDto> userDtos = userService.findAll();

        // then
        verify(userRepository).findAll();
        assertThat(userDtos).isEmpty();
    }

    @Test
    void deleteShouldInvokeRepositoryAndDeletesTheUser()
    {
        // given
        // when
        userService.delete(USER_ID);

        // then
        verify(userRepository).delete(USER_ID);
    }

    @Test
    void saveShouldInvokeRepositoryAndSaveUser()
    {
        // given
        UserDto userDto = prepareUserDto();
        User user = prepareUser();
        given(userConverter.convertToUser(userDto)).willReturn(user);

        // when
        userService.save(userDto);

        // then
        verify(userRepository).save(user);
    }

    @Test
    void findByUsernameShouldInvokeRepositoryAndConverterAndReturnUserDto()
    {
        // given
        User user = prepareUser();
        given(userRepository.findByUsername(USERNAME)).willReturn(user);
        given(userConverter.convertToDto(user)).willReturn(prepareUserDto());

        // when
        final UserDto result = userService.findByUsername(USERNAME);

        // then
        verify(userRepository).findByUsername(USERNAME);
        verify(userConverter).convertToDto(user);
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void findByUsernameShouldInvokeRepositoryAndConverterAndReturnNullIfNoUserIsFound()
    {
        // given
        given(userRepository.findByUsername(USERNAME)).willReturn(null);

        // when
        final UserDto result = userService.findByUsername(USERNAME);

        // then
        verify(userRepository).findByUsername(USERNAME);
        verify(userConverter).convertToDto(null);
        assertThat(result).isNull();
    }

    private UserDto prepareUserDto()
    {
        return new UserDto(USER_ID, USERNAME, PASSWORD);
    }

    private User prepareUser()
    {
        return new User(USER_ID, USERNAME, PASSWORD);
    }
}