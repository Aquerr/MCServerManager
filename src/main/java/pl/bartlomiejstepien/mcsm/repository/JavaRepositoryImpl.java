package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcsm.repository.ds.Java;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class JavaRepositoryImpl implements JavaRepository
{
    private final EntityManager entityManager;

    @Autowired
    public JavaRepositoryImpl(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    public List<Java> findAll()
    {
        final TypedQuery<Java> query = this.entityManager.createQuery("SELECT java FROM Java java", Java.class);
        return query.getResultList();
    }

    @Override
    public Java find(Integer id)
    {
        return this.entityManager.find(Java.class, id);
    }

    @Override
    public void save(Java java)
    {
        this.entityManager.merge(java);
    }

    @Override
    public void delete(Integer id)
    {
        final Java java = this.entityManager.find(Java.class, id);
        if (java != null)
            this.entityManager.remove(java);
    }

    @Override
    public Java findFirst()
    {
        TypedQuery<Java> query = this.entityManager.createQuery("SELECT java FROM Java java", Java.class);
        query.setMaxResults(1);
        return query.getResultList().stream()
                .findFirst()
                .orElse(null);
    }
}
