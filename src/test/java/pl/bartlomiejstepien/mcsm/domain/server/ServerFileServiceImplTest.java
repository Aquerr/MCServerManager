package pl.bartlomiejstepien.mcsm.domain.server;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServerFileServiceImplTest
{
    @InjectMocks
    private ServerFileServiceImpl serverFileService;

    @ParameterizedTest
    @MethodSource(value = "prepareStringsWillIllegalCharacters")
    void convertRemovesAllIllegalCharactersFromGivenString(String text)
    {
        final String convertedText = serverFileService.prepareFilePath(text);

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