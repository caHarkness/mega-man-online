package com.caharkness.megaman.packets;

public class DisconnectPacket extends Packet
{
    public DisconnectPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }
}
