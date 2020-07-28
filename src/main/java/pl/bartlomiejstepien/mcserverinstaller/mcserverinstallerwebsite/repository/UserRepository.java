package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public User find(final int id)
    {
        return this.entityManager.find(UserDto.class, id).toUser();
    }

    public List<User> findAll()
    {
        return this.entityManager.createQuery("from User").getResultList();
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
        final Query query = this.entityManager.createQuery("SELECT u FROM UserDto u WHERE u.username = :username");
        query.setParameter("username", username);

        final Object object = query.getSingleResult();
        final UserDto userDto = (UserDto) object;
        return userDto.toUser();
    }
}
