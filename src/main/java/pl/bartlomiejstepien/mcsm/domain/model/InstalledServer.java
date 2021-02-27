package pl.bartlomiejstepien.mcsm.domain.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InstalledServer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDto.class);

    private int id;
    private String name;
    private Path serverDir;
    private Path startFilePath;

    private final List<UserDto> userDtos = new ArrayList<>();

    private final List<String> players = new LinkedList<>();

}
