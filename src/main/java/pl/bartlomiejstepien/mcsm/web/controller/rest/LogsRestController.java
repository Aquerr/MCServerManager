package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.domain.mcsm.McsmLogsService;

import java.util.List;

@RestController
@RequestMapping(Routes.API_LOGS)
public class LogsRestController
{
    private final McsmLogsService mcsmLogsService;

    public LogsRestController(McsmLogsService mcsmLogsService)
    {
        this.mcsmLogsService = mcsmLogsService;
    }

    @GetMapping
    public List<String> getMcsmLogs()
    {
        return this.mcsmLogsService.getMcsmLogs();
    }
}
