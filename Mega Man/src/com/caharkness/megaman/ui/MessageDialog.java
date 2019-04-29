package com.caharkness.megaman.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.engine.Mouse;
import com.caharkness.megaman.util.GFX;

public class MessageDialog extends Entity
{
    public String message;

    public MessageDialog(String prompt)
    {
        this.message = prompt;
    }

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;

        this.x = 0;
        this.y = 0 + this.engine.screen_height / 4;
        this.width = this.engine.screen_width;
        this.height = this.engine.screen_height / 2;

        Keyboard.setFocus(this);
        Mouse.setFocus(this);
    }

    @Override
    public void onStep()
    {
        if (!this.timers.containsKey("blink"))
            this.timers.put("blink", 14);
    }

    @Override
    public void onKeyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            this.onEnterKeyPressed();
    }

    public void onEnterKeyPressed()
    {
        Keyboard.returnFocus();
        Mouse.returnFocus();
        this.destroy();
    }

    @Override
    public void onDraw(Graphics2D g)
    {
        g.setColor(new Color(0, 0, 255, 64));
        g.fillRect(
            (int) this.x,
            (int) this.y,
            (int) this.width,
            (int) this.height);

        GFX.drawText(
            g,
            (int) this.x + 8,
            (int) this.y + 8,
            this.message,
            this.engine.screen_width / 8 - 8);
    }
}
