package com.caharkness.megaman.packets;

import com.caharkness.megaman.models.MapEntity;
import com.caharkness.megaman.util.Network;

public class MapPacket extends Packet
{
    public MapPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public MapPacket(MapEntity map)
    {
        String command = PacketType.MAP.getCommand();
        String escaped = Network.escapeMessage(map.serializeMap());

        this.rebuild(
            "?",
            command,
            escaped);
    }

    public String getData()
    {
        return this.getDeserialized()[2];
    }

    public TilePacket[] getTilePackets()
    {
        String unescaped        = Network.unescapeMessage(getData());
        String[] lines          = unescaped.split("\n");
        TilePacket[] packets    = new TilePacket[lines.length];

        int index = 0;
        for (String line : lines)
        {
            packets[index] = (TilePacket) Packet.fromString(Network.unescapeMessage(line));
            index++;
        }

        return packets;
    }
}
