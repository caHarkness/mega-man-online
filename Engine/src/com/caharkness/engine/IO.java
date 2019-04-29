package com.caharkness.engine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IO
{
    public static void log(String format, Object... objects)
    {
        System.out.println(
            String.format(format, objects));
    }

    public static void log(Exception x)
    {
        log("[Exception] %s", x.getMessage());

        Throwable cause = x.getCause();
        if (cause != null)
            log("Cause: ", cause.getMessage());

        for (StackTraceElement step : x.getStackTrace())
        {
            log("    in file %s on line %s",
                step.getFileName(),
                step.getLineNumber());
        }
    }

    /**
     *  Reads an entire file to a string.
     */
    public static String read(String fname)
    {
        try
        {
            FileReader reader = new FileReader(fname);
            String contents = "";

            int b;
            while ((b = reader.read()) > -1)
                contents += (char) b;

            reader.close();
            return contents;
        }
        catch (Exception x) { x.printStackTrace(); }
        return null;
    }

    /**
     * Returns a filtered list of matches.
     */
    public static List<String> matches(String data, String regex, int group)
    {
        ArrayList<String> list = new ArrayList<>();
        try
        {
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(data);

            while (matcher.find())
                list.add(matcher.group(group));
        }
        catch (Exception x) { x.printStackTrace(); }
        return list;
    }

    /**
     * Returns the first match from {@code matches()}.
     */
    public static String match(String data, String regex, int group)
    {
        List<String> list = matches(data, regex, group);

        if (list.size() > 0)
                return list.get(0);
        else    return null;
    }

    /**
     * Writes an entire string to a file. (Overwriting)
     */
    public static boolean write(String fname, String data)
    {
        try
        {
            FileWriter
                writer = new FileWriter(fname);
                writer.write(data);
                writer.flush();
                writer.close();

            return true;
        }
        catch (Exception x) { x.printStackTrace(); }
        return false;
    }

    /**
     * Appends one or more lines to a file.
     */
    public static boolean writeln(String fname, String... lines)
    {
        try
        {
            FileWriter writer = new FileWriter(fname, true);
            for (String line : lines)
                if (line != null)
                    writer.append(line + "\n");

            writer.flush();
            writer.close();
            return true;
        }
        catch (Exception x) { x.printStackTrace(); }
        return false;
    }

    /**
     * Creates an empty file.
     */
    public static boolean touch(String fname)
    {
        if (isFree(fname))
        try
        {
            File
                file = new File(fname);
                file.getParentFile().mkdirs();
                file.createNewFile();

            return true;
        }
        catch (Exception x) { x.printStackTrace(); }
        return false;
    }

    /**
     * Writes a copy of the file contents to the destination path.
     */
    public static boolean copy(String fname, String dest)
    {
        if (isFree(dest))
            return write(dest, read(fname));
        else
        return false;
    }

    /**
     * Moves a file to the destination path.
     */
    public static boolean move(String fname, String dest)
    {
        if (copy(fname, dest))
            return delete(fname);
            return false;
    }

    /**
     * Deletes the specified file from the file system.
     */
    public static boolean delete(String fname)
    {
        try
        {
            File
                file = new File(fname);
                file.delete();

            return true;
        }
        catch (Exception x) { x.printStackTrace(); }
        return false;
    }

    /**
     * Returns the name of the file at the specified path.
     */
    public static String name(String fname)
    {
        return new File(fname).getName();
    }

    /**
     * Returns whether the specified path represents a file.
     */
    public static boolean isFile(String fname)
    {
        File file = new File(fname);
        return
            file.exists() &&
            file.isFile();
    }

    /**
     * Returns whether the specified path represents a directory.
     */
    public static boolean isDirectory(String dir)
    {
        File file = new File(dir);
        return
            file.exists() &&
            file.isDirectory();
    }

    /**
     * Returns true when no object is found at the specified path.
     */
    public static boolean isFree(String path)
    {
        File file = new File(path);
        return
            !isFile(path) &&
            !isDirectory(path) &&
            !file.exists();
    }

    /**
     * Returns a filtered list of paths to items found in the specified directory.
     */
    public static List<String> list(String dir, String regex)
    {
        ArrayList<String> items = new ArrayList<>();
        File container          = new File(dir);

        for (String fname : container.list())
            if (fname.matches(regex))
                items.add(String.format("%s/%s", dir, fname));

        container = null;
        return items;
    }

    /**
     * Returns a list of paths to items found in the specified directory.
     */
    public static List<String> list(String dir)
    {
        return list(dir, ".*");
    }

    /**
     *
     */
    public static boolean makePaths(String... dirs)
    {
        for (String dir : dirs)
            if (isFree(dir))
                new File(dir).mkdirs();

        for (String dir : dirs)
            if (!isDirectory(dir))
                return false;

        return true;
    }

    /**
     * Return a filtered list of paths for all file system objects found recursively in the specified directory.
     */
    public static List<String> recurse(String dir, String regex)
    {
        ArrayList<String> list = new ArrayList<>();

        for (String path : list(dir))
        {
            String p = path.replaceAll("^\\.\\/", "");

            if (p.matches(regex))
                list.add(p);

            if (isDirectory(p))
            {
                List<String> sublist = recurse(p, regex);
                for (String subpath : sublist)
                {
                    String s = subpath.replaceAll("\\.\\/", "");

                    if (s.matches(regex))
                        list.add(s);
                }
            }
        }

        return list;
    }

    /**
     * Expands and restores concatenated files back to their original location.
     */
    public static boolean unpack(String fname)
    {
        if (isFile(fname))
        try
        {
            String all = read(fname);

            //
            //  Most complicated regular expression written to date
            //  By Conner Harkness on 7/15/2017
            //
            List<String> matches = matches(all, "(.*?\\n|^)(--\\[.+?\\]\\r?\\n.*?(?=(\\r?\\n--\\[|$))).*?", 2);

            for (String m : matches)
            {
                String section = match(m, "^--\\[(.+?)].*$", 1);
                String data = match(m, "^--\\[.+?]\\r?\\n(.*)$", 1);

                if (isDirectory(section))
                    throw new RuntimeException("Cannot expand section to a folder.");

                touch(section);
                write(section, data);
            }
        }
        catch (Exception x) { x.printStackTrace(); }
        return false;
    }

    /**
     * Concatenates all matches found into a single file that can later be expanded.
     */
    public static boolean pack(String dir, String regex, String fname)
    {
        if (isDirectory(dir))
        try
        {
            String dump = "";
            List<String> list = recurse(dir, regex);

            for (String item : list)
                dump += String.format("--[%s]\n%s\n", item, read(item));

            write(fname, dump);
        }
        catch (Exception x) { x.printStackTrace(); }
        return false;
    }
}
