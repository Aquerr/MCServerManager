package pl.bartlomiejstepien.mcsm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.repository.JavaRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.JavaConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigServiceImpl implements ConfigService
{
    private final JavaRepository javaRepository;
    private final JavaConverter javaConverter;

    public ConfigServiceImpl(final JavaRepository javaRepository, final JavaConverter javaConverter)
    {
        this.javaRepository = javaRepository;
        this.javaConverter = javaConverter;
    }

    @Override
    @Transactional
    public List<JavaDto> getAllJavaVersions()
    {
        return this.javaRepository.findAll().stream()
                .map(javaConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteJava(int javaId)
    {
        this.javaRepository.delete(javaId);
    }

    @Override
    @Transactional
    public void addJava(JavaDto javaDto)
    {
        this.javaRepository.save(this.javaConverter.convertToDs(javaDto));
    }
}
