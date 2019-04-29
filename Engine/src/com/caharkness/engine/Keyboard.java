package com.caharkness.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Keyboard implements KeyListener
{
	private static Keyboard self;
	public static Keyboard getInstance()
	{
		if (self == null)
			self = new Keyboard();

		return self;
	}

	public enum KeyState
	{
	    KEY_UP,
	    KEY_DOWN,
	    KEY_HELD
	}

	private static HashMap<Integer, KeyState> draft = new HashMap<>();
	private static HashMap<Integer, KeyState> map = new HashMap<>();
    private static ArrayList<Entity> focus = new ArrayList<>();

    public static void resetKeys()
    {
        for (Integer i : draft.keySet())
            if (draft.get(i) != KeyState.KEY_UP)
                draft.put(i, KeyState.KEY_UP);

        for (Integer i : map.keySet())
            if (map.get(i) != KeyState.KEY_UP)
                map.put(i, KeyState.KEY_UP);
    }

    public static void setFocus(Entity e)
    {
        resetKeys();
        focus.add(0, e);
    }

    public static void returnFocus()
    {
        resetKeys();
        if (focus.size() > 0)
            focus.remove(0);
    }

    public static void clearFocus()
    {
        resetKeys();
        focus.clear();
    }

    public static boolean check(Integer code)
    {
        if (!map.containsKey(code))
            return false;

        return map.get(code) == KeyState.KEY_DOWN || map.get(code) == KeyState.KEY_HELD;
    }

    public static boolean await(Integer code)
    {
        if (!map.containsKey(code))
            return false;

        boolean down = map.get(code) == KeyState.KEY_DOWN;

        if (down == true)
            draft.put(code, KeyState.KEY_HELD);

        return down;
    }

    @SuppressWarnings("rawtypes")
    public static void publish()
    {
        map.clear();
        for (Entry e : draft.entrySet())
            map.put(
                (Integer) e.getKey(),
                (KeyState) e.getValue());
    }

	@Override
	public void keyPressed(KeyEvent e)
	{
	    if (focus.size() > 0)
	    {
	        focus
	            .get(0)
	            .onKeyPressed(e);

	        return;
	    }

	    if (draft.containsKey(e.getKeyCode()))
	        if (draft.get(e.getKeyCode()) == KeyState.KEY_HELD)
	            return;

	    draft.put(e.getKeyCode(), KeyState.KEY_DOWN);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
        if (focus.size() > 0)
        {
            focus
                .get(0)
                .onKeyReleased(e);

            return;
        }

	    draft.put(e.getKeyCode(), KeyState.KEY_UP);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}
}
