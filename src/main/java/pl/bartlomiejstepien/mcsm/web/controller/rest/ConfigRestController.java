package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.exception.UsernameAreadyExistsException;
import pl.bartlomiejstepien.mcsm.service.ConfigService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(Routes.API_CONFIG)
public class ConfigRestController
{
    private final ConfigService configService;
    private final AuthenticationFacade authenticationFacade;
    private final UserService userService;

    public ConfigRestController(final ConfigService configService,
                                final AuthenticationFacade authenticationFacade,
                                final UserService userService)
    {
        this.configService = configService;
        this.authenticationFacade = authenticationFacade;
        this.userService = userService;
    }

    @GetMapping("/java")
    public List<JavaDto> getJavaVersions()
    {
        return this.configService.getAllJavaVersions();
    }

    @PostMapping("/java")
    public String saveJava(final @RequestBody JavaDto javaDto)
    {
        this.configService.addJava(javaDto);
        return "Success";
    }

    @DeleteMapping("/java/{id}")
    public String deleteJava(final @PathVariable("id") int javaId)
    {
        this.configService.deleteJava(javaId);
        return "Success";
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(final @PathVariable("id") Integer userId, final @RequestBody UserDto userDto)
    {
        final UserDto existingUser = this.userService.find(userId);
        if (existingUser == null)
        {
            throw new RuntimeException("User not found!");
        }

        this.userService.save(userDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users")
    public ResponseEntity<?> saveUser(final @RequestBody UserDto userDto)
    {
        if(this.userService.findByUsername(userDto.getUsername()) != null)
        {
            throw new UsernameAreadyExistsException(userDto.getUsername() + " is already is use!");
        }
        this.userService.save(userDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(final @PathVariable("id") Integer userId)
    {
        if(Objects.equals(this.authenticationFacade.getCurrentUser().getId(), userId))
        {
            throw new RuntimeException("Cannot delete itself.");
        }

        this.userService.delete(userId);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(final @PathVariable("id") Integer userId)
    {
        UserDto userDto = this.userService.find(userId);
        userDto.setPassword("");
        return userDto;
    }
}
