package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController
{
    private final UserService userService;
    private final ServerService serverService;

    public UsersController(final UserService userService, final ServerService serverService)
    {
        this.userService = userService;
        this.serverService = serverService;
    }

    @GetMapping("/{id}")
    public String getUserPage(final Model model, final @PathVariable("id") int userId)
    {
        UserDto userDto = this.userService.find(userId);
        List<ServerDto> servers = this.serverService.getServersForUser(userId);

        model.addAttribute("user", userDto);
        model.addAttribute("servers", servers);
        return "users/user-profile";
    }
}
