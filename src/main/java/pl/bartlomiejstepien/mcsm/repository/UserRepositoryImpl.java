package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository
{
    private final EntityManager entityManager;

    @Autowired
    public UserRepositoryImpl(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    public User find(final int id)
    {
        return this.entityManager.find(User.class, id);
    }

    @Override
    public List<User> findAll()
    {
        return ((List<User>)this.entityManager.createQuery("from User").getResultList());
    }

    @Override
    public void save(final User user)
    {
        this.entityManager.merge(user);
    }

    @Override
    public void delete(final int id)
    {
        final User user = this.entityManager.find(User.class, id);
        if (user != null)
            this.entityManager.remove(user);
    }

    @Override
    public User findByUsername(final String username)
    {
        final TypedQuery<User> query = this.entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        final List<User> users = query.getResultList();
        return users.stream().findFirst().orElse(null);
    }

    @Override
    public void register(User user)
    {
        this.entityManager.persist(user);
    }

    @Override
    public void update(User user)
    {
        this.entityManager.merge(user);
    }

    @Override
    public Long countAllUsers()
    {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(User.class)));
        return this.entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public List<User> findPaginated(Pageable pageable)
    {
        final TypedQuery<User> query = this.entityManager.createQuery("from User", User.class);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }
}
