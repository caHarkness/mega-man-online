package com.caharkness.engine;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Mouse implements MouseListener, MouseWheelListener
{
	private static Mouse self;
	public static Mouse getInstance()
	{
		if (self == null)
			self = new Mouse();

		return self;
	}

	public enum ButtonState
	{
	    BUTTON_UP,
	    BUTTON_DOWN,
	    BUTTON_HELD,
	}

	private static Point location = null;
	private static Point location_map = null;
	private static HashMap<Integer, ButtonState> draft = new HashMap<>();
    private static HashMap<Integer, ButtonState> map = new HashMap<>();
    private static ArrayList<Entity> focus = new ArrayList<>();

    public static void resetButtons()
    {
        for (Integer i : draft.keySet())
            if (draft.get(i) != ButtonState.BUTTON_UP)
                draft.put(i, ButtonState.BUTTON_UP);

        for (Integer i : map.keySet())
            if (map.get(i) != ButtonState.BUTTON_UP)
                map.put(i, ButtonState.BUTTON_UP);
    }

    public static void setFocus(Entity e)
    {
        resetButtons();
        focus.add(0, e);
    }

    public static void returnFocus()
    {
        resetButtons();
        if (focus.size() > 0)
            focus.remove(0);
    }

    public static void clearFocus()
    {
        resetButtons();
        focus.clear();
    }

    public static boolean check(Integer button)
    {
        if (!map.containsKey(button))
            return false;

        return map.get(button) == ButtonState.BUTTON_DOWN || map.get(button) == ButtonState.BUTTON_HELD;
    }

    public static boolean await(Integer button)
    {
        if (!map.containsKey(button))
            return false;

        boolean down = map.get(button) == ButtonState.BUTTON_DOWN;

        if (down == true)
            draft.put(button, ButtonState.BUTTON_HELD);

        return down;
    }

    @SuppressWarnings("rawtypes")
    public static void publish()
    {
        map.clear();
        for (Entry e : draft.entrySet())
            map.put(
                (Integer) e.getKey(),
                (ButtonState) e.getValue());
    }

    public static void feed(Engine e)
    {
        try
        {
            Mouse.location = new Point((int)e.mouse_sx, (int)e.mouse_sy);
            Mouse.location_map =
                new Point(
                    (int) -(((Mouse.location.x - (e.scale_x) / e.screen_width) * -1) - e.camera_x),
                    (int) -(((Mouse.location.y - (e.scale_y) / e.screen_height) * -1) - e.camera_y));

        }
        catch (Exception x) {}
    }

    public static int getX()
    {
        if (location != null)
                return location.x;
        else    return 0;
    }

    public static int getY()
    {
        if (location != null)
                return location.y;
        else    return 0;
    }

    public static int getTranslatedX()
    {
        if (location_map != null)
                return location_map.x;
        else    return 0;
    }

    public static int getTranslatedY()
    {
        if (location_map != null)
                return location_map.y;
        else    return 0;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (focus.size() > 0)
        {
            focus
                .get(0)
                .onMousePressed(e);
        }

        if (draft.containsKey(e.getButton()))
            if (draft.get(e.getButton()) == ButtonState.BUTTON_HELD)
                return;

        draft.put(e.getButton(), ButtonState.BUTTON_DOWN);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (focus.size() > 0)
        {
            focus
                .get(0)
                .onMouseReleased(e);
        }

        draft.put(e.getButton(), ButtonState.BUTTON_UP);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (focus.size() > 0)
        {
            focus
                .get(0)
                .onMouseWheelMoved(e);

            return;
        }
    }
}
