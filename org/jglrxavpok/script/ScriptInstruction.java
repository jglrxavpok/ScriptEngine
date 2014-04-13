package org.jglrxavpok.script;


public class ScriptInstruction
{

    private String instruction;
    private String[] args;
    private int lineNumber;

    public ScriptInstruction(String instruction, String[] args, int lineNumber)
    {
        this.instruction = instruction;
        this.lineNumber = lineNumber;
        this.args = args;
    }
    
    public String getInstruction()
    {
        return instruction;
    }
    
    public String[] getArgs()
    {
        return args;
    }

    public int getLine()
    {
        return lineNumber;
    }
}
