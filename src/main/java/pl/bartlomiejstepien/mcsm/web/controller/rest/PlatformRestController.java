package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/platforms")
public class PlatformRestController
{
    @GetMapping
    public List<Platform> getPlatforms()
    {
        return Arrays.stream(Platform.values()).collect(Collectors.toList());
    }
}
