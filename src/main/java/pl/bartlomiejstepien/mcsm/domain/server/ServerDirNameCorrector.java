package pl.bartlomiejstepien.mcsm.domain.server;

import org.springframework.stereotype.Component;

@Component
public class ServerDirNameCorrector
{
    private static final String ILLEGAL_CHARACTERS_PATTERN = "#|%|&|\\{|\\}|\\\\|<|>|\\*|\\?|\\/|\\$|!|'|\"|:|@|\\+|`|\\||=|\\^|\\~";

    public String convert(String path)
    {
        return path.replaceAll(ILLEGAL_CHARACTERS_PATTERN, "-");
    }
}
