package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import javax.persistence.EntityManager;
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
        return this.entityManager.createQuery("from server").getResultList();
    }

    public int save(final ServerDto serverDto)
    {
        final ServerDto mergedServerDto = this.entityManager.merge(serverDto);
        return mergedServerDto.getId();
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
}
