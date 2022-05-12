package pl.bartlomiejstepien.mcsm.integration.curseforge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.config.CurseForgeAPIRoutes;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.model.ServerPack;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationStatusMonitor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurseForgeClientImpl implements CurseForgeClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CurseForgeClientImpl.class);
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    private final CurseforgeModpackConverter curseforgeModpackConverter;
    private final Config config;
    private final ServerInstallationStatusMonitor serverInstallationStatusMonitor;

    @Autowired
    public CurseForgeClientImpl(final Config config, final ServerInstallationStatusMonitor serverInstallationStatusMonitor, final CurseforgeModpackConverter curseforgeModpackConverter)
    {
        this.config = config;
        this.serverInstallationStatusMonitor = serverInstallationStatusMonitor;
        this.curseforgeModpackConverter = curseforgeModpackConverter;
    }

    @Override
    public List<ModPack> getModpacks(final int categoryId, String modpackName, String version, final int count, int startIndex)
    {
        final String url = CurseForgeAPIRoutes.MODPACKS_SEARCH.replace("{categoryId}", String.valueOf(categoryId))
                .replace("{version}", version)
                .replace("{size}", String.valueOf(count))
                .replace("{index}", String.valueOf(startIndex))
                .replace("{modpackName}", modpackName);
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

            final ModPack modPack = this.curseforgeModpackConverter.convertToModpack((ObjectNode) jsonNode);
            modPacks.add(modPack);
        }

        return modPacks;
    }

    @Override
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

    @Override
    public ModPack getModpack(final int id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_INFO.replace("{modpackId}", String.valueOf(id));
        LOGGER.info("GET " + url);
        final ObjectNode modpackJson = REST_TEMPLATE.getForObject(url, ObjectNode.class);
        LOGGER.debug("Response: " + modpackJson);
        if (modpackJson == null)
            throw new RuntimeException("Could not find modpack with id " + id);

        final ModPack modPack = this.curseforgeModpackConverter.convertToModpack(modpackJson);
        return modPack;
    }

    @Override
    public String getModpackDescription(final int id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_DESCRIPTION.replace("{modpackId}", String.valueOf(id));
        LOGGER.info("GET " + url);
        final String modpackDescription = REST_TEMPLATE.getForObject(url, String.class);
        LOGGER.debug("Response: " + modpackDescription);
        return modpackDescription;
    }

    @Override
    public String getServerDownloadUrl(final int modpackId, final int serverFileId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_LATEST_SERVER_DOWNLOAD_URL.replace("{modpackId}", String.valueOf(modpackId)).replace("{fileId}", String.valueOf(serverFileId));
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
    @Override
    public boolean downloadServerFile(int serverId, final ModPack modPack, String serverDownloadUrl) throws CouldNotDownloadServerFilesException
    {
        LOGGER.info("Downloading server files for modpack {id=" + modPack.getId() + ", name=" + modPack.getName() + "}");
        try
        {
            final URL url = new URL(serverDownloadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            long totalToDownload = httpURLConnection.getContentLengthLong();
            long alreadyDownloaded = 0;
            this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(1, 0, "Downloading server files..."));

            try(BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(this.config.getDownloadsDir() + File.separator + modPack.getName() + "_" + modPack.getVersion()))
            {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1)
                {
                    final InstallationStatus installationStatus = this.serverInstallationStatusMonitor.getInstallationStatus(modPack.getId()).orElse(new InstallationStatus(1, 0, "Downloading server files..."));
                    alreadyDownloaded += 1024;
                    int percentage = (int)(alreadyDownloaded * 100 / totalToDownload);
                    installationStatus.setPercent(percentage);
                    this.serverInstallationStatusMonitor.setInstallationStatus(serverId, installationStatus);
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

        return true;
    }

    @Override
    public List<ModPack.ModpackFile> getModPackFiles(int modpackId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_FILES.replace("{modpackId}", String.valueOf(modpackId));
        LOGGER.info("GET " + url);
        final ArrayNode filesJsonArray = REST_TEMPLATE.getForObject(url, ArrayNode.class);
        LOGGER.debug("Response: " + filesJsonArray);
        return this.curseforgeModpackConverter.convertToModPackFiles(filesJsonArray);
    }

    @Override
    public List<ServerPack> getServerPacks(int modpackId)
    {
        final List<ModPack.ModpackFile> modpackFiles = getModPackFiles(modpackId);
        return modpackFiles.stream()
                .filter(modpackFile -> !StringUtils.isEmpty(modpackFile.getServerPackFileId()))
                .sorted(Comparator.comparing(ModPack.ModpackFile::getFileDate).reversed())
                .map(modpackFile -> new ServerPack(modpackFile.getServerPackFileId(), modpackFile.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public ModPack.ModpackFile getModPackFile(ModPack modPack, int fileId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_FILE
                .replace("{modpackId}", String.valueOf(modPack.getId()))
                .replace("{fileId}", String.valueOf(fileId));
        LOGGER.info("GET " + url);
        final ObjectNode modpackFileJson = REST_TEMPLATE.getForObject(url, ObjectNode.class);
        LOGGER.debug("Response: " + modpackFileJson);
        return this.curseforgeModpackConverter.convertToModPackFile(modpackFileJson);
    }
}
