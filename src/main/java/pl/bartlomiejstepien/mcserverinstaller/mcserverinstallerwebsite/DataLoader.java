package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.UserService;

@Component
public class DataLoader
{
    private final UserService userService;
    private final ServerService serverService;

    @Autowired
    public DataLoader(final UserService userService, final ServerService serverService)
    {
        this.userService = userService;
        this.serverService = serverService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData()
    {
        final User user = this.userService.find(1);
        if (user == null || !user.getUsername().equals("Nerdi"))
        {
            this.userService.save(new User(0, "Nerdi", "$2a$10$RsBi7zEwsAHxTgQO8cBX5Oe7iCPvIkGN3ichuibM9uGzmvx6TzFC6"));
        }
    }
}
