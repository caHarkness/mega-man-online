package com.caharkness.megaman.util;

public class Network
{
    public static final String DELIMITER = "\t";

    public static String[] deserializePacket(String... input)
    {
        String data = "";

        for (String s : input)
            data += String.format("%s%s", s, DELIMITER);

        data = data.substring(0, data.length() - 1);
        return data.split(DELIMITER);
    }

    public static String serializePacket(String... input)
    {
        String output = "";

        for (String s : input)
            output += String.format("%s%s", s, DELIMITER);

        output = output.substring(0, output.length() - 1);
        return output;
    }

    public static String escapeMessage(String s)
    {
        String output = s;

        output = output.replaceAll("\t", "\\\\t");
        output = output.replaceAll("\n", "\\\\n");

        return output;
    }

    public static String unescapeMessage(String s)
    {
        String output = s;

        output = output.replaceAll("\\\\t", "\t");
        output = output.replaceAll("\\\\n", "\n");

        return output;
    }
}
