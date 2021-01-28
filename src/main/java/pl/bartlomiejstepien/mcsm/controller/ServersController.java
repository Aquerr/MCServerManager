package pl.bartlomiejstepien.mcsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.model.ModPack;
import pl.bartlomiejstepien.mcsm.model.ServerDto;
import pl.bartlomiejstepien.mcsm.model.UserDto;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcsm.service.ServerService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/servers")
public class ServersController
{
    private final CurseForgeAPIService curseForgeAPIService;
    private final ServerService serverService;

    @Autowired
    public ServersController(final CurseForgeAPIService curseForgeAPIService, final ServerService serverService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverService = serverService;
    }

    @GetMapping("/add-server")
    public String addServer(final Model model)
    {
        final List<ModPack> modPacks = this.curseForgeAPIService.getModpacks();
        model.addAttribute("modpacks", modPacks);
        return "servers/add-server";
    }

    @GetMapping("/{id}")
    public String showServer(final @PathVariable("id") int id, final Model model, final Authentication authentication)
    {
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();

        Optional<ServerDto> optionalServer = this.serverService.getServersForUser(authenticatedUser.getId()).stream()
                .filter(serverDto -> serverDto.getId() == id)
                .findFirst();

        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        model.addAttribute("server", optionalServer.get());
        return "servers/server-panel";
    }
}
