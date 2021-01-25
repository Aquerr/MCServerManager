package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.Server;

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

    public Server find(final int id)
    {
        return this.entityManager.find(Server.class, id);
    }

    public List<Server> findAll()
    {
        final TypedQuery<Server> query = this.entityManager.createQuery("SELECT server FROM Server server", Server.class);
        return query.getResultList();
    }

    public int save(final Server server)
    {
        final Server mergedServer = this.entityManager.merge(server);
        return mergedServer.getId();
//        this.entityManager.flush();
    }

    public void update(final Server server)
    {
        this.entityManager.merge(server);
    }

    public void delete(final int id)
    {
        final Server user = this.entityManager.find(Server.class, id);
        if (user != null)
            this.entityManager.remove(user);
    }

    public Server findByPath(String path)
    {
        try
        {
            final TypedQuery<Server> query = this.entityManager.createQuery("SELECT server FROM Server server WHERE server.path = :serverPath", Server.class);
            query.setParameter("serverPath", path);
            return query.getSingleResult();
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    public List<Server> findByUserId(int userId)
    {
        final TypedQuery<Server> query = this.entityManager.createQuery("SELECT server FROM Server AS server JOIN server.users AS user WHERE user.id = :userId", Server.class);
        query.setParameter("userId", userId);
        List<Server> servers = query.getResultList();
        return servers;
    }
}
