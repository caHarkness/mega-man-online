package com.caharkness.engine;

import java.util.HashMap;
import java.util.Map.Entry;

public class Prefs
{
    private static HashMap<String, String> map;

    public static void set(String key, String val)
    {
        map.put(key, val);
    }

    public static String get(String key)
    {
        if (map.containsKey(key))
                return map.get(key);
        else    return null;
    }

    public static void load(String fname)
    {
        String data = IO.read(fname) + "\n";
        String[] lines = data.split("\n");

        for (String ln : lines)
        if (ln.length() > 0 && ln.contains(" = "))
        {
            String key = ln.substring(0, ln.indexOf(" = "));
            String value = ln.substring(ln.indexOf(" = "), ln.length() - 1);
            set(key, value);
        }
    }

    public static void save(String fname)
    {
        String data = "";
        for (Entry<String, String> e : map.entrySet())
            data += String.format(
                "%s = %s\n",
                e.getKey(),
                e.getValue());

        IO.write(fname, data);
    }
}
