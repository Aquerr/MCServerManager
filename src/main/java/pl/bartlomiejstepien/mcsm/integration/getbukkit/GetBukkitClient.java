package pl.bartlomiejstepien.mcsm.integration.getbukkit;

import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;

import java.nio.file.Path;

public interface GetBukkitClient
{
    /**
     * Downloads spigot for given version.
     * @param version of spigot to download
     * @return {@link Path} to the downloaded file
     */
    Path downloadServer(int serverId, String version) throws CouldNotDownloadServerFilesException;
}
