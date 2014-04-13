package org.jglrxavpok.script.test;

import org.jglrxavpok.script.Script;

public class ScriptTest
{

    public static void main(String[] args) throws Exception
    {
        Script script = new Script(IO.readString(ScriptTest.class.getResourceAsStream("/org/jglrxavpok/script/test/script.txt"), "UTF-8"));
        while(script.hasNext())
            script.executeNext();
    }

}
