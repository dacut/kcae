package kanga.kcae.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class GenerateIcons {
    static final int SIZE_I = 256;
    static final double SIZE = (double) SIZE_I;
    static final double MARGIN = SIZE * 0.125;
    static final double LINE_WIDTH = MARGIN * 0.75;
    static final double OUTLINE_WIDTH = LINE_WIDTH * 0.5;
    static final double THIN_OUTLINE_WIDTH = LINE_WIDTH * 0.15;
    static final int[] IMAGE_SIZES = { 256, 64, 32, 16 };
    
    public static final Stroke lineStroke = new BasicStroke(
        (float) LINE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    public static final Stroke outlineStroke = new BasicStroke(
        (float) OUTLINE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    public static final Stroke thinOutlineStroke = new BasicStroke(
        (float) THIN_OUTLINE_WIDTH, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER);

    public static void drawTarget(final Graphics2D g, double xc, double yc) {
        Rectangle2D.Double rect = new Rectangle2D.Double(
            xc - LINE_WIDTH,
            yc - LINE_WIDTH,
            2 * LINE_WIDTH,
            2 * LINE_WIDTH);
        g.setPaint(Color.BLACK);
        g.fill(rect);

        g.setPaint(Color.WHITE);
        g.setStroke(outlineStroke);
        g.draw(rect);
    }
    
    public static interface ToolRenderer {
        public void render(final Graphics2D g);
    }
    
    public static class LineToolRenderer implements ToolRenderer {
        @Override
        public void render(final Graphics2D g) {
            Line2D.Double line = new Line2D.Double(
                MARGIN, SIZE - MARGIN, SIZE - MARGIN, MARGIN);
            g.setPaint(Color.BLACK);
            g.setStroke(lineStroke);
            g.draw(line);
            drawTarget(g, SIZE - MARGIN, MARGIN);
        }
    }
    
    public static class BezierToolRenderer implements ToolRenderer {
        @Override
        public void render(final Graphics2D g) {
            CubicCurve2D.Double curve = new CubicCurve2D.Double(
                MARGIN, 0.4 * SIZE,
                1.5 * SIZE, -0.6 * SIZE,
                -0.5 * SIZE, 1.4 * SIZE,
                SIZE - MARGIN, 0.7 * SIZE);
            g.setPaint(Color.BLACK);
            g.setStroke(lineStroke);
            g.draw(curve);
            drawTarget(g, SIZE - MARGIN, 0.7 * SIZE);
        }
    }
    
    public static class NavigateToolRenderer implements ToolRenderer {
        @Override
        public void render(final Graphics2D g) {
            Path2D.Double arrowHead = new Path2D.Double();
            arrowHead.moveTo(64.0, 64.0);
            arrowHead.lineTo(128.0, 4.0);
            arrowHead.lineTo(192.0, 64.0);
            arrowHead.closePath();
            
            g.setPaint(Color.BLACK);
            
            for (int i = 0; i < 4; ++i) {
                g.fill(arrowHead);
                g.rotate(Math.PI * 0.5);
                g.translate(0.0, -256.0);
            }
            
            g.setStroke(lineStroke);
            g.drawLine(128, 32, 128, 256 - 32);
            g.drawLine(32, 128, 256 - 32, 128);
        }
    }
    
    public static class HandToolRenderer implements ToolRenderer {
        @Override
        public void render(final Graphics2D g) {
            Path2D.Double path = new Path2D.Double();
            path.moveTo(88.0, 256.0);
            path.lineTo(88.0, 232.0);
            
            // Thumb
            path.lineTo( 8.0, 120.0);
            // curveTo(...)
            path.lineTo(40.0, 120.0);
            path.lineTo(72.0, 136.0);
            
            // Index
            path.lineTo(40.0, 40.0);
            // curveTo(...)
            path.lineTo(88.0, 40.0);
            path.lineTo(104.0, 88.0);
            
            // Middle
            path.lineTo(104.0, 24.0);
            // curveTo(...)
            path.lineTo(152.0, 24.0);
            path.lineTo(152.0, 88.0);

            // Ring
            path.lineTo(152.0, 40.0);
            // curveTo(...)
            path.lineTo(200.0, 40.0);
            path.lineTo(200.0, 104.0);

            // Pinky
            path.lineTo(216.0, 72.0);
            // curveTo(...)
            path.lineTo(248.0, 72.0);
            
            path.lineTo(248.0, 120.0);
            path.lineTo(200.0, 216.0);
            path.lineTo(200.0, 256.0);
            g.setPaint(Color.WHITE);
            g.fill(path);
            
            g.setPaint(Color.BLACK);
            g.setStroke(outlineStroke);
            g.draw(path);

        }
    }
    
    @SuppressWarnings("rawtypes")
    static final Class[] renderClasses = {
        LineToolRenderer.class,
        BezierToolRenderer.class,
        NavigateToolRenderer.class,
        HandToolRenderer.class,
    };
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        for (Class<? extends ToolRenderer> cls : renderClasses) {
            ToolRenderer tr = cls.newInstance();
            String fileNameBase = "res/" + cls.getSimpleName();
            if (fileNameBase.endsWith("Renderer")) {
                fileNameBase = fileNameBase.substring(
                    0, fileNameBase.length() - "Renderer".length());
            }
            BufferedImage img = new BufferedImage(
                SIZE_I, SIZE_I, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g = img.createGraphics();
            tr.render(g);

            for (int size : IMAGE_SIZES) {
            
                String fileName = fileNameBase + "-" + size + "x" + size +
                    ".png";
                File file = new File(fileName);

                if (size == SIZE_I) {
                    ImageIO.write(img, "png", file);
                } else {
                    AffineTransform xform = AffineTransform.getScaleInstance(
                        ((double) size) / SIZE,
                        ((double) size) / SIZE);
                    AffineTransformOp scaler = new AffineTransformOp(
                        xform, AffineTransformOp.TYPE_BICUBIC);
                    BufferedImage scaledImg = new BufferedImage(
                        size, size, BufferedImage.TYPE_INT_ARGB_PRE);

                    scaler.filter(img, scaledImg);
                    ImageIO.write(scaledImg, "png", file);
                }
            }
        }
    }

}
