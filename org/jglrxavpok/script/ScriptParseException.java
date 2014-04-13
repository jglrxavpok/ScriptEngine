package org.jglrxavpok.script;


public class ScriptParseException extends RuntimeException
{

    public ScriptParseException(Exception e)
    {
        super(e);
    }

    public ScriptParseException(String string)
    {
        super(string);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 5003928557499954716L;

}
