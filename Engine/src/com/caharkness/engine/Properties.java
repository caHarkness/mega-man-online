package com.caharkness.engine;

import java.util.HashMap;

public class Properties
{
    private static Properties self;
    public static Properties getInstance()
    {
        if (self == null)
            self = new Properties();

        return self;
    }

	private HashMap<String, String> map = new HashMap<>();

	public Properties()
	{

	}

	public String get(String key)
	{
		if (map.containsKey(key))
			return map.get(key);

		return "";
	}

	public String get(String key, String def)
	{
		if (map.containsKey(key))
			return map.get(key);

		map.put(key, def);
		return get(key, def);
	}

	public void set(String key, String value)
	{
		map.put(key, value);
	}

	public boolean getBoolean(String key)
	{
		if (map.containsKey(key))
		{
			String value =
				map
					.get(key)
					.toLowerCase();

			switch (value)
			{
				case "true":
					return true;

				case "false":
					return false;

				default:
					// Do nothing
			}
		}

		return false;
	}

	public boolean getBoolean(String key, boolean def)
	{
		if (map.containsKey(key))
		{
			String value =
				map
					.get(key)
					.toLowerCase();

			switch (value)
			{
				case "true":
					return true;

				case "false":
					return false;

				default:
					// Do nothing
			}
		}

		if (def == true)	set(key, "true");
		if (def == false)	set(key, "false");
		return getBoolean(key, def);
	}

	public void setBoolean(String key, boolean value)
	{
		if (value == true)	set(key, "true");
		if (value == false)	set(key, "false");
	}

	public int getInteger(String key, int def)
	{
		if (map.containsKey(key))
			return Integer.parseInt(map.get(key));

		map.put(key, def + "");
		return getInteger(key, def);
	}

	public double getDouble(String key, double def)
	{
		if (map.containsKey(key))
			return Double.parseDouble(map.get(key));

		map.put(key, def + "");
		return getDouble(key, def);
	}

	public float getFloat(String key, float def)
	{
		if (map.containsKey(key))
			return Float.parseFloat(map.get(key));

		map.put(key, def + "");
		return getFloat(key, def);
	}
}
