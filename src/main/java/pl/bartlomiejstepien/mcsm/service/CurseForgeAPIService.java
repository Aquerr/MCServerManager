package pl.bartlomiejstepien.mcsm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.config.CurseForgeAPIRoutes;
import pl.bartlomiejstepien.mcsm.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.model.ModPack;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class CurseForgeAPIService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CurseForgeAPIService.class);
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final Config config;
    private final ServerInstaller serverInstaller;

    @Autowired
    public CurseForgeAPIService(final Config config, final ServerInstaller serverInstaller)
    {
        this.config = config;
        this.serverInstaller = serverInstaller;
    }

    public List<ModPack> getModpacks(final int categoryId, String version, final int count)
    {
        final String url = CurseForgeAPIRoutes.MODPACKS_SEARCH.replace("{categoryId}", String.valueOf(categoryId)).replace("${version}", version).replace("{size}", String.valueOf(count));
        LOGGER.info("GET " + url);
        final ArrayNode modpacksJsonArray = REST_TEMPLATE.getForObject(url, ArrayNode.class);
        LOGGER.debug("Response: " + modpacksJsonArray);
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
        LOGGER.info("GET " + url);
        final ArrayNode filesJsonArray = REST_TEMPLATE.getForObject(url, ArrayNode.class);
        LOGGER.debug("Response: " + filesJsonArray);

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
        LOGGER.info("GET " + url);
        final ObjectNode modpackJson = REST_TEMPLATE.getForObject(url, ObjectNode.class);
        LOGGER.debug("Response: " + modpackJson);
        if (modpackJson == null)
            throw new RuntimeException("Could not find modpack with id " + id);

        final ModPack modPack = ModPack.fromJson(modpackJson);
        return modPack;
    }

    public String getModpackDescription(final int id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_DESCRIPTION.replace("{modpackId}", String.valueOf(id));
        LOGGER.info("GET " + url);
        final String modpackDescription = REST_TEMPLATE.getForObject(url, String.class);
        LOGGER.debug("Response: " + modpackDescription);
        return modpackDescription;
    }

    public String getLatestServerDownloadUrl(final int modpackId,final int latestServerFileId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_LATEST_SERVER_DOWNLOAD_URL.replace("{modpackId}", String.valueOf(modpackId)).replace("{fileId}", String.valueOf(latestServerFileId));
        LOGGER.info("GET " + url);
        final String serverDownloadUrl = REST_TEMPLATE.getForObject(url, String.class);
        LOGGER.debug("Response: " + serverDownloadUrl);
        return serverDownloadUrl;
    }

    /**
     * Downloads the zip file that contains server files.
     *
     * @param modPack to download
     * @param serverDownloadUrl the link to download from
     * @return the name of the zip file (with .zip extension)
     */
    public String downloadServerFile(final ModPack modPack, String serverDownloadUrl) throws CouldNotDownloadServerFilesException
    {
        try
        {
            final URL url = new URL(serverDownloadUrl);
            try(BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(this.config.getDownloadsDir() + File.separator + modPack.getName() + "_" + modPack.getVersion()))
            {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1)
                {
                    final InstallationStatus installationStatus = this.serverInstaller.getInstallationStatus(modPack.getId()).orElse(new InstallationStatus(0, "Downloading server files..."));
                    installationStatus.setPercent(bufferedInputStream.available() / 100);
                    this.serverInstaller.setInstallationStatus(modPack.getId(), installationStatus);
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        }
        catch (Exception exception)
        {
            throw new CouldNotDownloadServerFilesException("Could not download server files", exception);
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

        return modPack.getName();
    }
}
