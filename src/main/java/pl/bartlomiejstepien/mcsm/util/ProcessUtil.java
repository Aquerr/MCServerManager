package pl.bartlomiejstepien.mcsm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProcessUtil
{
    private static final Pattern PATTERN = Pattern.compile("^[0-9]+.*$");

    public static long getProcessID(final String name)
    {
        try
        {
            final Process process = Runtime.getRuntime().exec(new String[]{"powershell.exe", "Get-Process | Where-Object {$_.MainWindowTitle -Like \"'*" + name + "*'\"}"});
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                line = line.trim();

                if (line.equals(""))
                    continue;

                if (!PATTERN.matcher(line).matches())
                    continue;

                final String[] splitLine = line.split(" ");
                final List<String> processLineArgs = Arrays.stream(splitLine).filter(x-> !x.isEmpty() && !x.trim().equals("")).collect(Collectors.toList());
                return Long.parseLong(processLineArgs.get(5));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return -1;
    }
}
