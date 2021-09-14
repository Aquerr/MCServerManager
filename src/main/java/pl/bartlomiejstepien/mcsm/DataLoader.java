package pl.bartlomiejstepien.mcsm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.domain.dto.RoleEnum;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.User;


@Component
public class DataLoader
{
    private final UserRepository userRepository;

    @Autowired
    public DataLoader(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadData()
    {
        final User user = this.userRepository.findByUsername("Nerdi");
        if (user == null)
        {
            this.userRepository.save(new User(null, "Nerdi", "$2a$10$RsBi7zEwsAHxTgQO8cBX5Oe7iCPvIkGN3ichuibM9uGzmvx6TzFC6", RoleEnum.ADMIN.getId()));
        }
    }
}
