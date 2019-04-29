package com.caharkness.megaman.packets;

public class ChatPacket extends Packet
{
    public ChatPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public String getMessage()
    {
        return
        this.getDeserialized()[2];
    }
}
