package com.caharkness.megaman.objects;

import java.awt.Graphics2D;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.megaman.models.PlayerEntity;
import com.caharkness.megaman.ui.PlayerCamera;

public class PlayerSpawn extends Entity
{
    int f = 0;

    @Override
    public void onCreate()
    {
        this.x = this.engine.screen_width / 2 - 100;
        this.y = -100;
        this.vel_y = 10;
        this.width = 32;
        this.height = 32;

    }

    @Override
    public void onStep()
    {
        if (this.f == 2)
        {
            this.onLand();
            this.destroy();
        }

        if (this.f == 1)
            this.f = 2;

        for (Entity e : this.getCollisionsWithTag(CollisionRect.VERTICAL, "solid"))
        {
            if (this.y + this.height > e.y)
            {
                this.vel_y = 0;
                this.y = e.y - this.height;
                f++;
            }
            break;
        }
    }

    public void onLand()
    {
        PlayerEntity e =
        (PlayerEntity) this.spawn(new PlayerEntity()
        {
            @Override
            public void onCreate()
            {
                super.onCreate();
                this.x = PlayerSpawn.this.x + 8;
                this.y = PlayerSpawn.this.y;
                this.spawn(new PlayerCamera());
            }
        });
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.drawImage(
            Image.get("/sprites/beam.png", 32, 32, 0, this.f),
            (int) this.x,
            (int) this.y,
            null);
    }
}
