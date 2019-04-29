package com.caharkness.megaman.packets;

public class SyncPacket extends Packet
{
    public SyncPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public SyncPacket()
    {
        super("?",
            PacketType.SYNC.getCommand());
    }
}
