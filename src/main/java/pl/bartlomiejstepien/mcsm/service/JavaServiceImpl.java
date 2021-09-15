package pl.bartlomiejstepien.mcsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.repository.JavaRepository;
import pl.bartlomiejstepien.mcsm.repository.converter.JavaConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JavaServiceImpl implements JavaService
{
    private final JavaRepository javaRepository;
    private final JavaConverter javaConverter;

    @Autowired
    public JavaServiceImpl(JavaRepository javaRepository, JavaConverter javaConverter)
    {
        this.javaRepository = javaRepository;
        this.javaConverter = javaConverter;
    }

    @Override
    public List<JavaDto> findAll()
    {
        return this.javaRepository.findAll().stream().map(this.javaConverter::convertToDto).collect(Collectors.toList());
    }

    @Override
    public JavaDto find(Integer id)
    {
        return Optional.ofNullable(this.javaRepository.find(id))
                .map(this.javaConverter::convertToDto)
                .orElse(null);
    }

    @Override
    public JavaDto findFirst()
    {
        return Optional.ofNullable(this.javaRepository.findFirst())
                .map(this.javaConverter::convertToDto)
                .orElse(null);
    }
}
