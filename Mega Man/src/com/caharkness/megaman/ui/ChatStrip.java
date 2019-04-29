package com.caharkness.megaman.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.megaman.util.GFX;

public class ChatStrip extends Entity
{
    public boolean open;
    public String buffer;

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;
        this.open = false;
        this.buffer = "";
    }

    @Override
    public void onStep()
    {
        if (!this.open)
            if (Keyboard.await(KeyEvent.VK_T))
            {
                Keyboard.setFocus(this);
                this.open = true;
                this.buffer = "";
            }

        if (!this.timers.containsKey("blink"))
            this.timers.put("blink", 14);
    }

    @Override
    public void onKeyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            this.open = false;
            Keyboard.returnFocus();
            return;
        }

        if (e.getKeyCode() > 31 && e.getKeyCode() < 127)
        {
            buffer = buffer + e.getKeyChar();
        }

        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            if (buffer.length() > 0)
                buffer = buffer.substring(0, buffer.length() - 1);

        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            this.onEnterKeyPressed();
    }

    public void onEnterKeyPressed()
    {
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        if (this.open)
        {
            g.setColor(new Color(0, 0, 255, 64));
            g.fillRect(
                0,
                this.engine.screen_height - 8,
                this.engine.screen_width,
                8);

            if (this.timers.containsKey("blink"))
                if (this.timers.get("blink") < 7)
                {
                    g.setColor(Color.WHITE);
                    g.fillRect(
                        0 + (8 * buffer.length()),
                        this.engine.screen_height - 8,
                        8,
                        8);
                }

            GFX.drawText(
                g,
                0,
                this.engine.screen_height - 8,
                buffer,
                this.engine.screen_width / 8);
        }
    }
}
