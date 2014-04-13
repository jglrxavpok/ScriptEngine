package org.jglrxavpok.script;


public interface ScriptListener
{

    public boolean onInstruction(ScriptInstruction in);
    
    public void onDownloaded(byte[] buffer, int bytesRead, int index, int max);

    public String formatArg(String string);
}
