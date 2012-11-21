package kanga.kcae.view.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Point;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/** Helper class for loading images and cursors from the KCAE JAR.
 * 
 */
public class Resource {
    private static final String resourcePrefix = "kanga/kcae/res/";
    
    public static final Image closedGrabImage = getImage("ClosedGrab.png");
    public static final Image openGrabImage = getImage("OpenGrab.png");

    public static InputStream getResource(String name) {
        return getResource(name, ClassLoader.getSystemClassLoader());
    }

    public static InputStream getResource(
        String name,
        ClassLoader classLoader)
    {
        return classLoader.getResourceAsStream(resourcePrefix + name);
    }

    public static Image getImage(final String name) {
        return getImage(name, ClassLoader.getSystemClassLoader());
    }
    
    public static Image getImage(String name, ClassLoader classLoader) {
        try {
            URL url = classLoader.getResource(
                resourcePrefix + name);
            assert url != null : "Unknown resource " + resourcePrefix + name;
            Image result = ImageIO.read(url);
        
            assert result != null;
            return result;
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unknown image " + name, e);
        }
    }
    
    public static Cursor getCursor(
        final Image image,
        final int hotSpotX,
        final int hotSpotY,
        final String name)
    {
        return getCursor(
            Toolkit.getDefaultToolkit(), image, hotSpotX, hotSpotY, name);
    }
    
    public static Cursor getCursor(
        final Component component,
        final Image image,
        final int hotSpotX,
        final int hotSpotY,
        final String name)
    {
        return getCursor(
            component.getToolkit(), image, hotSpotX, hotSpotY, name);
    }
    
    public static Cursor getCursor(
        final Toolkit toolkit,
        final Image image,
        final int hotSpotX,
        final int hotSpotY,
        final String name)
    {
        return toolkit.createCustomCursor(
            image, new Point(hotSpotX, hotSpotY), name);
    }
}
