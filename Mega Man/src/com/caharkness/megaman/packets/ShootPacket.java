package com.caharkness.megaman.packets;

import com.caharkness.megaman.objects.WeaponProjectile;

public class ShootPacket extends Packet
{
    public ShootPacket(Packet packet)
    {
        super(packet.getDeserialized());
    }

    public float getX()
    {
        return Float.parseFloat(this.getDeserialized()[2]);
    }

    public float getY()
    {
        return Float.parseFloat(this.getDeserialized()[3]);
    }

    public float getVelocityX()
    {
        return Float.parseFloat(this.getDeserialized()[4]);
    }

    public WeaponProjectile getResultingEntity()
    {
        return
        new WeaponProjectile()
        {
            @Override
            public void onCreate()
            {
                super.onCreate();
                this.x = ShootPacket.this.getX();
                this.y = ShootPacket.this.getY();
                this.vel_x = ShootPacket.this.getVelocityX();
                this.tags.add("hazard");
            }
        };
    }
}
