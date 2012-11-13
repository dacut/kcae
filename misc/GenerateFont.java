
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import kanga.kcae.object.BezierCurveTo;
import kanga.kcae.object.ClosePath;
import kanga.kcae.object.Color;
import kanga.kcae.object.Glyph;
import kanga.kcae.object.LineTo;
import kanga.kcae.object.LineStyle;
import kanga.kcae.object.MoveTo;
import kanga.kcae.object.Path;
import kanga.kcae.object.PathInstruction;
import kanga.kcae.object.Point;
import kanga.kcae.object.QuadraticCurveTo;
import kanga.kcae.object.Typeface;

public class GenerateFont {
    public static void main(String[] args) throws Exception {
        Map<Character, Glyph> glyphs =
            new HashMap<Character, Glyph>(3);
        Glyph replacementGlyph = null;
        Typeface tf;
        Color black = new Color(0, 0, 0);
        LineStyle defaultLineStyle = new LineStyle(500000, black, null, LineStyle.CapStyle.butt, LineStyle.JoinStyle.bevel, 1000000);
        Path path;
        PathInstruction[] pathInstructions;
        LineStyle lineStyle;
        Glyph glyph;

        pathInstructions = new PathInstruction[] {
            new MoveTo(new Point(0, 0)),
            new LineTo(new Point(0, 10000000)),
            new LineTo(new Point(5000000, 10000000)),
            new LineTo(new Point(5000000, 0)),
            new ClosePath()
        };
        lineStyle = defaultLineStyle;
        path = new Path(pathInstructions, lineStyle, null);
        glyph = new Glyph(path, 6000000, null);
        replacementGlyph = glyph;

        pathInstructions = new PathInstruction[] {
            new MoveTo(new Point(0, 0)),
            new LineTo(new Point(2500000, 10000000)),
            new LineTo(new Point(5000000, 0)),
            new MoveTo(new Point(1250000, 5000000)),
            new LineTo(new Point(3750000, 5000000))
        };
        lineStyle = defaultLineStyle;
        path = new Path(pathInstructions, lineStyle, null);
        glyph = new Glyph(path, 6000000, null);
        glyphs.put('\u0041', glyph);

        pathInstructions = new PathInstruction[] {
            new MoveTo(new Point(0, 0)),
            new LineTo(new Point(0, 10000000)),
            new BezierCurveTo(new Point(5000000, 10000000), new Point(5000000, 5000000), new Point(0, 5000000)),
            new BezierCurveTo(new Point(5000000, 5000000), new Point(5000000, 0), new Point(0, 0))
        };
        lineStyle = defaultLineStyle;
        path = new Path(pathInstructions, lineStyle, null);
        glyph = new Glyph(path, 6000000, null);
        glyphs.put('\u0042', glyph);

        tf = new Typeface("Sans Serif", 0, 5000000, 10000000, 10000000, 5000000, glyphs, replacementGlyph);
        System.out.println(tf);

        FileOutputStream fos = new FileOutputStream("res/sans.fnt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tf);
        oos.close();
    }
}
