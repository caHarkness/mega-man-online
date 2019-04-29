package com.caharkness.megaman.objects;

import java.awt.Graphics2D;
import java.util.List;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.engine.Sound;

public class WeaponProjectile extends Entity
{
    public int row = 0;

    @Override
    public void onCreate()
    {
        this.tags.add("shot");
        this.timers.put("life", 30 * 4);

        this.z = 0;
        this.width = 8;
        this.height = 8;
        this.row = 0;
    }

    @Override
    public void onStep()
    {
        if (!this.tags.contains("deflected"))
        {
            List<Entity> deflectors = this.getCollisionsWithTag(CollisionRect.WHOLE, "deflective");

            if (deflectors.size() > 0)
            {
                Sound.play("/sounds/player/megaman_classic_reflect.wav");
                this.tags.add("deflected");
                this.vel_x *= -1;
                this.vel_y = -Math.abs(vel_x);
            }
        }

        if (!this.timers.containsKey("life"))
            this.destroy();
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.drawImage(
            Image.get(
                "/sprites/spr_effects_weapon_projectile.png",
                8,
                8,
                this.row,
                0),
            (int) this.x,
            (int) this.y,
            null,
            null
        );
    }
}
