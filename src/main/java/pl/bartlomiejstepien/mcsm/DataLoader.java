package pl.bartlomiejstepien.mcsm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.dto.UserDto;
import pl.bartlomiejstepien.mcsm.service.UserService;

@Component
public class DataLoader
{
    private final UserService userService;

    @Autowired
    public DataLoader(final UserService userService)
    {
        this.userService = userService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData()
    {
        final UserDto userDto = this.userService.find(1);
        if (userDto == null || !userDto.getUsername().equals("Nerdi"))
        {
            this.userService.save(new UserDto(0, "Nerdi", "$2a$10$RsBi7zEwsAHxTgQO8cBX5Oe7iCPvIkGN3ichuibM9uGzmvx6TzFC6"));
        }
    }
}
