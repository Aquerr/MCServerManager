package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ServerRepository
{
    private final EntityManager entityManager;

    @Autowired
    public ServerRepository(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public ServerDto find(final int id)
    {
        return this.entityManager.find(ServerDto.class, id);
    }

    public List<ServerDto> findAll()
    {
        final TypedQuery<ServerDto> query = this.entityManager.createQuery("SELECT server FROM ServerDto server", ServerDto.class);
        return query.getResultList();
    }

    public int save(final ServerDto serverDto)
    {
        final ServerDto mergedServer = this.entityManager.merge(serverDto);
        return mergedServer.getId();
//        this.entityManager.flush();
    }

    public void update(final ServerDto serverDto)
    {
        this.entityManager.merge(serverDto);
    }

    public void delete(final int id)
    {
        final ServerDto user = this.entityManager.find(ServerDto.class, id);
        if (user != null)
            this.entityManager.remove(user);
    }

    public ServerDto findByPath(String path)
    {
        try
        {
            final TypedQuery<ServerDto> query = this.entityManager.createQuery("SELECT server FROM ServerDto server WHERE server.path = :serverPath", ServerDto.class);
            query.setParameter("serverPath", path);
            return query.getSingleResult();
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    public List<ServerDto> findByUserId(int userId)
    {
        final TypedQuery<ServerDto> query = this.entityManager.createQuery("SELECT server FROM ServerDto AS server JOIN server.users AS user WHERE user.id = :userId", ServerDto.class);
        query.setParameter("userId", userId);
        List<ServerDto> serverDtos = query.getResultList();
        return serverDtos;
    }
}
