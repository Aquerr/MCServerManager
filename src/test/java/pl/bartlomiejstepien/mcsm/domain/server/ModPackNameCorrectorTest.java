package pl.bartlomiejstepien.mcsm.domain.server;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ModPackNameCorrectorTest
{
    @InjectMocks
    private ModPackNameCorrector modPackNameCorrector;

    @ParameterizedTest
    @MethodSource(value = "prepareStringsWillIllegalCharacters")
    void convertRemovesAllIllegalCharactersFromGivenString(String text)
    {
        final String convertedText = modPackNameCorrector.convert(text);

        assertThat(convertedText).doesNotContain(illegalCharactersList());
    }

    private static List<String> prepareStringsWillIllegalCharacters()
    {
        return List.of(
                "Project Ozone: Season 2",
                "{}~+=*&'",
                "Yolo /\\ 3dots%?`$"
        );
    }

    private List<String> illegalCharactersList()
    {
        return List.of("#", "%", "&", "{", "}", "\\", "<", ">", "*", "?", "/", "$", "!", "'", "\"", ":", "@", "+", "`", "|", "=", "^", "~");
    }
}