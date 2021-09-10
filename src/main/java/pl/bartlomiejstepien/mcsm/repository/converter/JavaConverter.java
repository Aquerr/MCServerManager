package pl.bartlomiejstepien.mcsm.repository.converter;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.repository.ds.Java;

@Component
public class JavaConverter
{
    public JavaDto convertToDto(Java java)
    {
        if (java == null)
            return null;

        JavaDto javaDto = new JavaDto();
        javaDto.setId(java.getId());
        javaDto.setName(java.getName());
        javaDto.setPath(java.getPath());
        return javaDto;
    }

    public Java convertToDs(JavaDto javaDto)
    {
        if (javaDto == null)
            return null;

        Java java = new Java();
        java.setId(javaDto.getId());
        java.setName(javaDto.getName());
        java.setPath(javaDto.getPath());
        return java;
    }
}
