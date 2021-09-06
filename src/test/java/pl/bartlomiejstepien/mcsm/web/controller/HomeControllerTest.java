package pl.bartlomiejstepien.mcsm.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.service.ServerServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest extends BaseIntegrationTest
{
    @MockBean
    private ServerServiceImpl serverService;

    @MockBean
    private AuthenticationFacade authenticationFacade;

    @Test
    @WithUserDetails(value = USER_USERNAME, userDetailsServiceBeanName = "userDetailsServiceTest")
    public void homePageShouldRequireAuthentication() throws Exception
    {
        when(authenticationFacade.getCurrentUser()).thenReturn(getTestUser());

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithUserDetails(value = USER_USERNAME, userDetailsServiceBeanName = "userDetailsServiceTest")
    public void homePageShouldShowUserServers() throws Exception
    {
        when(authenticationFacade.getCurrentUser()).thenReturn(getTestUser());
        when(serverService.getServersForUser(getTestUser().getId())).thenReturn(prepareServerDtos());

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.model().attribute("servers", prepareServerDtos()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private List<ServerDto> prepareServerDtos()
    {
        return Arrays.asList(new ServerDto(1, "Test1", "Test2"), new ServerDto(2, "Test3", "Test4"));
    }
}