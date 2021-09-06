package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcsm.repository.ds.McsmPrincipal;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserRepository
{
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public McsmPrincipal find(final int id)
    {
        return this.entityManager.find(McsmPrincipal.class, id);
    }

    public List<McsmPrincipal> findAll()
    {
        return ((List<McsmPrincipal>)this.entityManager.createQuery("from user").getResultList());
    }

    public void save(final McsmPrincipal mcsmPrincipal)
    {
        this.entityManager.persist(mcsmPrincipal);
    }

    public void delete(final int id)
    {
        final McsmPrincipal mcsmPrincipal = this.entityManager.find(McsmPrincipal.class, id);
        if (mcsmPrincipal != null)
            this.entityManager.remove(mcsmPrincipal);
    }

    public McsmPrincipal findByUsername(final String username)
    {
        final TypedQuery<McsmPrincipal> query = this.entityManager.createQuery("SELECT u FROM McsmPrincipal u WHERE u.username = :username", McsmPrincipal.class);
        query.setParameter("username", username);
        final List<McsmPrincipal> mcsmPrincipals = query.getResultList();
        return mcsmPrincipals.stream().findFirst().orElse(null);
    }
}
