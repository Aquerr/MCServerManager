package pl.bartlomiejstepien.mcsm.repository;

import org.springframework.data.domain.Pageable;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.List;

public interface UserRepository
{
    User find(int id);

    List<User> findAll();

    void save(User user);

    void delete(int id);

    User findByUsername(String username);

    void register(User user);

    void update(User user);

    Long countAllUsers();

    List<User> findPaginated(Pageable pageable);
}
