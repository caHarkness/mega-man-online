package com.caharkness.megaman.packets;

public class MarcoPacket extends Packet
{
    public MarcoPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public String getName()
    {
        return
        this.getDeserialized()[2];
    }

    public int getAppearanceIndex()
    {
        return
        Integer.parseInt(this.getDeserialized()[3]);
    }
}
