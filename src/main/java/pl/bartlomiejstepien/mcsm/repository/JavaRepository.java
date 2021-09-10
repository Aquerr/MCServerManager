package pl.bartlomiejstepien.mcsm.repository;

import pl.bartlomiejstepien.mcsm.repository.ds.Java;

import java.util.List;

public interface JavaRepository
{
    List<Java> findAll();

    Java find(Integer id);

    void save(Java java);

    void delete(Integer id);
}
