package kanga.kcae.view.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Set;

import javax.annotation.Nonnull;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

import kanga.kcae.object.Font;
import kanga.kcae.object.Pin;
import kanga.kcae.object.PinStyle;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Shape;
import kanga.kcae.object.Symbol;

import static org.apache.commons.lang3.StringUtils.defaultString;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PinPainter {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(PinPainter.class);
        
    public static final int NEGATED_SIZE =  5000000; // 5.0 mm
    public static final int NET_WIDTH =      500000; // 0.5 mm
    public static final int BUS_WIDTH =     2000000; // 2.0 mm
    public static final int MITER_LIMIT =   1000000; // 1.0 mm
    public static final int LABEL_OFFSET =  5000000; // 5.0 mm
    
    /** Paint a symbol's pin on a canvas.
     * 
     *  @param  symbol  The symbol which owns the pin.
     *  @param  pin     The pin to paint.
     *  @param  g       The graphics context on the canvas to paint with.
     */
    public static void paint(
        @Nonnull final Symbol symbol,
        @Nonnull final Pin pin,
        @Nonnull final Graphics2D g)
    {
        // Restore the graphics transformation to its original state after
        // we're done.
        final AffineTransform originalTransform = g.getTransform();
        try {
            final Font nameFont = symbol.getPinNameFont(pin);
            final Font numberFont = symbol.getPinNumberFont(pin);
            final Point conPoint = pin.getConnectionPoint();
            Point endPoint = pin.getEndPoint();
            final Set<PinStyle> pinStyles = pin.getPinStyles();
            final boolean bus = pinStyles.contains(PinStyle.BUS);
            final boolean negated = pinStyles.contains(PinStyle.NEGATED);
            final long dX = endPoint.getX() - conPoint.getX();
            final long dY = endPoint.getY() - conPoint.getY();
            final double angle = atan2(dY, dX);
            final double pinLength = sqrt(dX * dX + dY * dY);
            
            // Draw everything relative to the end point.
            g.translate(endPoint.getX(), endPoint.getY());
            g.rotate(angle);
        
            // For drawing the pin itself.
            final int lineWidth = (bus ? BUS_WIDTH : NET_WIDTH);
            final Stroke stroke = new BasicStroke(
                lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                MITER_LIMIT);
            final Path2D.Double awtPinPath = new Path2D.Double();

            // If we need to draw the negated bubble at the endpoint.
            Ellipse2D negatedCircle = null;
        
            // Shapes which make up the labels.
            final Shape nameLabel = nameFont.render(
                defaultString(pin.getName()));
            final Shape numberLabel = numberFont.render(
                defaultString(pin.getPinNumber()));

            Rectangle nameLabelBBox = nameLabel.getBoundingBox();
            Rectangle numberLabelBBox = numberLabel.getBoundingBox();
            
            final long nameLabelHeight = (nameLabelBBox != null ?
                nameLabelBBox.getHeight() : 0);
            final long numberLabelWidth = (numberLabelBBox != null ?
                numberLabelBBox.getWidth() : 0);
            
            // Draw the pin itself.
            if (negated) {
                awtPinPath.moveTo(-NEGATED_SIZE, 0);
                awtPinPath.lineTo(-NEGATED_SIZE - pinLength, 0);
                negatedCircle = new Ellipse2D.Double(
                    -NEGATED_SIZE * 0.5, 0, NEGATED_SIZE, NEGATED_SIZE);
            } else {
                awtPinPath.moveTo(0, 0);
                awtPinPath.lineTo(-pinLength, 0);
            }
            if (pinStyles.contains(PinStyle.BUS)) {
                g.setPaint(java.awt.Color.BLUE);
            } else {
                g.setPaint(java.awt.Color.BLACK);
            }
            g.setStroke(stroke);
            g.draw(awtPinPath);
            
            // Draw the negated circle.
            if (negatedCircle != null) {
                g.draw(negatedCircle);
            }
            
            // Draw the pin name.
            g.setPaint(java.awt.Color.BLACK);
            g.translate(LABEL_OFFSET, - nameLabelHeight * 0.5);
            ShapePainter.paint(g, nameLabel);
            g.setPaint(java.awt.Color.RED);
            
            if (nameLabelBBox != null) {
                ShapePainter.paint(g, nameLabelBBox);
            }
                        
            g.translate(-LABEL_OFFSET * 2 - numberLabelWidth,
                        (nameLabelHeight - LABEL_OFFSET) * 0.5);
            ShapePainter.paint(g, numberLabel);
        }
        finally {
            g.setTransform(originalTransform);
        }
    }
}
