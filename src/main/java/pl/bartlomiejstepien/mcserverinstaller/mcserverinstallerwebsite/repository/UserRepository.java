package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserRepository
{
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}
