package com.caharkness.megaman.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Keyboard;
import com.caharkness.megaman.util.GFX;

public class QuestionDialog extends Entity
{
    public String prompt;
    public String buffer;

    public QuestionDialog(String prompt)
    {
        this.prompt = prompt;
    }

    @Override
    public void onCreate()
    {
        this.placement = Placement.IN_FOREGROUND;
        this.buffer = "";

        this.x = 0;
        this.y = 0 + this.engine.screen_height / 4;
        this.width = this.engine.screen_width;
        this.height = this.engine.screen_height / 2;

        Keyboard.setFocus(this);
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
        g.setColor(new Color(0, 0, 255, 128));
        g.fillRect(
            (int) this.x,
            (int) this.y,
            (int) this.width,
            //(int) this.height);
            32);

        g.setColor(new Color(0, 0, 0, 64));
        g.fillRect(
            (int) this.x + 8,
            (int) this.y + 16,
            (int) this.width - 16,
            8);

        if (this.timers.containsKey("blink"))
            if (this.timers.get("blink") < 7)
            {
                g.setColor(Color.WHITE);
                g.fillRect(
                    (int) this.x + 8 + (8 * buffer.length()),
                    (int) this.y + 16,
                    8,
                    8);
            }

        GFX.drawText(
            g,
            (int) this.x + 8,
            (int) this.y + 8,
            this.prompt,
            this.engine.screen_width / 8);

        GFX.drawText(
            g,
            (int) this.x + 8,
            (int) this.y + 16,
            buffer,
            this.engine.screen_width / 8);
    }
}
