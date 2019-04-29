package com.caharkness.megaman.controllers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.caharkness.engine.Entity;
import com.caharkness.engine.Image;
import com.caharkness.engine.Keyboard;
import com.caharkness.engine.Mouse;
import com.caharkness.megaman.models.TileBehavior;

public class MapEditor extends Entity
{
    int offset_x = 0;
    int offset_y = -24;

    String tileset = "";
    int tileset_index = 0;
    int tile_index = 0;
    int behavior_index = 0;
    int row = 0;
    int col = 0;
    int layer = 0;
    String desc = "";

    float last_x;
    float last_y;
    int area = 0;

    MapEditorOverlay overlay;

    String[] tilesets =
    {
        "/sprites/spr_tileset_elec_man.png",
        "/sprites/spr_tileset_fire_man.png",
        "/sprites/spr_tileset_gravity_man.png",
        //"/sprites/hardman.png",
        "/sprites/spr_tileset_green.png",
        "/sprites/spr_tileset_magnet_man.png",
        "/sprites/spr_tileset_shiny.png",
        "/sprites/spr_tileset_stone_man.png"
    };

    int behavior_selection = 0;

    public void previousBehavior()
    {
        this.behavior_selection--;

        if (this.behavior_selection < 0)
            this.behavior_selection = TileBehavior.getPlaceableTiles().size() - 1;

        this.timers.put("preview", 120);
    }

    public void nextBehavior()
    {
        this.behavior_selection++;

        if (this.behavior_selection >= TileBehavior.getPlaceableTiles().size())
            this.behavior_selection = 0;

        this.timers.put("preview", 120);
    }

    public void previousTileset()
    {
        tileset_index--;
        if (tileset_index < 0)
            tileset_index = tilesets.length - 1;

        this.timers.put("preview", 120);
    }

    public void nextTileset()
    {
        tileset_index++;
        if (tileset_index > tilesets.length - 1)
            tileset_index = 0;

        this.timers.put("preview", 120);
    }

    public void previousTile()
    {
        tile_index--;
        if (tile_index < 0)
            tile_index = 35;

        col--;
        if (col < 0)
        {
            col = 5;
            row--;

            if (row < 0)
            {
                row = 5;
                col = 5;
            }
        }

        this.timers.put("preview", 120);
    }

    public void nextTile()
    {
        tile_index++;
        if (tile_index > 35)
            tile_index = 0;

        col++;
        if (col > 5)
        {
            col = 0;
            row++;

            if (row > 5)
            {
                row = 0;
                col = 0;
            }
        }

        this.timers.put("preview", 120);
    }

    public void place()
    {
        if (NetworkClient.getInstance() != null)
        {
            NetworkClient
                .getInstance()
                .map
                .setTileLocal(
                    (int) this.x / 16,
                    (int) this.y / 16,
                    this.tilesets[tileset_index],
                    this.row,
                    this.col,
                    TileBehavior.getPlaceableTiles().get(behavior_selection));
        }
    }

    public void erase()
    {
        if (NetworkClient.getInstance() != null)
        {
            NetworkClient
                .getInstance()
                .map
                .setTileLocal(
                    (int) this.x / 16,
                    (int) this.y / 16,
                    this.tilesets[tileset_index],
                    this.row,
                    this.col,
                    TileBehavior.VOID);
        }
    }

	@Override
	public void onCreate()
	{
	    this.z = 1000;
		this.width = 16;
		this.height = 16;

		Mouse.setFocus(this);

		this.max_frame = 1;
		this.frame_max_age = 5;
		this.overlay = (MapEditorOverlay) this.spawn(new MapEditorOverlay());

		/*
		// Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the JFrame.
        this.engine.setCursor(blankCursor);
        */
	}

	@Override
	public void onMousePressed(MouseEvent e)
	{
	    if (e.getButton() == MouseEvent.BUTTON1)
	        place();

	    else if (e.getButton() == MouseEvent.BUTTON3)
	        erase();
	}

	@Override
	public void onMouseWheelMoved(MouseWheelEvent e)
	{
	    if (e.getWheelRotation() < 0)
        {
	        if (Keyboard.check(KeyEvent.VK_SHIFT))
	               previousTileset();
	        else   previousTile();
        }

	    if (e.getWheelRotation() > 0)
        {
            if (Keyboard.check(KeyEvent.VK_SHIFT))
                   nextTileset();
            else   nextTile();
        }
	}

	@Override
	public void onStep()
	{
		this.x = Mouse.getTranslatedX() - 8;
		this.y = Mouse.getTranslatedY() - 8;
		this.x = Math.round(this.x / 16) * 16;
		this.y = Math.round(this.y / 16) * 16;

		if (this.x != this.last_x || this.y != this.last_y)
		{
		    if (Mouse.check(MouseEvent.BUTTON1))
		        place();

		    if (Mouse.check(MouseEvent.BUTTON3))
		        erase();
		}

		this.last_x = this.x;
		this.last_y = this.y;

	    if (Keyboard.await(KeyEvent.VK_TAB))
	    {
	        if (Keyboard.check(KeyEvent.VK_SHIFT))
	                previousBehavior();
	        else    nextBehavior();
	    }

		if (this.timers.containsKey("preview"))
		    desc = String.format("%s - %s",
		        TileBehavior
        		    .getPlaceableTiles()
        		    .get(behavior_selection)
        		    .getTitle(),
        		TileBehavior
                    .getPlaceableTiles()
                    .get(behavior_selection)
                    .getDescription());
		else desc = "";
	}

	@Override
	public void onDraw(Graphics2D g)
	{
	    /*
	    if (Mouse.getTranslatedX() < player.x)
    	    g.drawImage(
                Image.get("/sprites/rush.png", 32, 32, 1, this.frame),
                (int) Mouse.getTranslatedX() - 48,
                (int) Mouse.getTranslatedY() - 24,
                null,
                null
            );
	    else
	        g.drawImage(
                Image.get("/sprites/rush.png", 32, 32, 0, this.frame),
                (int) Mouse.getTranslatedX() + 16,
                (int) Mouse.getTranslatedY() - 24,
                null,
                null
            );
        */

	    Composite original = g.getComposite();
	    g.setComposite(
	        AlphaComposite.getInstance(
	            AlphaComposite.SRC_OVER, 0.5f));

	    g.setColor(Color.RED);
        g.drawRect(
            (int) this.x,
            (int) this.y,
            (int) this.width - 1,
            (int) this.height - 1);

        if (area > 0)
        {
            g.drawRect(
                (int) this.x - 16 * area,
                (int) this.y - 16 * area,
                (int) this.width + 16 * area + 16,
                (int) this.height + 16 * area + 16);
        }

        g.drawImage(
            Image.get(tilesets[tileset_index], 16, 16, row, col),
            (int) this.x,
            (int) this.y,
            null,
            null);

	    g.setComposite(original);
	}
}
