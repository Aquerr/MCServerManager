package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.service.ConfigService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/config")
public class ConfigController
{
    private final ConfigService configService;
    private final UserService userService;

    @Autowired
    public ConfigController(final ConfigService configService, final UserService userService)
    {
        this.configService = configService;
        this.userService = userService;
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

    @GetMapping("/users")
    public String showUsersConfig(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size)
    {
        int currentPage = page.map((page1) -> page1 - 1).orElse(0);
        int pageSize = size.orElse(10);

        Page<UserDto> userPage = this.userService.findPaginated(PageRequest.of(currentPage, pageSize));

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("users", userPage);
        return "config/users/users-config";
    }
}
