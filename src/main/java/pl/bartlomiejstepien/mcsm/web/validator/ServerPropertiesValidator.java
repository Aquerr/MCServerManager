package pl.bartlomiejstepien.mcsm.web.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;

public class ServerPropertiesValidator implements Validator
{
    @Override
    public boolean supports(Class<?> clazz)
    {
        return ServerProperties.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        ValidationUtils.rejectIfEmpty(errors, "levelName", "levelName.error");
    }
}
