package pl.bartlomiejstepien.mcsm.controller.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.H2UserDetailsService;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ModpackRestController.class)
class ModpackRestControllerTest
{
    private static final Integer MODPACK_ID = 1;
    private static final Integer CATEGORY_ID = 2;

    private static final String MODPACK_DESCRIPTION = "This is a test <p>description</p>";

    private static final String GET_MODPACK_DESCRIPTION_URL = "http://localhost:8081/api/modpacks/{id}/description";
    private static final String POST_INSTALL_SERVER = "http://localhost:8081/api/modpacks/{id}/install";

    @MockBean
    private CurseForgeAPIService curseForgeAPIService;
    @MockBean
    private ServerService serverService;
    @MockBean
    private UserService userService;

//    @MockBean
//    private H2UserDetailsService h2UserDetailsServic;

    @Autowired
    private MockMvc mockMvc;

    public void setup()
    {

    }

//    @Test
    @WithUserDetails(userDetailsServiceBeanName = "h2UserDetailsService")
    public void getModpackDescriptionShouldReturnCorrectModpackDescription() throws Exception
    {
        final String url = GET_MODPACK_DESCRIPTION_URL.replace("{id}", String.valueOf(MODPACK_ID));
        when(curseForgeAPIService.getModpackDescription(MODPACK_ID)).thenReturn(MODPACK_DESCRIPTION);

        //then
        this.mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(MODPACK_DESCRIPTION));
    }

//    @Test
    @WithUserDetails(userDetailsServiceBeanName = "h2UserDetailsService")
    public void installServerStartsServerInstallation() throws Exception
    {
        final String url = POST_INSTALL_SERVER.replace("{id}", String.valueOf(MODPACK_ID));
        final Integer serverId = 1;

//        when(h2UserDetailsServic.loadUserByUsername("user")).thenReturn(new AuthenticatedUser(1, "user", "password"));

        when(this.serverService.installServer(any(AuthenticatedUser.class), MODPACK_ID)).thenReturn(serverId);

        //when
        this.mockMvc.perform(MockMvcRequestBuilders.post(url))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(1)));

        verify(this.serverService.installServer(any(), MODPACK_ID));
    }
}
