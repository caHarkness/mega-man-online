package com.caharkness.engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Image
{
	private static HashMap<String, BufferedImage> map = new HashMap<>();

	public static BufferedImage get(String name, int width, int height, int row, int column)
    {
        String key_name =
            String.format(
                "%s,%s,%s,%s,%s",
                name,
                width,
                height,
                row,
                column);

        // Load the already existing BufferedImage
        // From our HashMap instead of loading the
        // File.
        if (map.containsKey(key_name))
        {
            return map.get(key_name);
        }

        // Otherwise, continue loading the resource
        // And save it to our HashMap.
        else
        {
            try
            {
                URL url = Image
                    .class
                    .getResource(name);

                BufferedImage image = ImageIO.read(url);

                image = image.getSubimage(
                    column * width,
                    row * height,
                    width,
                    height);

                map.put(key_name, image);
                return image;
            }
            catch (Exception x) {}
        }

        return null;
    }

	public static BufferedImage resizeImage(BufferedImage image, int w, int h)
	{
        double oh = image.getHeight();
        double ow = image.getWidth();

        if (oh / h > ow /w)
            w = (int) (h * ow / oh);
        else
            h = (int) (w * oh / ow);

        BufferedImage out = new BufferedImage(w, h, image.getType());
        // scales the input image to the output image
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(image, 0, 0, w, h, null);
        g2d.dispose();
        return out;
    }
}
