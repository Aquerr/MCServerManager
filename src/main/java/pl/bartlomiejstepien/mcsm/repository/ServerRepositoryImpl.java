package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ServerRepositoryImpl implements ServerRepository
{
    private final EntityManager entityManager;

    @Autowired
    public ServerRepositoryImpl(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    public Server find(final int id)
    {
        return this.entityManager.find(Server.class, id);
    }

    @Override
    public List<Server> findAll()
    {
        final TypedQuery<Server> query = this.entityManager.createQuery("SELECT server FROM Server server", Server.class);
        return query.getResultList();
    }

    @Override
    public int save(final Server server)
    {
        final Server mergedServer = this.entityManager.merge(server);
        return mergedServer.getId();
//        this.entityManager.flush();
    }

    @Override
    public void update(final Server server)
    {
        this.entityManager.merge(server);
    }

    @Override
    public void delete(final int id)
    {
        final Server user = this.entityManager.find(Server.class, id);
        if (user != null)
            this.entityManager.remove(user);
    }

    @Override
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

    @Override
    public List<Server> findByUserId(int userId)
    {
        final TypedQuery<Server> query = this.entityManager.createQuery("SELECT server FROM Server AS server JOIN server.usersIds AS users WHERE users IN (:userId)", Server.class);
        query.setParameter("userId", userId);
        List<Server> servers = query.getResultList();
        return servers;
    }
}
