package com.caharkness.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LiteEngine extends JPanel implements Runnable
{
    private static final long serialVersionUID = 3032294362501311883L;

    public int screen_width         = 640;
    public int screen_height        = 480;
    public int target_framerate     = 30;
    public int timer                = 30;
    public float mouse_sx           = 0;
    public float mouse_sy           = 0;
    public boolean running          = false;
    public Thread thread            = null;
    public BufferedImage image      = null;
    public Graphics2D graphics      = null;
    public JFrame window            = null;

    @Override
    public void run()
    {
        long start;
        long elapsed;
        long wait;

        this.running = true;

        while (this.running)
        {
            start = System.nanoTime();

            Keyboard.publish();
            // Mouse.feed(this);
            Mouse.publish();

            this.onPrepareStep();
            this.onPrepareDraw();
            this.repaint();

            elapsed = System.nanoTime() - start;
            wait =
                (1000 / this.target_framerate) -
                (elapsed / 1000000);

            try
            {
                Thread.sleep(wait);
            }
            catch (Exception x) {}
        }
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        this.addKeyListener(Keyboard.getInstance());
        this.addMouseListener(Mouse.getInstance());
        this.addMouseWheelListener(Mouse.getInstance());

        if (thread == null)
        {
            thread = new Thread(this, "Main Loop");
            thread.start();
        }
    }

    private void onPrepareStep()
    {
        this.onStep();
    }

    public void onStep()
    {
    }

    private void onPrepareDraw()
    {
        if (graphics == null)
            graphics = image.createGraphics();

        graphics.setColor(new Color(0, 0, 0, 255));
        graphics.fillRect(
            0,
            0,
            this.screen_width,
            this.screen_height);

        this.onDraw(graphics);
    }

    public void onDraw(Graphics2D g)
    {
    }

    @Override
    public void paintComponent(Graphics g)
    {
        if (image != null)
        {
            g.drawImage(
                image,
                0,
                0,
                this.getWidth(),
                this.getHeight(),
                Color.BLACK,
                null);
        }
    }

    public void resizeWindow(int width, int height)
    {
        this.setPreferredSize(new Dimension(width, height));
        this.window.pack();
    }

    @SuppressWarnings("unused")
    public void resizeViewport(int width, int height)
    {
        //this.setPreferredSize(new Dimension(width, height));
        //this.window.pack();

        this.running = false;

        int t = 0;
        while (this.thread.isAlive())
            t++;

        this.screen_width = width;
        this.screen_height = height;

        image = new BufferedImage(
            this.screen_width,
            this.screen_height,
            BufferedImage.TYPE_INT_ARGB);

        this.thread = new Thread(this);
        thread.start();
    }

    public LiteEngine(int width, int height)
    {
        this.screen_width   = width;
        this.screen_height  = height;

        Dimension resolution =
            new Dimension(
                this.screen_width,
                this.screen_height);

        this.setPreferredSize(resolution);
        this.setMinimumSize(resolution);
        this.setFocusable(true);
        this.requestFocus();
        this.setFocusTraversalKeysEnabled(false);

        image = new BufferedImage(
            this.screen_width,
            this.screen_height,
            BufferedImage.TYPE_INT_ARGB);

        //
        //
        //

        window = new JFrame();
        window.setBackground(Color.BLACK);
        window.setContentPane(this);
        //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusTraversalKeysEnabled(false);

        window.pack();
        window.setVisible(true);
        window.addWindowListener(new WindowAdapter()
        {
            @SuppressWarnings("unused")
            @Override
            public void windowClosing(WindowEvent e)
            {
                LiteEngine.this.running = false;

                int t = 0;
                while (LiteEngine.this.thread.isAlive())
                    t++;

                System.exit(0);
            }
        });
    }

    public void setTargetFramerate(int fr)
    {
        this.target_framerate = fr;
    }
}


