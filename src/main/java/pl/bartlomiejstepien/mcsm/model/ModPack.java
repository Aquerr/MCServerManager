package pl.bartlomiejstepien.mcsm.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ModPack
{
    private final int id;
    private final String name;
    private final String summary;

    private final List<ModpackFile> latestFiles;

    private final String version;

    private final String thumbnailUrl;

    public static ModPack fromJson(final ObjectNode objectNode)
    {
        final int id = objectNode.get("id").intValue();
        final String name = objectNode.get("name").textValue();
        final String summary = objectNode.get("summary").textValue();
        final String thumbnail = objectNode.get("attachments").get(0).get("thumbnailUrl").textValue();
        final String version = objectNode.get("gameVersionLatestFiles").get(0).get("gameVersion").textValue();

        final ArrayNode latestFilesNode = (ArrayNode) objectNode.get("latestFiles");
        final List<ModpackFile> latestFiles = new ArrayList<>(latestFilesNode.size());

        final Iterator<JsonNode> filesIterator = latestFilesNode.elements();
        while (filesIterator.hasNext())
        {
            final JsonNode fileNode = filesIterator.next();

            final int fileId = fileNode.get("id").intValue();
            final String displayName = fileNode.get("displayName").textValue();
            final String fileName = fileNode.get("fileName").textValue();
            final Instant fileDate = Instant.parse(fileNode.get("fileDate").textValue());
            final String downloadUrl = fileNode.get("downloadUrl").textValue();
            final int serverPackFileId = fileNode.get("serverPackFileId").intValue();

            final ModpackFile modpackFile = new ModpackFile(fileId, displayName, fileName, fileDate, downloadUrl, serverPackFileId);
            latestFiles.add(modpackFile);
        }

        return new ModPack(id, name, summary, thumbnail, version, latestFiles);
    }

    public ModPack(int id, String name, String summary, String thumbnailUrl, final String version, final List<ModpackFile> latestFiles)
    {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.thumbnailUrl = thumbnailUrl;
        this.version = version;
        this.latestFiles = latestFiles;
    }

    public int getId()
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
        private final int id;
        private final String displayName;
        private final String fileName;
        private final Instant fileDate;
        private final String downloadUrl;
        private final int serverPackFileId;

        public ModpackFile(int id, String displayName, String fileName, Instant fileDate, String downloadUrl, int serverPackFileId)
        {
            this.id = id;
            this.displayName = displayName;
            this.fileName = fileName;
            this.fileDate = fileDate;
            this.downloadUrl = downloadUrl;
            this.serverPackFileId = serverPackFileId;
        }

        public int getId()
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

        public int getServerPackFileId()
        {
            return serverPackFileId;
        }
    }
}
