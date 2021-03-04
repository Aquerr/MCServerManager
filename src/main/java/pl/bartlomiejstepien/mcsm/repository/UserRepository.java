package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.converter.UserConverter;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepository
{
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public User find(final int id)
    {
        return this.entityManager.find(User.class, id);
    }

    public List<User> findAll()
    {
        return ((List<User>)this.entityManager.createQuery("from user").getResultList());
    }

    public void save(final User user)
    {
        this.entityManager.persist(user);
    }

    public void delete(final int id)
    {
        final User user = this.entityManager.find(User.class, id);
        if (user != null)
            this.entityManager.remove(user);
    }

    public User findByUsername(final String username)
    {
        final TypedQuery<User> query = this.entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        final List<User> users = query.getResultList();
        return users.stream().findFirst().orElse(null);
    }
}
