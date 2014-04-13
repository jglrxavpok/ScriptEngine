package org.jglrxavpok.script.security;

/**
@author jglrxavpok
<br/>
The MIT License (MIT)<br/>
<br/>
Copyright (c) 2014 <br/>
<br/>
Permission is hereby granted, free of charge, to any person obtaining a copy<br/>
of this software and associated documentation files (the "Software"), to deal<br/>
in the Software without restriction, including without limitation the rights<br/>
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br/>
copies of the Software, and to permit persons to whom the Software is<br/>
furnished to do so, subject to the following conditions:<br/>
<br/>
The above copyright notice and this permission notice shall be included in all<br/>
copies or substantial portions of the Software.<br/>
<br/>
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br/>
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br/>
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br/>
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br/>
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br/>
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE<br/>
SOFTWARE.<br/>
 */
public final class ScriptSecurity
{

    public static enum Modes
    {
        ANARCHY, SHOULD_REQUEST, BLOCK_EVERYTHING, ONLY_BLOCK_CRITICAL
    }
    
    public static enum Permissions
    {
        DOWNLOAD("download", false), ALTER_CLASSPATH("updateclasspath", true);
        
        private String permID;
        private boolean critical;

        Permissions(String id, boolean critical)
        {
            this.permID = id;
            this.critical = critical;
        }
        
        public String getPermissionID()
        {
            return permID;
        }

        public static Permissions get(String string)
        {
            for(Permissions p : values())
            {
                if(p.permID.equals(string))
                    return p;
            }
            return null;
        }

        public boolean isCritical()
        {
            return critical;
        }
    }
    
    
    public static class ScriptSecurityException extends Exception
    {

        private static final long serialVersionUID = 525368989059033003L;

        public ScriptSecurityException(Exception e)
        {
            super(e);
        }

        public ScriptSecurityException(String string)
        {
            super(string);
        }
    }

    
}
