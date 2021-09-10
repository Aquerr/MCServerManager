package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.service.ConfigService;

import java.util.List;

@RestController
@RequestMapping(Routes.API_CONFIG)
public class ConfigRestController
{
    private final ConfigService configService;

    public ConfigRestController(final ConfigService configService)
    {
        this.configService = configService;
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
}
