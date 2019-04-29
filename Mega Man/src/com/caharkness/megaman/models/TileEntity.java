package com.caharkness.megaman.models;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.engine.Sound;
import com.caharkness.megaman.objects.fx.Debris;
import com.caharkness.megaman.objects.fx.Explosion;
import com.caharkness.megaman.packets.TilePacket;

public class TileEntity extends Entity
{
    public String tileset;
    public int row;
    public int col;
    public TileBehavior behavior;
    public int damage;
    public BufferedImage gfx;

    public TilePacket getPacket()
    {
        return
        TilePacket.fromString(
            "?",
            "tile",
            (int) (this.x / 16) + "",
            (int) (this.y / 16) + "",
            this.tileset,
            this.row + "",
            this.col + "",
            this.behavior + "");
    }

    @Override
    public void onCreate()
    {
        this.tags.add("tile");
        this.width = 16;
        this.height = 16;

        //
        //  Tag this entity with the tags supplied
        //  From the ordinal values.
        //
        if (this.behavior.getTags() != null)
        	for (String s : this.behavior.getTags())
        		this.tags.add(s);

        this.z = this.behavior.getZ();
        this.damage = 0;
    }

    @Override
    public void onStep()
    {
        if (this.tags.contains("destructible"))
        {
            while (this.tags.contains("blit"))
                this.tags.remove("blit");

            for (Entity e : this.getCollisionsWithTag(CollisionRect.WHOLE, "shot"))
            {
                this.damage++;
                this.tags.add("blit");

                e.destroy();
                Sound.play("/sounds/snd_machine_hurt.wav");

                this.spawn(new Debris()
                {
                    @Override
                    public void onCreate()
                    {
                        this.source = gfx;
                        super.onCreate();
                        this.x = parent.x + 8;
                        this.y = parent.y + 8;
                    }
                });
            }

            if (this.damage > 9 && !this.tags.contains("exploding"))
            {
                this.tags.add("exploding");
                this.spawn(new Explosion()
                {
                    @Override
                    public void onCreate()
                    {
                        super.onCreate();
                        this.x = parent.x + 8;
                        this.y = parent.y + 8;
                    }

                    @Override
                    public void onDestroy()
                    {
                        for (int i = 0; i < 10; i++)
                        this.spawn(new Debris()
                        {
                            @Override
                            public void onCreate()
                            {
                                this.source = gfx;
                                super.onCreate();
                                this.vel_x *= 4;
                                this.vel_y *= 2;
                                this.x = parent.x + 8;
                                this.y = parent.y + 8;
                            }
                        });

                        parent.destroy();
                    }
                });

                for (int i = 0; i < 4; i++)
                this.spawn(new Debris()
                {
                    @Override
                    public void onCreate()
                    {
                        this.source = gfx;
                        super.onCreate();
                        this.x = parent.x + 8;
                        this.y = parent.y + 8;
                    }
                });
            }
        }
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        if (gfx == null)
            gfx = Image.get(
                this.tileset,
                (int) this.width,
                (int) this.height,
                this.row,
                this.col);

        g.drawImage(
            gfx,
            (int) this.x,
            (int) this.y,
            null,
            null);

        if (this.tags.contains("destructible"))
        {
            boolean blit_anyways =
                this.tags.contains("exploding") &&
                this.age % 2 == 0;

            if (this.tags.contains("blit") || blit_anyways)
            {
                g.setColor(Color.WHITE);
                g.fillRect(
                    (int) this.x,
                    (int) this.y,
                    (int) this.width,
                    (int) this.height);
            }
        }
    }
}
