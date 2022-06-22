package pl.bartlomiejstepien.mcsm.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class ModPack
{
    private final Long id;
    private final String name;
    private final String summary;

    private final List<ModpackFile> latestFiles;

    private final String version;

    private final String thumbnailUrl;

    public ModPack(Long id, String name, String summary, String thumbnailUrl, final String version, final List<ModpackFile> latestFiles)
    {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.thumbnailUrl = thumbnailUrl;
        this.version = version;
        this.latestFiles = latestFiles;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public String getVersion()
    {
        return version;
    }

    public List<ModpackFile> getLatestFiles()
    {
        return Collections.unmodifiableList(this.latestFiles);
    }

    public static final class ModpackFile
    {
        private final Long id;
        private final String displayName;
        private final String fileName;
        private final Instant fileDate;
        private final String downloadUrl;
        private final Long serverPackFileId;

        public ModpackFile(Long id, String displayName, String fileName, Instant fileDate, String downloadUrl, Long serverPackFileId)
        {
            this.id = id;
            this.displayName = displayName;
            this.fileName = fileName;
            this.fileDate = fileDate;
            this.downloadUrl = downloadUrl;
            this.serverPackFileId = serverPackFileId;
        }

        public Long getId()
        {
            return id;
        }

        public String getDisplayName()
        {
            return displayName;
        }

        public String getFileName()
        {
            return fileName;
        }

        public Instant getFileDate()
        {
            return fileDate;
        }

        public String getDownloadUrl()
        {
            return downloadUrl;
        }

        public Long getServerPackFileId()
        {
            return serverPackFileId;
        }
    }
}
