package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ModPack;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class CurseForgeAPIService
{
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public List<ModPack> getModpacks()
    {
        // Add modpacks to model
        final String url = "https://addons-ecs.forgesvc.net/api/v2/addon/search?categoryId=0&gameId=432&gameVersion=&index=0&pageSize=25&searchFilter=&sectionId=4471&sort=0";
        final ArrayNode modpacksJsonArray = REST_TEMPLATE.getForObject(url, ArrayNode.class);

        final LinkedList<ModPack> modPacks = new LinkedList<>();

        final Iterator<JsonNode> jsonIterator = modpacksJsonArray.elements();
        while (jsonIterator.hasNext())
        {
            final JsonNode jsonNode = jsonIterator.next();

            final int id = jsonNode.get("id").intValue();
            final String name = jsonNode.get("name").textValue();
            final String summary = jsonNode.get("summary").textValue();
            final String thumbnail = jsonNode.get("attachments").get(0).get("thumbnailUrl").textValue();
            final ModPack modPack = new ModPack(id, name, summary, thumbnail);
            modPacks.add(modPack);
        }

        return modPacks;
    }

    public int getLatestServerFileId(final int modpackId)
    {
        final String url = "https://addons-ecs.forgesvc.net/api/v2/addon/" + modpackId + "/files";
        final ArrayNode filesJsonArray = REST_TEMPLATE.getForObject(url, ArrayNode.class);

        Instant instant = Instant.parse(filesJsonArray.get(0).get("fileDate").textValue());

        Iterator<JsonNode> jsonIterator = filesJsonArray.elements();
        while (jsonIterator.hasNext())
        {
            final JsonNode jsonNode = jsonIterator.next();

            final String fileDate = jsonNode.get("fileDate").textValue();
            final Instant fileDateInstant = Instant.parse(fileDate);

            if (fileDateInstant.isAfter(instant))
                instant = fileDateInstant;
        }

        jsonIterator = filesJsonArray.elements();
        while (jsonIterator.hasNext())
        {
            final JsonNode jsonNode = jsonIterator.next();

            final String fileDate = jsonNode.get("fileDate").textValue();
            final Instant fileDateInstant = Instant.parse(fileDate);

            if (fileDateInstant.equals(instant))
            {
                // Get the server file id
                return jsonNode.get("serverPackFileId").intValue();
            }
        }
        return 0;
    }
}
