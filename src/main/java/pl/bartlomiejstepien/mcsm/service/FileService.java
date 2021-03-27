package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.FancyTreeNode;

import java.util.List;

public interface FileService
{
    List<FancyTreeNode> getServerFileStructure(ServerDto serverDto);

    String getFileContent(String fileName, ServerDto serverDto);

    boolean saveFileContent(String fileName, String fileContent, ServerDto serverDto);
}
