package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.bartlomiejstepien.mcsm.service.ConfigService;

@Controller
@RequestMapping("/config")
public class ConfigController
{
    private final ConfigService configService;

    @Autowired
    public ConfigController(final ConfigService configService)
    {
        this.configService = configService;
    }

    @GetMapping
    public String showConfig()
    {
        return "config/config-home";
    }

    @GetMapping("/java")
    public String showJavaConfig(Model model)
    {
        model.addAttribute("javaList", this.configService.getAllJavaVersions());
        return "config/java/java-config";
    }
}
