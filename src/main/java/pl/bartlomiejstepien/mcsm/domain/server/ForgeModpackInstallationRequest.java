package pl.bartlomiejstepien.mcsm.domain.server;

import pl.bartlomiejstepien.mcsm.domain.platform.Platform;

import java.util.Objects;

public class ForgeModpackInstallationRequest implements ServerInstallationRequest
{
    private final int modpackId;
    private final int serverPackId;
    private final String username;

    public ForgeModpackInstallationRequest(final String username, final int modpackId, final int serverPackId)
    {
        this.username = username;
        this.modpackId = modpackId;
        this.serverPackId = serverPackId;
    }

    public int getModpackId()
    {
        return modpackId;
    }

    public String getUsername()
    {
        return username;
    }

    public int getServerPackId()
    {
        return serverPackId;
    }

    @Override
    public Platform getPlatform()
    {
        return Platform.FORGE;
    }

    @Override
    public String toString()
    {
        return "ForgeModpackInstallationRequest{" +
                "modpackId=" + modpackId +
                ", serverPackId=" + serverPackId +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgeModpackInstallationRequest that = (ForgeModpackInstallationRequest) o;
        return modpackId == that.modpackId && serverPackId == that.serverPackId && username.equals(that.username);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(modpackId, serverPackId, username);
    }
}
