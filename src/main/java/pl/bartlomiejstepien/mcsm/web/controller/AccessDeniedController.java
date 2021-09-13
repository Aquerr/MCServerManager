package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController
{
    @GetMapping("/access-denied")
    public String accessDenied()
    {
        return "error/access-denied";
    }
}
