package org.jglrxavpok.script.test;

import org.jglrxavpok.script.Script;

/**
 * No license here, that's just a test file, do whatever you want with it
 * @author jglrxavpok
 *
 */
public class ScriptTest
{

    public static void main(String[] args) throws Exception
    {
        Script script = new Script(IO.readString(ScriptTest.class.getResourceAsStream("/org/jglrxavpok/script/test/test.script"), "UTF-8"), "Test script");
        while(script.hasNext())
            script.executeNext();
    }

}
