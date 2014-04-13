package org.jglrxavpok.script;

public class ScriptVariable
{

    public static final ScriptVariable NULL_VAR = new ScriptVariable("null");

    private String name;
    private String value;

    public ScriptVariable(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String string)
    {
        value = string;
    }
}
