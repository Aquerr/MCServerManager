package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;

import java.util.List;

public interface ConfigService
{
    List<JavaDto> getAllJavaVersions();

    void deleteJava(int javaId);

    void addJava(JavaDto javaDto);
}
