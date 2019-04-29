package com.caharkness.engine;

import java.net.URL;
import java.util.HashMap;

import javafx.scene.media.AudioClip;


public class Sound
{
    public static boolean silence = false;

	public static HashMap<String, AudioClip> map = new HashMap<>();

	public static void play(String name)
	{
	    if (silence)
	        return;

		// Play the already existing AudioStream
		// From our HashMap instead of loading the
		// File.
		if (map.containsKey(name))
		{
			map
				.get(name)
				.play();
		}

		// Otherwise, continue loading the resource
		// And save it to our HashMap.
		else
		{
			try
			{
				URL url = Sound.class.getResource(name);

				AudioClip clip =
					new AudioClip(
						url
							.toURI()
							.toString());

				clip.setVolume(0.25);

				map.put(name, clip);
				play(name);
			}
			catch (Exception x) {}
		}
	}
}
