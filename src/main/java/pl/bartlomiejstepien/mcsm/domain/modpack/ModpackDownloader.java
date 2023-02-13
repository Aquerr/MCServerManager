package pl.bartlomiejstepien.mcsm.domain.modpack;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.server.ServerFileService;
import pl.bartlomiejstepien.mcsm.integration.curseforge.CurseForgeClient;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@AllArgsConstructor
public class ModpackDownloader
{
    private final Config config;
    private final CurseForgeClient curseForgeClient;
    private ServerFileService serverFileService;

    public Path downloadServerFilesForModpack(int serverId, final ModPack modPack, int serverPackId) throws CouldNotDownloadServerFilesException
    {
        Path downloadPath = getDownloadPath(modPack);

        if (isModPackAlreadyDownloaded(downloadPath))
        {
            return downloadPath;
        }

        String serverDownloadUrl = this.curseForgeClient.getServerDownloadUrl(modPack.getId(), serverPackId);

        //TODO: Fix url. Parenthesis "(" and ")" still not work
        serverDownloadUrl = normalizeUrl(serverDownloadUrl);
        return this.curseForgeClient.downloadServerFile(serverId, modPack, serverDownloadUrl, downloadPath);
    }

    private boolean isModPackAlreadyDownloaded(final Path downloadPath)
    {
        return Files.exists(downloadPath);
    }

    private String normalizeUrl(String serverDownloadUrl)
    {
        return serverDownloadUrl.replace(" ", "%20");
    }

    private Path getDownloadPath(ModPack modPack)
    {
        return this.config.getDownloadsDirPath().resolve(serverFileService.prepareFilePath(modPack.getName() + "_" + modPack.getVersion() + ".zip"));
    }
}
