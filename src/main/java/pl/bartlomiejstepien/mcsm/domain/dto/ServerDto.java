package pl.bartlomiejstepien.mcsm.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerDto
{
    private int id;
    private String name;
    private String serverDir;

    private List<Integer> usersIds = new ArrayList<>();
    private List<String> players = new LinkedList<>();
    private Path startFilePath;
    private String platform;
    private ServerProperties serverProperties = new ServerProperties();

    private Integer javaId;

    public ServerDto(int id, String name, String serverDir)
    {
        this.id = id;
        this.name = name;
        this.serverDir = serverDir;
    }

    public Path getLatestLogFilePath()
    {
        return Paths.get(this.serverDir).resolve("logs").resolve("latest.log");
    }
}
