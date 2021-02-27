package pl.bartlomiejstepien.mcsm.web.controller.rest;

public class RestErrorResponse
{
    private int status;
    private String message;

    public RestErrorResponse()
    {

    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }

    public String getMessage()
    {
        return message;
    }

    public RestErrorResponse(int status, String message)
    {
        this.status = status;
        this.message = message;
    }
}
