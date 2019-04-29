package com.caharkness.megaman.packets;

import com.caharkness.megaman.models.TileBehavior;
import com.caharkness.megaman.models.TileEntity;

public class TilePacket extends Packet
{
    public TilePacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public TilePacket(String... packlets)
    {
        super(packlets);
    }

    public int getTileX()
    {
        return Integer.parseInt(this.getDeserialized()[2]);
    }

    public int getTileY()
    {
        return Integer.parseInt(this.getDeserialized()[3]);
    }

    public String getTileset()
    {
        return this.getDeserialized()[4];
    }

    public int getRow()
    {
        return Integer.parseInt(this.getDeserialized()[5]);
    }

    public int getColumn()
    {
        return Integer.parseInt(this.getDeserialized()[6]);
    }

    public TileBehavior getBehavior()
    {
        return TileBehavior.valueOf(this.getDeserialized()[7]);
    }

    public TileEntity getResultingEntity()
    {
        return
        new TileEntity()
        {
            @Override
            public void onCreate()
            {
                this.x          = TilePacket.this.getTileX() * 16f;
                this.y          = TilePacket.this.getTileY() * 16f;
                this.tileset    = TilePacket.this.getTileset();
                this.row        = TilePacket.this.getRow();
                this.col        = TilePacket.this.getColumn();
                this.behavior   = TilePacket.this.getBehavior();
                super.onCreate();
            }
        };
    }
}
