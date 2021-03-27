package pl.bartlomiejstepien.mcsm.service;

import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.FancyTreeNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService
{
    public FileServiceImpl()
    {

    }

    @Override
    public List<FancyTreeNode> getServerFileStructure(final ServerDto serverDto)
    {
        try
        {
            return Files.list(Paths.get(serverDto.getServerDir()))
                    .map(this::mapPathToFancyTreeNode)
                    .collect(Collectors.toList());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public String getFileContent(final String fileName, final ServerDto serverDto)
    {
        try
        {
            final FilePathCollector filePathCollector = new FilePathCollector(fileName);
            Files.walkFileTree(Paths.get(serverDto.getServerDir()), new ServerFileVisitor(filePathCollector));
            return Optional.ofNullable(filePathCollector.getFoundPath())
                    .map(path -> {
                        try
                        {
                            return Files.readString(path, StandardCharsets.UTF_8);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        return "";
                    })
            .orElse("");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean saveFileContent(final String fileName, final String fileContent, final ServerDto serverDto)
    {
        try
        {
            Files.walk(Paths.get(serverDto.getServerDir()))
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .filter(this::filterRestrictedFiles)
                    .findFirst()
                    .ifPresent(path -> {
                        try
                        {
                            Files.writeString(path, fileContent, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    private FancyTreeNode mapPathToFancyTreeNode(Path path)
    {
        if (Files.isDirectory(path))
            return getDirectoryNode(path);
        else
            return getFileNode(path);
    }

    private FancyTreeNode getFileNode(Path path)
    {
        return new FancyTreeNode(path.getFileName().toString(), false);
    }

    private FancyTreeNode getDirectoryNode(Path path)
    {
        final FancyTreeNode directoryNode = new FancyTreeNode(path.getFileName().toString(), true);
        try
        {
            Files.list(path)
                    .map(this::mapPathToFancyTreeNode)
                    .forEach(directoryNode::addChild);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return directoryNode;
    }

    private static class ServerFileVisitor extends SimpleFileVisitor<Path>
    {
        private final FilePathCollector filePathCollector;

        public ServerFileVisitor(final FilePathCollector filePathCollector)
        {
            this.filePathCollector = filePathCollector;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        {
            if (Files.isDirectory(file))
                return FileVisitResult.CONTINUE;

            String pathFileName = file.getFileName().toString();
            if (!pathFileName.contains(".")
                    || pathFileName.endsWith(".jar")
                    || pathFileName.endsWith(".sh")
                    || pathFileName.endsWith(".bat"))
                return FileVisitResult.CONTINUE;

            if(pathFileName.equals(filePathCollector.getFileNameToLookFor()))
            {
                filePathCollector.setFoundPath(file);
                return FileVisitResult.TERMINATE;
            }

            return FileVisitResult.CONTINUE;
        }
    }

    private static class FilePathCollector
    {
        private final String fileNameToLookFor;
        private Path foundPath = null;

        public FilePathCollector(String fileNameToLookFor)
        {
            this.fileNameToLookFor = fileNameToLookFor;
        }

        public Path getFoundPath()
        {
            return foundPath;
        }

        public void setFoundPath(final Path path)
        {
            this.foundPath = path;
        }

        public String getFileNameToLookFor()
        {
            return fileNameToLookFor;
        }
    }

    private boolean filterRestrictedFiles(Path path)
    {
        String pathFileName = path.getFileName().toString();
        return Files.isDirectory(path)
                || !pathFileName.contains(".")
                || pathFileName.endsWith(".jar")
                || pathFileName.endsWith(".sh")
                || pathFileName.endsWith(".bat");
    }
}
