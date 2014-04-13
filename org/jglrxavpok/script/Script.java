package org.jglrxavpok.script;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;

public class Script
{

    public static boolean DEBUG = true;
    
    
    private String script;
    private ArrayList<ScriptInstruction> instructions;
    private int index;
    private ScriptListener listener;
    private ArrayList<ScriptVariable> variables;
    private OutputStream outStream;
    private int currentLine;


    private String scriptName;

    public Script(String script)
    {
        this.script = script;
        int maxIndex = 10;
        if(maxIndex >= script.length())
            maxIndex = script.length()-1;
        scriptName = script.substring(0, maxIndex).trim().replace("\n", " ").replace("\r", " ");
        this.outStream = System.out;
        variables = new ArrayList<ScriptVariable>();
        parseScript(script);
    }
    
    public Script setName(String n)
    {
        this.scriptName = n;
        return this;
    }
    
    public String getEntireScript()
    {
        return script;
    }
    
    public void setListener(ScriptListener l)
    {
        listener = l;
    }
    
    private void parseScript(String s)
    {
        String[] lines = s.split("\r|\n");
        instructions = new ArrayList<ScriptInstruction>(); 
        int lineNbr = 0;
        for(String line : lines)
        {
            if(line.isEmpty())
            {
                continue;
            }
            currentLine++;
            while(line.charAt(0) == ' ' || line.charAt(0) == '\t')
            {
                line = line.substring(1);
            }
            String parts[] = line.split(" ");
            String instruction = parts[0];
            String[] args = new String[0];
            if(doesInstructionHasArgs(instruction))
            {
                args = splitCorrectly(parts, 1, parts.length);
            }
            instructions.add(new ScriptInstruction(instruction, args, lineNbr++));
        }
    }
    
    private String[] splitCorrectly(String[] parts, int i, int length)
    {
        if(parts.length >= length && i <= parts.length && i >= 0)
        {
            ;
        }
        else
        {
            throw new ScriptParseException("Invalid index/length");
        }
        ArrayList<String> list = new ArrayList<String>();
        String toSplit = new String();
        for(int index = i;index<length;index++)
        {
            toSplit+=(index-i == 0 ? "" : " ")+parts[index];
        }
        if(toSplit.trim().isEmpty())
        {
            return new String[0];
        }
        if(DEBUG)
        System.err.println("Received: "+toSplit);
        
        boolean inDoubleQuote = false;
        boolean inSingleQuote = false;
        char previous = '\0';
        String current = new String();
        for(int index = 0;index<toSplit.length();index++)
        {
            char c = toSplit.charAt(index);
            if(c == '\r' || c == '\n')
            {
                if(inDoubleQuote)
                {
                    throw new ScriptParseException("Unbalanced quote at line: "+currentLine);
                }
                else if(inSingleQuote)
                {
                    throw new ScriptParseException("Unbalanced quote at line: "+currentLine);
                }
            }
            if(previous == '\\')
            {
                if(c == '\\')
                {
                    current+='\\';
                }
                else if(c == '"')
                {
                    current+='"';
                }
            }
            else
            {
                if(c == '"')
                {
                    inDoubleQuote = !inDoubleQuote;
                }
                else if(c == '\'')
                {
                    inSingleQuote = !inSingleQuote;
                }
                else if(c == ' ')
                {
                    if(inDoubleQuote || inSingleQuote)
                    {
                        current+=" ";
                    }
                    else
                    {
                        list.add(current);
                        current = new String();
                        previous = '\0';
                    }
                }
                else
                {
                    current+=c;
                }
            }
            
            
            previous = c;
        }
        if(inDoubleQuote)
        {
            throw new ScriptParseException("Unbalanced quote at line: "+currentLine);
        }
        else if(inSingleQuote)
        {
            throw new ScriptParseException("Unbalanced quote at line: "+currentLine);
        }
        list.add(current);
        return list.toArray(new String[0]);
    }

    public void injectIntoClasspath(URL path)
    {
        try
        {
            URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class<?>[]{URL.class}); 
            addURL.setAccessible(true);
            addURL.invoke(classLoader, new Object[]{path});
            System.out.println("Injected "+path);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private int executeInstruction(ScriptInstruction in) throws ScriptParseException
    {
        if(listener == null || listener.onInstruction(in))
        try
        {
            String instruction = in.getInstruction();
            String[] args = in.getArgs();
            if(instruction == null || instruction.equals(""))
            {
                return 0;
            }
            if(instruction.equals("exit"))
            {
                return -1; // Stops the script
            }
            for(int i = 0;i<args.length;i++)
            {
                if(args[i].startsWith("$") && getVarByName(args[i].replace("$", "")) != ScriptVariable.NULL_VAR)
                {
                    args[i] = getVarByName(args[i].replace("$", "")).getValue();
                }
                else
                    args[i] = format(args[i]);
            }
            if(instruction.equals("download"))
            {
                download(getArg(args, 0, in.getLine(), "source url"), getArg(args, 1, in.getLine(), "destination path"));
            }
            else if(instruction.equals("updateclasspath"))
            {
                if(getArg(args, 0, in.getLine()).equals("add"))
                {
                    injectIntoClasspath(new File(getArg(args, 1, in.getLine())).toURI().toURL());
                }
            }
            else if(instruction.equals("var"))
            {
                String name = getArg(args, 0, in.getLine(), "variable name");
                if(name.startsWith("$"))
                    throw new ScriptParseException("Invalid variable name: "+name+ " at ("+scriptName+",line:"+in.getLine()+")");
                ScriptVariable var = new ScriptVariable(name);
                register(var);
                
                if(args.length >= 2)
                {
                    String operator = getArg(args, 1, in.getLine(), "operator");
                    if(operator.equals("="))
                    {
                        var.setValue(getArg(args,2,in.getLine(), "value"));
                    }
                    else
                    {
                        throw new ScriptParseException("Expected '=' at index 1 (at "+scriptName+", line:"+in.getLine()+")");
                    }
                }
            }
            else if(instruction.equals("free"))
            {
                free(getArg(args, 0, in.getLine(), "variable"));
            }
            else if(instruction.equals("print"))
            {
                this.outStream.write(getArg(args, 0, in.getLine(), "text or variable to display").getBytes());
            }
            else if(instruction.equals("println"))
            {
                this.outStream.write("\n".getBytes());
                if(args.length > 0)
                    this.outStream.write(getArg(args, 0, in.getLine(), "text or variable to display").getBytes());
            }
            else if(instruction.startsWith("$"))
            {
                String varName = instruction.replace("$", "");
                ScriptVariable var = getVarByName(varName);
                if(var == ScriptVariable.NULL_VAR)
                {
                    throw new ScriptParseException(varName+" is not a valid variable. (at "+scriptName+",line:"+in.getLine()+")");   
                }
                else
                {
                    if(getArg(args, 0, in.getLine(),"operator").equals("="))
                    {
                        var.setValue(getArg(args, 1, in.getLine(),"value"));
                    }
                }
            }
            else
            {
                throw new ScriptParseException("Unknown instruction: "+instruction+" (Line "+in.getLine()+")");
            }
            return 0;
        }
        catch(Exception e)
        {
            throw new ScriptParseException(e);
        }
        return 0;
    }
    
    private String getArg(String[] args, int i, int line)
    {
        return getArg(args, i, line, ""+i);
    }
    
    private String getArg(String[] args, int i, int line, String name)
    {
        if(i >= args.length)
        {
            throw new ScriptParseException("Missing argument '"+name+"' at index "+i+" of line "+line);
        }
        return args[i];
    }

    @SuppressWarnings("unused")
    private void free(ScriptVariable var)
    {
        variables.remove(var);
    }
    
    private void free(String string)
    {
        variables.remove(getVarByName(string));
    }

    private void register(ScriptVariable var)
    {
        variables.add(var);
    }
    
    private ScriptVariable getVarByName(String name)
    {
        for(ScriptVariable var : variables)
        {
            if(name.equals(var.getName()))
                return var;
        }
        return ScriptVariable.NULL_VAR;
    }

    private void download(String url, String dstPath) throws Exception
    {
        URL src = new URL(url);
        File dst = new File(dstPath);
        if(!dst.getParentFile().exists())
            dst.getParentFile().mkdirs();
        dst.createNewFile();
        URLConnection connection = src.openConnection();
        BufferedInputStream input = new BufferedInputStream(src.openStream());
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dst));
        int i = 0;
        byte[] buffer = new byte[65565];
        int index = 0;
        int max = connection.getContentLength();
        while((i = input.read(buffer, 0, buffer.length)) != -1)
        {
            output.write(buffer, 0, i);
            index+=i;
            if(listener != null)
            listener.onDownloaded(buffer, i, index, max);
        }
        output.flush();
        output.close();
        input.close();
    }

    private String format(String string)
    {
        if(listener != null)
            string = listener.formatArg(string);
        return string;
    }

    private boolean doesInstructionHasArgs(String instruction)
    {
        if(instruction.equals("exit"))
        {
            return false;
        }
        return true;
    }
    
    public boolean hasNext()
    {
        if(index == -1)
            return false;
        return index < instructions.size();
    }

    public void executeNext()
    {
        if(hasNext())
        {
            if(this.executeInstruction(instructions.get(index)) != -1)
                index++;
            else
                index = -1;
        }
    }
}
