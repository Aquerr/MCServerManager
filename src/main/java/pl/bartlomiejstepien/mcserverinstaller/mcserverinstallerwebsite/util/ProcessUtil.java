package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProcessUtil
{
    private static final Pattern PATTERN = Pattern.compile("^[0-9]+.*$");

    public static long getProcessID(Process p)
    {
        long result = -1;
        try
        {
            //for windows
            if (p.getClass().getName().equals("java.lang.Win32Process") ||
                    p.getClass().getName().equals("java.lang.ProcessImpl"))
            {
                Field f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handl = f.getLong(p);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE hand = new WinNT.HANDLE();
                hand.setPointer(Pointer.createConstant(handl));
                result = kernel.GetProcessId(hand);
                f.setAccessible(false);
            }
            //for unix based operating systems
            else if (p.getClass().getName().equals("java.lang.UNIXProcess"))
            {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                result = f.getLong(p);
                f.setAccessible(false);
            }
        }
        catch(Exception ex)
        {
            result = -1;
        }
        return result;
    }

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
