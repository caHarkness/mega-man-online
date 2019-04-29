package com.caharkness.megaman.models;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public enum TileBehavior
{
    VOID((byte) 0, null, null, 0f, null),

    BACKGROUND(
        (byte) 10,
        "Background",
        "This tile will be drawn in the background",
        -8f,
        new ArrayList<String>()
        {{
            this.add("background");
        }}),

    LADDER(
        (byte) 20,
        "Ladder",
        "Players can climb this tile",
        -8f,
        new ArrayList<String>()
        {{
            this.add("background");
            this.add("ladder");
        }}),

    DEFLECTIVE(
        (byte) 30,
        "Deflective",
        "This tile is solid and deflects player projectile",
        -2f,
        new ArrayList<String>()
        {{
            this.add("solid");
            this.add("deflective");
        }}),

    DESTRUCTIBLE(
        (byte) 40,
        "Destructible",
        "Players can shoot and break this solid tile",
        -2f,
        new ArrayList<String>()
        {{
            this.add("solid");
            this.add("destructible");
        }}),

    SOLID(
        (byte) 50,
        "Solid",
        "Players can stand on this tile",
        -2f,
        new ArrayList<String>()
        {{
            this.add("solid");
        }}),

    FOREGROUND(
        (byte) 60,
        "Foreground",
        "This tile will be drawn in the foreground",
        8f,
        new ArrayList<String>()
        {{
            this.add("foreground");
        }}),

    DEADLY(
        (byte) 70,
        "Deadly",
        "Players will die on contact with this tile",
        -2f,
        new ArrayList<String>()
        {{
            this.add("deadly");
        }});

    //
    //
    //
    //
    //

    private byte b;
    private String title;
    private String description;
    private final float z;
    private final List<String> tags;

    TileBehavior(
        byte b,
        String title,
        String description,
        float z,
        List<String> tags)
    {
        this.b = b;
        this.title = title;
        this.description = description;
        this.z = z;
        this.tags = tags;
    }

    public byte toByte()
    {
        return
            this.b;
    }

    public String getTitle()
    {
        return
            this.title;
    }

    public String getDescription()
    {
        return
            this.description;
    }

    public float getZ()
    {
        return
            this.z;
    }

    public List<String> getTags()
    {
        return
            this.tags;
    }

    //
    //
    //
    //
    //

    public static TileBehavior fromByte(byte b)
    {
        for (TileBehavior behavior : TileBehavior.values())
            if (b == behavior.toByte())
                return behavior;

        return VOID;
    }

    public static List<TileBehavior> getPlaceableTiles()
    {
        return new ArrayList<TileBehavior>()
        {{
            this.add(TileBehavior.BACKGROUND);
            this.add(TileBehavior.LADDER);
            this.add(TileBehavior.DEFLECTIVE);
            this.add(TileBehavior.DESTRUCTIBLE);
            this.add(TileBehavior.SOLID);
            this.add(TileBehavior.FOREGROUND);
            this.add(TileBehavior.DEADLY);
        }};
    }
}
