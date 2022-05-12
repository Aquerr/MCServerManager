package pl.bartlomiejstepien.mcsm.integration.curseforge;

import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.model.ServerPack;

import java.util.List;

public interface CurseForgeClient
{
    List<ModPack> getModpacks(int categoryId, String modpackName, String version, int count, int startIndex);

    int getLatestServerFileId(int modpackId);

    ModPack getModpack(int id);

    String getModpackDescription(int id);

    String getServerDownloadUrl(int modpackId, int serverFileId);

    boolean downloadServerFile(int serverId, ModPack modPack, String serverDownloadUrl) throws CouldNotDownloadServerFilesException;

    List<ModPack.ModpackFile> getModPackFiles(int modpackId);

    /**
     * Gets server packs for the given modpack
     * @param modpackId the modpack id
     * @return list of serverpacks
     */
    List<ServerPack> getServerPacks(int modpackId);

    ModPack.ModpackFile getModPackFile(ModPack modPack, int fileId);
}
