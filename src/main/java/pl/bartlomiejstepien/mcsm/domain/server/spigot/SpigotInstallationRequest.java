package pl.bartlomiejstepien.mcsm.domain.server.spigot;

import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationRequest;

import java.util.Objects;

public class SpigotInstallationRequest implements ServerInstallationRequest
{
    private final String version;
    private final String username;

    public SpigotInstallationRequest(final String username, final String version)
    {
        this.username = username;
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public String getUsername()
    {
        return username;
    }

    @Override
    public Platform getPlatform()
    {
        return Platform.SPIGOT;
    }

    @Override
    public String toString()
    {
        return "SpigotInstallRequest{" +
                "version='" + version + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpigotInstallationRequest that = (SpigotInstallationRequest) o;
        return version.equals(that.version) && username.equals(that.username);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(version, username);
    }
}
