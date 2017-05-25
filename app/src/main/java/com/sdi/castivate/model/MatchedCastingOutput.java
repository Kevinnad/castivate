package com.sdi.castivate.model;

/**
 * Created by nijamudhin on 3/14/2017.
 */

public class MatchedCastingOutput {
    private String message;

    private String code;

    private CastingList CastingList;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public CastingList getCastingList ()
    {
        return CastingList;
    }

    public void setCastingList (CastingList CastingList)
    {
        this.CastingList = CastingList;
    }

}
