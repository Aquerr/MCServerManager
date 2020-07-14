package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.config.CurseForgeAPIRoutes;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ModPack;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        final String url = CurseForgeAPIRoutes.MODPACKS_SEARCH;
        final ArrayNode modpacksJsonArray = REST_TEMPLATE.getForObject(url, ArrayNode.class);

        final LinkedList<ModPack> modPacks = new LinkedList<>();

        final Iterator<JsonNode> jsonIterator = modpacksJsonArray.elements();
        while (jsonIterator.hasNext())
        {
            final JsonNode jsonNode = jsonIterator.next();
            if (!jsonNode.isObject())
                continue;

            final ModPack modPack = ModPack.fromJson((ObjectNode) jsonNode);
            modPacks.add(modPack);
        }

        return modPacks;
    }

    public int getLatestServerFileId(final int modpackId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_FILES.replace("{modpackId}", String.valueOf(modpackId));
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

    public ModPack getModpack(final int id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_INFO.replace("{modpackId}", String.valueOf(id));
        final ObjectNode modpackJson = REST_TEMPLATE.getForObject(url, ObjectNode.class);
        if (modpackJson == null)
            throw new RuntimeException("Could not find modpack with id " + id);

        final ModPack modPack = ModPack.fromJson(modpackJson);
        return modPack;
    }

    public String getModpackDescription(final int id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_DESCRIPTION.replace("{modpackId}", String.valueOf(id));
        final String modpackDescription = REST_TEMPLATE.getForObject(url, String.class);
        return modpackDescription;
    }

    public String getLatestServerDownloadUrl(final int modpackId,final int latestServerFileId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_LATEST_SERVER_DOWNLOAD_URL.replace("{modpackId}", String.valueOf(modpackId)).replace("{fileId}", String.valueOf(latestServerFileId));
        final String serverDownloadUrl = REST_TEMPLATE.getForObject(url, String.class);
        return serverDownloadUrl;
    }

    /**
     * Downloads the zip file that contains server files.
     * @param serverDownloadUrl the link to download from
     * @return the name of the zip file (with .zip extension)
     */
    public String downloadServerFile(final int modpackId, final String serverDownloadUrl)
    {
        URI uri = URI.create(serverDownloadUrl);
        Path path = Paths.get(uri.getPath());
        final String last = path.getName(path.getNameCount() - 1).toString();

//        final WebClient webClient = WebClient.create(serverDownloadUrl);
//        final byte[] response = webClient
//                .get()
//                .retrieve()
//                .bodyToMono(byte[].class)
//                .block();
//
//
        URL url = null;
        try
        {
            url = new URL(serverDownloadUrl);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("downloads/" + last))
        {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1)
            {
                final InstallationStatus installationStatus = ServerService.MODPACKS_INSTALLATION_STATUSES.getOrDefault(modpackId, new InstallationStatus(0, "Downloading server files..."));
                installationStatus.setPercent(installationStatus.getPercent() + 5);
                ServerService.MODPACKS_INSTALLATION_STATUSES.put(modpackId, installationStatus);
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        //Working!!!
//        try(InputStream inputStream = url.openStream(); ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
//                FileOutputStream fileOutputStream = new FileOutputStream("test.zip"))
//        {
//            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        return last;
    }
}
