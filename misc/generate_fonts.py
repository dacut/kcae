#! /usr/bin/env python2.7
from __future__ import print_function
import subprocess
from sys import exit, stderr

name = "Sans Serif"
descender =  0.0
median =     5.0
capital =   10.0
ascender =  10.0
xheight =    5.0

default_advance = 6.0
glyphs = {}

class advance(object):
    def __init__(self, value):
        object.__init__(self)
        self.value = value
        return

def mm(value):
    return int(round(value * 1e6))

class line_style(object):
    def __init__(self, width=1.0, cap="butt", join="bevel", miterLimit=1.0):
        object.__init__(self)
        self.width = width
        self.cap = cap
        self.join = join
        self.miterLimit = miterLimit
        return
        
    def __str__(self):
        return ("new LineStyle(%d, black, null, LineStyle.CapStyle.%s, "
                "LineStyle.JoinStyle.%s, %d)" %
                (mm(self.width), self.cap, self.join,
                 mm(self.miterLimit)))
default_line_style = line_style()

class lt(object):
    def __init__(self, x, y):
        object.__init__(self)
        self.x = x
        self.y = y
        return

    def __str__(self):
        return "new LineTo(new Point(%d, %d))" % (mm(self.x), mm(self.y))

class mt(object):
    def __init__(self, x, y):
        object.__init__(self)
        self.x = x
        self.y = y
        return

    def __str__(self):
        return "new MoveTo(new Point(%d, %d))" % (mm(self.x), mm(self.y))

class bz(object):
    def __init__(self, cp1x, cp1y, cp2x, cp2y, tx, ty):
        object.__init__(self)
        self.cp1x = cp1x
        self.cp1y = cp1y
        self.cp2x = cp2x
        self.cp2y = cp2y
        self.tx = tx
        self.ty = ty
        return

    def __str__(self):
        return ("new BezierCurveTo(new Point(%d, %d), new Point(%d, %d), "
                "new Point(%d, %d))" %
                (mm(self.cp1x), mm(self.cp1y),
                 mm(self.cp2x), mm(self.cp2y),
                 mm(self.tx), mm(self.ty)))

class qd(object):
    def __init__(self, cpx, cpy, tx, ty):
        object.__init__(self)
        self.cpx = cpx
        self.cpy = cpy
        self.tx = tx
        self.ty = ty
        return

    def __str__(self):
        return ("new QuadraticCurveTo(new Point(%d, %d), new Point(%d, %d), "
                "new Point(%d, %d))" %
                (mm(self.cpx), mm(self.cpy),
                 mm(self.tx), mm(self.ty)))

class cp(object):
    def __init__(self):
        object.__init__(self)
        return

    def __str__(self):
        return "new ClosePath()"

def glyph(c, *args):
    global default_advance, default_line_style, glyphs
    if isinstance(c, basestring):
        c = ord(c)

    adv = default_advance
    ls = "defaultLineStyle"
    path = [mt(0,0)]
    
    for arg in args:
        if isinstance(arg, advance):
            adv = arg.value
        elif isinstance(arg, line_style):
            ls = str(arg)
        else:
            path.append(arg)
    
    data = """
        pathInstructions = new PathInstruction[] {
""" + ",\n".join(["            " + str(p) for p in path]) + """
        };
        lineStyle = """ + ls + """;
        path = new Path(pathInstructions, lineStyle, null);
        glyph = new Glyph(path, """ + str(mm(adv)) + """, null);
"""
        
    glyphs[c] = data
    return

def generate(fontname):
    global default_advance, default_line_style, glyphs
    default_advance = 6.0
    glyphs = {}
    default_line_style = line_style()

    execfile("misc/" + fontname + ".py", globals(), locals())

    print("dls: %s" % default_line_style)

    with open("misc/GenerateFont.java", "w") as fd:
        fd.write("""
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
            new HashMap<Character, Glyph>(%(nglyphs)d);
        Glyph replacementGlyph = null;
        Typeface tf;
        Color black = new Color(0, 0, 0);
        LineStyle defaultLineStyle = %(default_line_style)s;
        Path path;
        PathInstruction[] pathInstructions;
        LineStyle lineStyle;
        Glyph glyph;
""" % { 'nglyphs': len(glyphs),
        'default_line_style': default_line_style })
                 
        for c in sorted(glyphs.keys()):
            g = glyphs[c]
            fd.write(g)
            if c == 0:
                fd.write("        replacementGlyph = glyph;\n")
            else:
                fd.write("        glyphs.put('\\u%04x', glyph);\n" % (c,))

        fd.write("""
        tf = new Typeface("%(name)s", %(descender)d, %(median)d, %(capital)d, %(ascender)d, %(xheight)d, glyphs, replacementGlyph);
        System.out.println(tf);
""" % { 'name': name,
        'descender': mm(descender),
        'median': mm(median),
        'capital': mm(capital),
        'ascender': mm(ascender),
        'xheight': mm(xheight) })

        fd.write("""
        FileOutputStream fos = new FileOutputStream("res/%(fontname)s.fnt\");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tf);
        oos.close();
    }
}
""" % {'fontname': fontname})
    
    result = subprocess.call(
        ["./run-java", "-compile", "misc/GenerateFont.java"])
    if result != 0:
        print("Failed to compile GenerateFonts.py", file=stderr)
        return result

    result = subprocess.call(
        ["./run-java", "-nw", "-classpath", "misc", "GenerateFont"])
    if result != 0:
        print("Failed to execute GenerateFonts", file=stderr)
        return result

    print("Created %s.fnt" % fontname)

    return 0

if __name__ == "__main__":
    for font in ["sans"]:
        result = generate(font)
        if result != 0:
            exit(result)
    exit(0)
