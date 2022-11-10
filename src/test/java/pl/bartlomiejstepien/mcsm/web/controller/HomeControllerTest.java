package pl.bartlomiejstepien.mcsm.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.bartlomiejstepien.mcsm.SpringSecurityTestConfiguration;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.service.ServerService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static pl.bartlomiejstepien.mcsm.SpringSecurityTestConfiguration.USER_USERNAME;

@WebMvcTest(HomeController.class)
@Import(SpringSecurityTestConfiguration.class)
public class HomeControllerTest
{
    @MockBean
    private ServerService serverService;

    @MockBean
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails(value = USER_USERNAME)
    void homePageShouldRequireAuthentication() throws Exception
    {
        when(authenticationFacade.getCurrentUser()).thenReturn(SpringSecurityTestConfiguration.AUTHENTICATED_ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithUserDetails(value = USER_USERNAME)
    void homePageShouldShowUserServers() throws Exception
    {
        List<ServerDto> serverDtos = prepareServerDtos();
        when(authenticationFacade.getCurrentUser()).thenReturn(SpringSecurityTestConfiguration.AUTHENTICATED_ADMIN);
        when(serverService.getServersForUser(SpringSecurityTestConfiguration.AUTHENTICATED_ADMIN.getId())).thenReturn(serverDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.model().attribute("servers", hasSize(2)))
                .andExpect(MockMvcResultMatchers.model().attribute("servers", equalTo(serverDtos)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private List<ServerDto> prepareServerDtos()
    {
        return Arrays.asList(new ServerDto(1, "Test1", "Test2"), new ServerDto(2, "Test3", "Test4"));
    }
}