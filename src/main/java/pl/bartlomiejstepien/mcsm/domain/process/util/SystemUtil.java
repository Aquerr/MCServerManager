package pl.bartlomiejstepien.mcsm.domain.process.util;

public class SystemUtil
{
    public static boolean isWindows()
    {
        final String osName = System.getProperty("os.name");
        return osName.startsWith("Win") || osName.startsWith("win");
    }
}
