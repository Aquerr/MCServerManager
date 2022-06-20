package pl.bartlomiejstepien.mcsm.integration.curseforge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;

import java.time.Instant;
import java.util.*;

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
        final String thumbnail = objectNode.get("logo").get("thumbnailUrl").textValue();
        final String version = objectNode.get("latestFilesIndexes").get(0).get("gameVersion").textValue();

        final ArrayNode latestFilesNode = (ArrayNode) objectNode.get("latestFiles");
        final List<ModPack.ModpackFile> latestFiles = new ArrayList<>(latestFilesNode.size());

        final Iterator<JsonNode> filesIterator = latestFilesNode.elements();
        while (filesIterator.hasNext())
        {
            final JsonNode fileNode = filesIterator.next();
            final ModPack.ModpackFile modpackFile = convertToModPackFile((ObjectNode) fileNode);
            latestFiles.add(modpackFile);
        }

        return new ModPack(id, name, summary, thumbnail, version, latestFiles);
    }

    public ModPack.ModpackFile convertToModPackFile(final ObjectNode objectNode)
    {
        if (objectNode == null)
            return null;

        final int fileId = objectNode.get("id").intValue();
        final String displayName = objectNode.get("displayName").textValue();
        final String fileName = objectNode.get("fileName").textValue();
        final Instant fileDate = Instant.parse(objectNode.get("fileDate").textValue());
        final String downloadUrl = objectNode.get("downloadUrl").textValue();
        final int serverPackFileId = Optional.ofNullable(objectNode.get("serverPackFileId"))
                .map(JsonNode::intValue)
                .orElse(0);
        return new ModPack.ModpackFile(fileId, displayName, fileName, fileDate, downloadUrl, serverPackFileId);
    }

    public List<ModPack.ModpackFile> convertToModPackFiles(ArrayNode modPackFilesJsonArray)
    {
        final List<ModPack.ModpackFile> modPackFiles = new ArrayList<>(modPackFilesJsonArray.size());
        for (final JsonNode jsonNode : modPackFilesJsonArray)
        {
            modPackFiles.add(convertToModPackFile((ObjectNode) jsonNode));
        }
        return modPackFiles;
    }
}
