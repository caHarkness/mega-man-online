package com.caharkness.megaman.packets;

import java.util.List;

import com.caharkness.megaman.util.Network;

public class Packet
{
    private String raw;
    private String[] deserialized;

    public Packet()
    {

    }

    public Packet(String... data)
    {
        rebuild(data);
    }

    public void rebuild(String... data)
    {
        this.raw = "";

        for (String part : data)
            this.raw += String.format(
                "%s%s",
                part,
                Network.DELIMITER);

        this.raw =
            this.raw.substring(0, this.raw.length() - 1);
    }

    public String[] getDeserialized()
    {
        if (this.deserialized == null)
            this.deserialized = this.raw.split(Network.DELIMITER);

        return this.deserialized;
    }

    public String getSerialized()
    {
        return this.raw;
    }

    public String getId()
    {
        return this.getDeserialized()[0];
    }

    public String getCommand()
    {
        return this.getDeserialized()[1];
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T get(String name)
    {
        List<Parameter> parameters =
            PacketType
                .fromPacket(this)
                .getParameters();

        for (Parameter parameter : parameters)
            if (parameter.getName().equals(name))
                return
                (T) parameter
                    .getType()
                    .cast(parameters.indexOf(parameter));

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> T resolve()
    {
        try
        {
            return
            (T) PacketType
                .fromPacket(this)
                .getPacketClass()
                .getConstructor(Packet.class)
                .newInstance(this);
        }
        catch (Exception x) {}
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Packet> T fromString(String... data)
    {
        try
        {
            return
            (T) PacketType
                .fromString(data)
                .getPacketClass()
                .getConstructor(Packet.class)
                .newInstance(new Packet(data));
        }
        catch (Exception x) {}
        return null;
    }
}
