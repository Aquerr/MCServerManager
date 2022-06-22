package pl.bartlomiejstepien.mcsm.integration.curseforge;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.schema.CFMod;
import pl.bartlomiejstepien.schema.CFModFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
public class CurseForgeModpackConverter
{
    public ModPack convertToModpack(final CFMod cfMod)
    {
        if (cfMod == null)
            return null;

        final Long id = cfMod.getId();
        final String name = cfMod.getName();
        final String summary = cfMod.getSummary();
        final String thumbnail = cfMod.getLogo().getThumbnailUrl();
        final String version = cfMod.getLatestFilesIndexes().get(0).getGameVersion();

        final List<ModPack.ModpackFile> latestFiles = new ArrayList<>(cfMod.getLatestFiles().size());

        final Iterator<CFModFile> filesIterator = cfMod.getLatestFiles().iterator();
        while (filesIterator.hasNext())
        {
            final ModPack.ModpackFile modpackFile = convertToModPackFile(filesIterator.next());
            latestFiles.add(modpackFile);
        }

        return new ModPack(id, name, summary, thumbnail, version, latestFiles);
    }

    public ModPack.ModpackFile convertToModPackFile(final CFModFile cfLatestFile)
    {
        if (cfLatestFile == null)
            return null;

        final Long fileId = cfLatestFile.getId();
        final String displayName = cfLatestFile.getDisplayName();
        final String fileName = cfLatestFile.getFileName();
        final Instant fileDate = Instant.parse(cfLatestFile.getFileDate());
        final String downloadUrl = cfLatestFile.getDownloadUrl();
        final Long serverPackFileId = Optional.ofNullable(cfLatestFile.getServerPackFileId())
                .orElse(0L);
        return new ModPack.ModpackFile(fileId, displayName, fileName, fileDate, downloadUrl, serverPackFileId);
    }

    public List<ModPack.ModpackFile> convertToModPackFiles(CFModFile... cfLatestFiles)
    {
        final List<ModPack.ModpackFile> modPackFiles = new ArrayList<>(cfLatestFiles.length);
        for (final CFModFile file : cfLatestFiles)
        {
            modPackFiles.add(convertToModPackFile(file));
        }
        return modPackFiles;
    }
}
