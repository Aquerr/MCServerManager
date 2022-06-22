package pl.bartlomiejstepien.mcsm.integration.curseforge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import pl.bartlomiejstepien.schema.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final CurseForgeModpackConverter curseForgeModpackConverter;
    private final Config config;
    private final ServerInstallationStatusMonitor serverInstallationStatusMonitor;
    private final RestTemplate restTemplate;

    @Autowired
    public CurseForgeClientImpl(final Config config,
                                final ServerInstallationStatusMonitor serverInstallationStatusMonitor,
                                final CurseForgeModpackConverter curseForgeModpackConverter,
                                @Qualifier("curseForgeRestTemplate") final RestTemplate restTemplate)
    {
        this.config = config;
        this.serverInstallationStatusMonitor = serverInstallationStatusMonitor;
        this.curseForgeModpackConverter = curseForgeModpackConverter;
        this.restTemplate = restTemplate;
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
        final CFGetModsResponse modpacksResponse = restTemplate.getForObject(url, CFGetModsResponse.class);
        LOGGER.debug("Response: " + modpacksResponse);
        final LinkedList<ModPack> modPacks = new LinkedList<>();
        final Iterator<CFMod> modIterator = modpacksResponse.getData().iterator();
        while (modIterator.hasNext())
        {
            final CFMod mod = modIterator.next();
            final ModPack modPack = this.curseForgeModpackConverter.convertToModpack(mod);

            modPacks.add(modPack);
        }

        return modPacks;
    }

    @Override
    public int getLatestServerFileId(final Long modpackId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_FILES.replace("{modpackId}", String.valueOf(modpackId));
        LOGGER.info("GET " + url);
        final ArrayNode filesJsonArray = restTemplate.getForObject(url, ArrayNode.class);
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
    public ModPack getModpack(final Long id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_INFO.replace("{modpackId}", String.valueOf(id));
        LOGGER.info("GET " + url);
        final CFGetModResponse modpackResponse = restTemplate.getForObject(url, CFGetModResponse.class);
        LOGGER.debug("Response: " + modpackResponse);
        if (modpackResponse == null)
            throw new RuntimeException("Could not find modpack with id " + id);

        final ModPack modPack = this.curseForgeModpackConverter.convertToModpack(modpackResponse.getData());
        return modPack;
    }

    @Override
    public String getModpackDescription(final Long id)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_DESCRIPTION.replace("{modpackId}", String.valueOf(id));
        LOGGER.info("GET " + url);
        final CFGetModDescriptionResponse modpackDescription = restTemplate.getForObject(url, CFGetModDescriptionResponse.class);
        LOGGER.debug("Response: " + modpackDescription);
        return modpackDescription.getData();
    }

    @Override
    public String getServerDownloadUrl(final Long modpackId, final int serverFileId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_LATEST_SERVER_DOWNLOAD_URL.replace("{modpackId}", String.valueOf(modpackId)).replace("{fileId}", String.valueOf(serverFileId));
        LOGGER.info("GET " + url);
        final CFGetFileDownloadUrlResponse serverDownloadUrl = restTemplate.getForObject(url, CFGetFileDownloadUrlResponse.class);
        LOGGER.debug("Response: " + serverDownloadUrl);
        return serverDownloadUrl.getData();
    }

    /**
     * Downloads the zip file that contains server files.
     *
     * @param modPack to download
     * @param serverDownloadUrl the link to download from
     * @return path to the downloaded zip file
     */
    @Override
    public Path downloadServerFile(int serverId, final ModPack modPack, String serverDownloadUrl) throws CouldNotDownloadServerFilesException
    {
        LOGGER.info("Downloading server files for modpack {id=" + modPack.getId() + ", name=" + modPack.getName() + "}");
        Path downloadPath = Paths.get(this.config.getDownloadsDir() + File.separator + modPack.getName() + "_" + modPack.getVersion() + ".zip");

        try
        {
            final URL url = new URL(serverDownloadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            long totalToDownload = httpURLConnection.getContentLengthLong();
            long alreadyDownloaded = 0;
            this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(1, 0, "Downloading server files..."));

            try(BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(downloadPath.toAbsolutePath().toString()))
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
            return downloadPath;
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
    }

    @Override
    public List<ModPack.ModpackFile> getModPackFiles(Long modpackId)
    {
        final String url = CurseForgeAPIRoutes.MODPACK_FILES.replace("{modpackId}", String.valueOf(modpackId));
        LOGGER.info("GET " + url);
        final CFGetModFilesResponse modFilesResponse = restTemplate.getForObject(url, CFGetModFilesResponse.class);
        LOGGER.debug("Response: " + modFilesResponse);
        return this.curseForgeModpackConverter.convertToModPackFiles(modFilesResponse.getData().toArray(new CFModFile[0]));
    }

    @Override
    public List<ServerPack> getServerPacks(Long modpackId)
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
        final CFModFile modpackFile = restTemplate.getForObject(url, CFModFile.class);
        LOGGER.debug("Response: " + modpackFile);
        return this.curseForgeModpackConverter.convertToModPackFile(modpackFile);
    }
}
