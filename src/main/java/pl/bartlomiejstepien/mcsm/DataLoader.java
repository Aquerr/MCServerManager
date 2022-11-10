package pl.bartlomiejstepien.mcsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.domain.model.Role;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.util.UUID;


@Component
public class DataLoader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(final UserRepository userRepository, final PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadData()
    {
        final User user = this.userRepository.findByUsername("mcsm");
        if (user == null)
        {
            UUID uuid = UUID.randomUUID();
            LOGGER.info("\n\nYour initial password for 'mcsm' user is: " + uuid + "\n");
            this.userRepository.save(new User(null, "mcsm", passwordEncoder.encode(uuid.toString()), Role.OWNER.getId()));
        }
    }
}
