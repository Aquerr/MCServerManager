package pl.bartlomiejstepien.mcsm.service;

import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;

import java.util.List;

public interface JavaService
{
    List<JavaDto> findAll();

    JavaDto find(Integer id);

    JavaDto findFirst();
}
