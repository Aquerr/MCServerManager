package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.integration.curseforge.CurseForgeService;
import pl.bartlomiejstepien.mcsm.web.controller.BaseIntegrationTest;
import pl.bartlomiejstepien.mcsm.service.ServerServiceImpl;
import pl.bartlomiejstepien.mcsm.service.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModpackRestControllerTest extends BaseIntegrationTest
{
    private static final Integer MODPACK_ID = 1;
    private static final Integer CATEGORY_ID = 2;
    private static final Integer SERVER_PACK_ID = 3;

    private static final String MODPACK_DESCRIPTION = "This is a test <p>description</p>";

    private static final String GET_MODPACK_DESCRIPTION_URL = "/api/modpacks/{id}/description";
    private static final String POST_INSTALL_SERVER = "/api/modpacks/{modpackId}/serverpacks/{serverPackId}/install";

    @MockBean
    private ServerManager serverManager;
    @MockBean
    private CurseForgeService curseForgeService;
    @MockBean
    private ServerServiceImpl serverService;
    @MockBean
    private UserServiceImpl userService;

    @InjectMocks
    private ModpackRestController modpackRestController;

    @Test
    @WithUserDetails(value = USER_USERNAME, userDetailsServiceBeanName = "userDetailsServiceTest")
    public void getModpackDescriptionShouldReturnCorrectModpackDescription() throws Exception
    {
        final String url = GET_MODPACK_DESCRIPTION_URL.replace("{id}", String.valueOf(MODPACK_ID));
        when(curseForgeService.getModpackDescription(MODPACK_ID)).thenReturn(MODPACK_DESCRIPTION);

        //then
        this.mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(MODPACK_DESCRIPTION));
    }

    @Test
    @WithUserDetails(value = USER_USERNAME, userDetailsServiceBeanName = "userDetailsServiceTest")
    public void installServerStartsServerInstallation() throws Exception
    {
        final String url = POST_INSTALL_SERVER.replace("{modpackId}", String.valueOf(MODPACK_ID))
                .replace("{serverPackId}", String.valueOf(SERVER_PACK_ID));
        final Integer serverId = 1;

        when(this.serverManager.installServerForModpack(any(AuthenticatedUser.class), anyInt(), anyInt())).thenReturn(serverId);

        //when
        this.mockMvc.perform(MockMvcRequestBuilders.post(url))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(String.valueOf(1)));

        verify(this.serverManager).installServerForModpack(getTestUser(), MODPACK_ID, SERVER_PACK_ID);
    }
}
