package pl.bartlomiejstepien.mcsm.integration.getbukkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class GetBukkitClientImpl implements GetBukkitClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GetBukkitClientImpl.class);
    private static final String DOWNLOAD_URL = "https://download.getbukkit.org/spigot/spigot-{version}.jar";

    private static final String SPIGOT_DOWNLOAD_DIR = "spigot";

    private final Config config;

    public GetBukkitClientImpl(final Config config)
    {
        this.config = config;
    }

    @Override
    public Path downloadServer(String version) throws CouldNotDownloadServerFilesException
    {
        // Replace version placeholder in url.
        String downloadUrl = DOWNLOAD_URL.replace("{version}", version);

        Path downloadPath = prepareDownloadPath(version);

        if (Files.exists(downloadPath))
        {
            LOGGER.debug("File {} already exists! Skipping download.", downloadPath);
            return downloadPath;
        }

        LOGGER.info("Downloading spigot: {}", downloadUrl);
        try
        {
            Files.createDirectories(this.config.getDownloadsDirPath().resolve(SPIGOT_DOWNLOAD_DIR));
            final URL url = new URL(downloadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            long totalToDownload = httpURLConnection.getContentLengthLong();
//            long alreadyDownloaded = 0;
//            this.serverInstallationStatusMonitor.setInstallationStatus(modPack.getId(), new InstallationStatus(0, "Downloading server files..."));

            try(BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(downloadPath.toAbsolutePath().toString()))
            {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1)
                {
//                    final InstallationStatus installationStatus = this.serverInstallationStatusMonitor.getInstallationStatus(modPack.getId()).orElse(new InstallationStatus(0, "Downloading server files..."));
//                    alreadyDownloaded += 1024;
//                    int percentage = (int)(alreadyDownloaded * 100 / totalToDownload);
//                    installationStatus.setPercent(percentage);
//                    this.serverInstallationStatusMonitor.setInstallationStatus(modPack.getId(), installationStatus);
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        }
        catch (Exception exception)
        {
            throw new CouldNotDownloadServerFilesException("Could not download server files", exception);
        }

        // Place the file in downloads/spigot.

        // Return path to the downloaded file.

        return downloadPath;
    }

    private Path prepareDownloadPath(String version)
    {
        return this.config.getDownloadsDirPath().resolve(SPIGOT_DOWNLOAD_DIR).resolve("spigot-" + version + ".jar");
    }
}