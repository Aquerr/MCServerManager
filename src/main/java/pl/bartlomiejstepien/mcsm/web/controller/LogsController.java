package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logs")
public class LogsController
{
    @GetMapping
    public String logs()
    {
        return "config/logs/logs";
    }
}
