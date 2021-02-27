package pl.bartlomiejstepien.mcsm.domain.model;

public class InstallationStatus
{
    private int percent;
    private String message;

    public InstallationStatus()
    {

    }

    public InstallationStatus(int percent, String message)
    {
        this.percent = percent;
        this.message = message;
    }

    public int getPercent()
    {
        return percent;
    }

    public void setPercent(int percent)
    {
        this.percent = percent;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "InstallationStatus{" +
                "percent=" + percent +
                ", message='" + message + '\'' +
                '}';
    }
}
