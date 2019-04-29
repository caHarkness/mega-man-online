package com.caharkness.megaman.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.engine.Keyboard;
import com.caharkness.engine.Mouse;

public class Palette extends Entity
{
    boolean open;
    float cursor_x;
    float cursor_y;
    int selection_width;
    int selection_height;
    float scroll_x;

    String[] tilesets = new String[]
    {
        "/sprites/tilesets/elecman.png",
        "/sprites/tilesets/fireman.png",
        "/sprites/tilesets/gravityman.png",
        //"/sprites/hardman.png",
        "/sprites/tilesets/green.png",
        "/sprites/tilesets/magnetman.png",
        "/sprites/tilesets/shiny.png",
        "/sprites/tilesets/stoneman.png"
    };

    @Override
    public void onCreate()
    {
        this.open = false;
        this.x = 0;
        this.y = 0;
        this.width = this.engine.screen_width;
        this.height = this.engine.screen_height - 16;

        this.selection_width = 1;
        this.selection_height = 1;
    }

    @Override
    public void onStep()
    {
        this.slideController();
        this.cursorController();
        this.inputController();
    }

    public void slideController()
    {
        if (this.open)
            this.y -= 32;
        else this.y += 32;

        if (this.y < 16)
            this.y = 16;

        if (this.y > this.engine.screen_height)
            this.y = this.engine.screen_height;
    }

    public void cursorController()
    {
        this.cursor_x = Mouse.getX() - this.x;
        this.cursor_y = Mouse.getY() - this.y;

        this.cursor_x = Math.round(this.cursor_x / 16) * 16;
        this.cursor_y = Math.round(this.cursor_y / 16) * 16;
    }

    public void inputController()
    {
        if (this.open)
        {
            if (!Keyboard.check(KeyEvent.VK_TAB))
            {
                Mouse.returnFocus();
                this.open = false;
            }
        }
        else
        {
            if (Keyboard.check(KeyEvent.VK_TAB))
            {
                Mouse.setFocus(this);
                this.open = true;
            }
        }
    }

    @Override
    public void onMouseWheelMoved(MouseWheelEvent e)
    {
        if (e.getWheelRotation() > 0)
            this.scroll_x += 16;

        if (e.getWheelRotation() < 0)
            this.scroll_x -= 16;

        if (this.scroll_x < 0)
            this.scroll_x = 0;
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.setColor(Color.GRAY);
        g.fillRect(
            (int) this.x,
            (int) this.y,
            (int) this.width,
            (int) this.height);

        g.setColor(Color.RED);
        g.drawRect(
            (int) (this.x + this.cursor_x),
            (int) (this.y + this.cursor_y),
            (int) (16 * this.selection_width - 1),
            (int) (16 * this.selection_height - 1));


        int offset_x = 0;

        for (String set : tilesets)
        {
            g.drawImage(
                Image.get(set, 96, 96, 0, 0),
                (int) (this.x - this.scroll_x + offset_x),
                (int) (this.y + 8),
                (int) (96),
                (int) (96),
                null);

            offset_x += 96;
        }
    }
}

class PaletteTile
{

}
