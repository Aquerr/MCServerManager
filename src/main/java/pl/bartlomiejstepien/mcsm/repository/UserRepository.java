package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcsm.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public UserDto find(final int id)
    {
        final User user = this.entityManager.find(User.class, id);
        if (user != null)
            return user.toUser();
        else return null;
    }

    public List<UserDto> findAll()
    {
        return ((List<User>)this.entityManager.createQuery("from user").getResultList()).stream().map(User::toUser).collect(Collectors.toList());
    }

    public void save(final User user)
    {
        this.entityManager.merge(user);
    }

    public void delete(final int id)
    {
        final User user = this.entityManager.find(User.class, id);
        if (user != null)
            this.entityManager.remove(user);
    }

    public User findByUsername(final String username)
    {
        final Query query = this.entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
        query.setParameter("username", username);

        final Object object = query.getSingleResult();
        final User user = (User) object;
        return user;
    }
}
