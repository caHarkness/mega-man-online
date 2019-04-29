package com.caharkness.megaman.controllers;

import java.awt.Color;
import java.awt.Graphics2D;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.megaman.util.GFX;

public class MapEditorOverlay extends Entity
{
    MapEditor editor;

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;
        this.editor = (MapEditor) this.parent;
    }

    @Override
    public void onStep()
    {
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        for (int i = 0; i < 8; i++)
        {
            g.drawImage(
                Image.get("/sprites/misc.png", 8, 8, 0, 0),
                8,
                8 + (i * 8),
                null,
                null
            );
        }

        // Each bar is 2 pixels
        // 4 * 8
        // 32 hit points

        /*
        PlayerEntity player = ((PlayerEntity) this.parent);

        int bh = 2 * (32 - player.health);
        if (bh > 64)
            bh = 64;

        g.setColor(Color.BLACK);
        g.fillRect(8, 8, 8, bh);
        */

        if (this.editor.timers.containsKey("preview"))
        {
            g.drawImage(
                Image.get(
                    this.editor.tilesets[this.editor.tileset_index],
                    96,
                    96,
                    0,
                    0),
                (int) 24,
                (int) 8,
                null,
                null);

            g.setColor(Color.YELLOW);
            g.drawRect(
                (int) this.editor.col * 16 + 24,
                (int) this.editor.row * 16 + 8,
                16,
                16);

            GFX.drawText(g, 128, 8, this.editor.desc, 15);
        }
    }
}
