package pl.bartlomiejstepien.mcsm.domain.model;

import java.util.Objects;

public class InstallationStatus
{
    private int phase;
    private int percent;
    private String message;

    public InstallationStatus()
    {

    }

    public InstallationStatus(int phase, int percent, String message)
    {
        this.phase = phase;
        this.percent = percent;
        this.message = message;
    }

    public int getPhase()
    {
        return phase;
    }

    public void setPhase(int phase)
    {
        this.phase = phase;
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstallationStatus that = (InstallationStatus) o;
        return phase == that.phase && percent == that.percent && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(phase, percent, message);
    }

    @Override
    public String toString()
    {
        return "InstallationStatus{" +
                "phase=" + phase +
                ", percent=" + percent +
                ", message='" + message + '\'' +
                '}';
    }
}
