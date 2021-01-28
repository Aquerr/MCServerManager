package pl.bartlomiejstepien.mcsm.exception;

public class CouldNotDownloadServerFilesException extends Exception
{
    public CouldNotDownloadServerFilesException()
    {
        super();
    }

    public CouldNotDownloadServerFilesException(String message)
    {
        super(message);
    }

    public CouldNotDownloadServerFilesException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
