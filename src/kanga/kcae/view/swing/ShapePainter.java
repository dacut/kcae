package kanga.kcae.view.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import kanga.kcae.object.Color;
import kanga.kcae.object.LineStyle;
import kanga.kcae.object.Path;
import kanga.kcae.object.PathInstruction;
import kanga.kcae.object.PathPainter;
import kanga.kcae.object.Point;
import kanga.kcae.object.Shape;
import kanga.kcae.object.ShapeGroup;

public abstract class ShapePainter {
    public static void paint(final Graphics2D g, final ShapeGroup sg) {
        for (final Shape s : sg.getShapes()) {
            paint(g, s);
        }
    }

    public static void paint(final Graphics2D g, final Shape s) {
        if (s instanceof Path) {
            paint(g, (Path) s);
        }
        
        return;
    }

    public static class AWTPathPainter implements PathPainter {
        public AWTPathPainter(final Path2D.Double awtPath) {
            this.awtPath = awtPath;
        }

        @Override
        public void moveTo(Point p) {
            this.awtPath.moveTo((double) p.getX(), (double) p.getY());
        }

        @Override
        public void lineTo(Point p) {
            this.awtPath.lineTo((double) p.getX(), (double) p.getY());
        }

        @Override
        public void quadraticCurveTo(Point control, Point target) {
            this.awtPath.quadTo(
                (double) control.getX(), (double) control.getY(),
                (double) target.getX(), (double) target.getY());
        }

        @Override
        public void bezierCurveTo(
            Point control1,
            Point control2,
            Point target)
        {
            this.awtPath.curveTo(
                (double) control1.getX(), (double) control1.getY(),
                (double) control2.getX(), (double) control2.getY(),
                (double) target.getX(), (double) target.getY());
        }

        @Override
        public void closePath() {
            this.awtPath.closePath();
        }

        private final Path2D.Double awtPath;
    }

    public static void paint(final Graphics2D g, final Path path) {
        List<PathInstruction> instructions = path.getInstructions();
        Path2D.Double awtPath = new Path2D.Double(
            Path2D.WIND_NON_ZERO, path.size());
        AWTPathPainter pp = new AWTPathPainter(awtPath);

        setLineStyle(g, path.getLineStyle());
        
        for (final PathInstruction pi : instructions) {
            pi.paint(pp);
        }

        return;
    }

    public static void setLineStyle(final Graphics2D g, final LineStyle ls) {
        if (ls != null) {
            final Color c = ls.getColor();
            if (c != null) {
                g.setPaint(new java.awt.Color(c.getRGBA()));
            }

            float awtWidth = ls.getWidth() * 1e-3f;
            int awtCap, awtJoin;
            float miterLimit = ls.getMiterLimit() * 1e-3f;
            int[] stipple = ls.getStipple();

            switch (ls.getCapStyle()) {
                case butt: awtCap = BasicStroke.CAP_BUTT; break;
                case round: awtCap = BasicStroke.CAP_ROUND; break;
                case square: awtCap = BasicStroke.CAP_SQUARE; break;
                default: awtCap = BasicStroke.CAP_BUTT; break;
            }

            switch (ls.getJoinStyle()) {
                case bevel: awtJoin = BasicStroke.JOIN_BEVEL; break;
                case miter: awtJoin = BasicStroke.JOIN_MITER; break;
                case round: awtJoin = BasicStroke.JOIN_ROUND; break;
                default: awtJoin = BasicStroke.JOIN_BEVEL; break;
            }

            if (stipple != null) {
                float[] awtDash = new float[stipple.length];
                for (int i = 0; i < stipple.length; ++i) {
                    awtDash[i] = stipple[i] * 1e-3f;
                }

                g.setStroke(new BasicStroke(awtWidth, awtCap, awtJoin,
                                            miterLimit, awtDash, 0.0f));
            } else {
                g.setStroke(new BasicStroke(awtWidth, awtCap, awtJoin,
                                            miterLimit));
            }
        }

        return;
    }
}
