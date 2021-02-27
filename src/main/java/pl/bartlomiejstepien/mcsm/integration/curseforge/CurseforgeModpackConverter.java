package pl.bartlomiejstepien.mcsm.integration.curseforge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class CurseforgeModpackConverter
{
    public ModPack convertToModpack(final ObjectNode objectNode)
    {
        if (objectNode == null)
            return null;

        final int id = objectNode.get("id").intValue();
        final String name = objectNode.get("name").textValue();
        final String summary = objectNode.get("summary").textValue();
        final String thumbnail = objectNode.get("attachments").get(0).get("thumbnailUrl").textValue();
        final String version = objectNode.get("gameVersionLatestFiles").get(0).get("gameVersion").textValue();

        final ArrayNode latestFilesNode = (ArrayNode) objectNode.get("latestFiles");
        final List<ModPack.ModpackFile> latestFiles = new ArrayList<>(latestFilesNode.size());

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

            final ModPack.ModpackFile modpackFile = new ModPack.ModpackFile(fileId, displayName, fileName, fileDate, downloadUrl, serverPackFileId);
            latestFiles.add(modpackFile);
        }

        return new ModPack(id, name, summary, thumbnail, version, latestFiles);
    }
}
