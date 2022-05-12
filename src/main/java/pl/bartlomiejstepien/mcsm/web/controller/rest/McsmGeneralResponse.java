package pl.bartlomiejstepien.mcsm.web.controller.rest;

public class McsmGeneralResponse
{
    private int status;
    private String message;

    public McsmGeneralResponse(int status, String message)
    {
        this.status = status;
        this.message = message;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
