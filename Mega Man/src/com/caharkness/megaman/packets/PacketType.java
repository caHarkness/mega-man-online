package com.caharkness.megaman.packets;

import java.util.ArrayList;
import java.util.List;

import com.caharkness.megaman.models.PlayerAppearance;
import com.caharkness.megaman.models.TileBehavior;
import com.caharkness.megaman.util.Network;

@SuppressWarnings("serial")
public enum PacketType
{
    INVALID(null, null, null),
    UNIMPLEMENTED(null, null, null),
    INCOMPLETE(null, null, null),

    UNKNOWN(
        null,
        Packet.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
        }}),

    PLAYER(
        "player",
        PlayerPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
            this.add(new Parameter("x",         Float.class));
            this.add(new Parameter("y",         Float.class));
            this.add(new Parameter("vel_x",     Float.class));
            this.add(new Parameter("vel_y",     Float.class));
            this.add(new Parameter("state",     Byte.class));
            // this.add(new Parameter("name",      String.class));
        }}),

    SHOOT(
        "shoot",
        ShootPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
            this.add(new Parameter("x",         Float.class));
            this.add(new Parameter("y",         Float.class));
            this.add(new Parameter("vel_x",     Float.class));
        }}),

    TILE(
        "tile",
        TilePacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
            this.add(new Parameter("tx",        Integer.class));
            this.add(new Parameter("ty",        Integer.class));
            this.add(new Parameter("tileset",   String.class));
            this.add(new Parameter("row",       Integer.class));
            this.add(new Parameter("col",       Integer.class));
            this.add(new Parameter("behavior",  TileBehavior.class));
        }}),

    MAP(
        "map",
        MapPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
            this.add(new Parameter("data",      String.class));
        }}),

    SYNC(
        "sync",
        SyncPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
        }}),

    CHAT(
        "chat",
        ChatPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
            this.add(new Parameter("message",   String.class));
        }}),

    DISCONNECT(
        "disconnect",
        DisconnectPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",        String.class));
            this.add(new Parameter("command",   String.class));
        }}),

    MARCO(
        "marco",
        MarcoPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",            String.class));
            this.add(new Parameter("command",       String.class));
            this.add(new Parameter("name",          String.class));
            this.add(new Parameter("appearance",    PlayerAppearance.class));
        }}),

    POLO(
        "polo",
        PoloPacket.class,
        new ArrayList<Parameter>()
        {{
            this.add(new Parameter("id",            String.class));
            this.add(new Parameter("command",       String.class));
            this.add(new Parameter("name",          String.class));
            this.add(new Parameter("appearance",    PlayerAppearance.class));
        }});

    //
    //
    //
    //
    //

    private String command;
    private List<Parameter> parameters;
    private Class<? extends Packet> cls;

    private PacketType(String command, Class<? extends Packet> cls, List<Parameter> parameters)
    {
        this.command    = command;
        this.cls        = cls;
        this.parameters = parameters;
    }

    public String getCommand()
    {
        return this.command;
    }

    public Class<? extends Packet> getPacketClass()
    {
        return this.cls;
    }

    public List<Parameter> getParameters()
    {
        return this.parameters;
    }

    //
    //
    //
    //
    //

    public static PacketType fromString(String... data)
    {
        String raw = "";

        for (String part : data)
            raw += String.format(
                "%s%s",
                part,
                Network.DELIMITER);

        raw             = raw.substring(0, raw.length() - 1);
        String[] parts  = raw.split(Network.DELIMITER);

        if (parts.length < 2 || parts[0].length() < 1)
            return PacketType.INVALID;

        for (PacketType type : PacketType.values())
            if (parts[1].equals(type.getCommand()))
                if (parts.length >= type.getParameters().size())
                        return type;
                else    return INCOMPLETE;

        return UNIMPLEMENTED;
    }

    public static PacketType fromPacket(Packet packet)
    {
        return fromString(
            packet.getSerialized());
    }
}

class Parameter
{
    private String name;
    private Class<?> type;

    public Parameter(String name, Class<?> type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return this.name;
    }

    public Class<?> getType()
    {
        return this.type;
    }
}
