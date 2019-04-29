package com.caharkness.megaman.packets;

import com.caharkness.megaman.models.PlayerEntity;

public class PlayerPacket extends Packet
{
    public PlayerPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public PlayerPacket(PlayerEntity p)
    {
        super(
        new String[]
        {
            "?",
            "player",
            (int) Math.round(p.x) + "",
            (int) Math.round(p.y) + "",
            (int) Math.round(p.vel_x) + "",
            (int) Math.round(p.vel_y) + "",
            p.state + ""
        });
    }

    public float getX()
    {
        return
        Float.parseFloat(this.getDeserialized()[2]);
    }

    public float getY()
    {
        return
        Float.parseFloat(this.getDeserialized()[3]);
    }

    public float getVelocityX()
    {
        return
        Float.parseFloat(this.getDeserialized()[4]);
    }

    public float getVelocityY()
    {
        return
        Float.parseFloat(this.getDeserialized()[5]);
    }

    public byte getState()
    {
        return
        Byte.parseByte(this.getDeserialized()[6]);
    }
}
