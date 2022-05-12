package pl.bartlomiejstepien.mcsm.repository;

import pl.bartlomiejstepien.mcsm.repository.ds.Server;

import java.util.List;

public interface ServerRepository
{
    Server find(int id);

    List<Server> findAll();

    int save(Server server);

    void saveNewServer(Server server);

    void update(Server server);

    void delete(int id);

    Server findByPath(String path);

    List<Server> findByUserId(int userId);

    Integer getLastFreeServerId();
}
