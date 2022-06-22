package pl.bartlomiejstepien.mcsm.integration.curseforge;

import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.model.ServerPack;

import java.nio.file.Path;
import java.util.List;

public interface CurseForgeClient
{
    List<ModPack> getModpacks(int categoryId, String modpackName, String version, int count, int startIndex);

    int getLatestServerFileId(Long modpackId);

    ModPack getModpack(Long id);

    String getModpackDescription(Long id);

    String getServerDownloadUrl(Long modpackId, int serverFileId);

    Path downloadServerFile(int serverId, ModPack modPack, String serverDownloadUrl) throws CouldNotDownloadServerFilesException;

    List<ModPack.ModpackFile> getModPackFiles(Long modpackId);

    /**
     * Gets server packs for the given modpack
     * @param modpackId the modpack id
     * @return list of serverpacks
     */
    List<ServerPack> getServerPacks(Long modpackId);

    ModPack.ModpackFile getModPackFile(ModPack modPack, int fileId);
}
