package pl.bartlomiejstepien.mcsm.domain.server;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.FancyTreeNode;

import java.util.List;

public interface ServerFileService
{
    List<FancyTreeNode> getServerFileStructure(ServerDto serverDto);

    String getFileContent(String fileName, ServerDto serverDto);

    boolean saveFileContent(String fileName, String fileContent, ServerDto serverDto);
}
