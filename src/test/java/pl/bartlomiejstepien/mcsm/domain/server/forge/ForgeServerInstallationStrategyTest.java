package pl.bartlomiejstepien.mcsm.domain.server.forge;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.server.ModpackDownloader;
import pl.bartlomiejstepien.mcsm.domain.server.ServerFileService;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationStatusMonitor;
import pl.bartlomiejstepien.mcsm.domain.server.ServerStartFileFinder;
import pl.bartlomiejstepien.mcsm.integration.curseforge.CurseForgeClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ForgeServerInstallationStrategyTest
{
    private static final Integer SERVER_ID = 3;
    private static final String USERNAME = "USERNAME";
    private static final Long MOD_PACK_ID = 1L;
    private static final String MOD_PACK_NAME = "Modpack Name";
    private static final String MOD_PACK_SUMMARY = "Modpack Summary";
    private static final String MOD_PACK_THUMBNAIL_URL = "Modpack Thumbnail Url";
    private static final String MOD_PACK_VERSION = "Modpack Version";
    private static final int SERVER_PACK_ID = 2;
    private static final String EMPTY_STRING = "";

    private static final String SERVER_DOWNLOAD_URL = "Server download url";
    private static final String FIXED_SERVER_DOWNLOAD_URL = "Server%20download%20url";

    @Mock
    private Config config;
    @Mock
    private ServerInstallationStatusMonitor serverInstallationStatusMonitor;
    @Mock
    private CurseForgeClient curseForgeClient;
    @Mock
    private ServerFileService serverFileService;
    @Mock
    private ServerStartFileFinder serverStartFileFinder;
    @Mock
    private ModpackDownloader modpackDownloader;
    @InjectMocks
    private ForgeServerInstallationStrategy forgeServerInstallationStrategy;

    @Test
    void installShouldThrowCouldNotInstallServerExceptionWhenServerFilesCouldNotBeDownloaded() throws CouldNotDownloadServerFilesException
    {
        ModPack modPack = prepareModpack();
        given(config.getServersDir()).willReturn(EMPTY_STRING);
        given(serverFileService.prepareFilePath(any())).willReturn(EMPTY_STRING);
        given(curseForgeClient.getModpack(MOD_PACK_ID)).willReturn(modPack);
        given(modpackDownloader.downloadServerFilesForModpack(SERVER_ID, modPack, SERVER_PACK_ID)).willThrow(CouldNotDownloadServerFilesException.class);

        Throwable throwable = null;
        try(MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
            MockedStatic<FileSystemUtils> mockedFileSystemUtils = Mockito.mockStatic(FileSystemUtils.class))
        {
            mockedFiles.when(() -> Files.exists(any())).thenReturn(false);
            mockedFileSystemUtils.when(() -> FileSystemUtils.deleteRecursively(any(Path.class))).thenReturn(false);

            throwable = Assertions.catchThrowable(() -> forgeServerInstallationStrategy.install(SERVER_ID, prepareForgeModpackInstallationRequest()));
        }
        assertThat(throwable).isInstanceOf(CouldNotInstallServerException.class);
    }

    private ForgeModpackInstallationRequest prepareForgeModpackInstallationRequest()
    {
        return new ForgeModpackInstallationRequest(USERNAME, MOD_PACK_ID, SERVER_PACK_ID);
    }

    private ModPack prepareModpack()
    {
        return new ModPack(MOD_PACK_ID, MOD_PACK_NAME, MOD_PACK_SUMMARY, MOD_PACK_THUMBNAIL_URL, MOD_PACK_VERSION, Collections.emptyList());
    }
}