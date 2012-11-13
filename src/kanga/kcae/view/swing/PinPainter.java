package kanga.kcae.view.swing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Set;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.sin;

import kanga.kcae.object.Font;
import kanga.kcae.object.Pin;
import kanga.kcae.object.PinStyle;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Shape;
import kanga.kcae.object.Typeface;
import kanga.kcae.xchg.autocad.AutoCADShapeFile;

import org.apache.commons.lang3.ObjectUtils;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.join;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PinPainter {
    private static final Log log = LogFactory.getLog(PinPainter.class);
    public static final Typeface labelTypeface = 
        AutoCADShapeFile.find("iso3098b").toTypeface();
    public static final Font labelFont = new Font(labelTypeface, 8000000);
    public static final int NEGATED_SIZE =  5000000; // 5.0 mm
    public static final int NET_WIDTH =      500000; // 0.5 mm
    public static final int BUS_WIDTH =     2000000; // 2.0 mm
    public static final int MITER_LIMIT =   1000000; // 1.0 mm
    public static final int LABEL_OFFSET =  5000000; // 5.0 mm
    
    public static void paint(final Graphics2D g, final Pin pin) {
        Point conPoint = pin.getConnectionPoint();
        Point endPoint = pin.getEndPoint();
        final Set<PinStyle> pinStyles = pin.getPinStyles();
        boolean bus = pinStyles.contains(PinStyle.BUS);
        boolean negated = pinStyles.contains(PinStyle.NEGATED);
        
        // For drawing the pin itself.
        int lineWidth = (bus ? BUS_WIDTH : NET_WIDTH);
        Stroke stroke = new BasicStroke(
                lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                MITER_LIMIT);
        Path2D.Double awtPath = new Path2D.Double();

        // If we need to draw the negated bubble at the endpoint.
        Ellipse2D negatedCircle = null;
        
        // For drawing the labels.
        Shape nameLabel = labelFont.render(defaultString(pin.getName()));
        Shape numberLabel = labelFont.render(join(pin.getPinNumbers(), ";"));
        assert nameLabel != null;
        assert numberLabel != null;
        
        Rectangle nameLabelBBox = nameLabel.getBoundingBox();
        Rectangle numberLabelBBox = numberLabel.getBoundingBox();

        long nameLabelWidth = (nameLabelBBox != null ?
                               nameLabelBBox.getWidth() : 0);
        long numberLabelWidth = (numberLabelBBox != null ?
                                 numberLabelBBox.getWidth() : 0);

        AffineTransform gTransform = g.getTransform();
        AffineTransform nameLabelTransform = ObjectUtils.clone(gTransform);
        AffineTransform numberLabelTransform = ObjectUtils.clone(gTransform);
        
        // Compute positions of the pin name and number labels, as well as the
        // negated bubble.
        if (conPoint.getX() == endPoint.getX()) {
            // Vertical line (would result in infinite slope); compute the
            // offsets directly.
            long endPointDY, nameLabelDY, numberLabelDY;
            
            if (conPoint.getY() < endPoint.getY()) {
                endPointDY = -NEGATED_SIZE;
                nameLabelDY = -LABEL_OFFSET - nameLabelWidth;
                numberLabelDY = +LABEL_OFFSET;
            } else {
                endPointDY = +NEGATED_SIZE;
                nameLabelDY = +LABEL_OFFSET;
                numberLabelDY = -LABEL_OFFSET - numberLabelWidth;
            }

            nameLabelTransform.translate(
                endPoint.getX(), endPoint.getY() + nameLabelDY);
            nameLabelTransform.quadrantRotate(1);
            numberLabelTransform.translate(
                endPoint.getX(), endPoint.getY() + numberLabelDY);
            numberLabelTransform.quadrantRotate(1);

            if (negated) {
                endPoint = new Point(
                    endPoint.getX(), endPoint.getY() + endPointDY);
                negatedCircle = new Ellipse2D.Double(
                    endPoint.getX() - NEGATED_SIZE / 2,
                    endPoint.getY() + endPointDY,
                    NEGATED_SIZE, NEGATED_SIZE);
            }
        }
        else if (conPoint.getY() == endPoint.getY()) {
            // Horizontal line; compute the offset directly for accuracy,
            // avoiding the trig functions.
            long endPointDX, nameLabelDX, numberLabelDX;
            
            if (conPoint.getX() < endPoint.getX()) {
                endPointDX = -NEGATED_SIZE;
                nameLabelDX = +LABEL_OFFSET;
                numberLabelDX = -LABEL_OFFSET - numberLabelWidth;
            } else {
                endPointDX = +NEGATED_SIZE;
                nameLabelDX = -LABEL_OFFSET - nameLabelWidth;
                numberLabelDX = +LABEL_OFFSET;
            }
            
            nameLabelTransform.translate(
                endPoint.getX() + nameLabelDX, endPoint.getY());
            numberLabelTransform.translate(
                endPoint.getX() + numberLabelDX, endPoint.getY());
            
            if (negated) {
                endPoint = new Point(
                    endPoint.getX() + endPointDX, endPoint.getY());
                negatedCircle = new Ellipse2D.Double(
                    endPoint.getX() + endPointDX,
                    endPoint.getY() - NEGATED_SIZE / 2,
                    NEGATED_SIZE,
                    NEGATED_SIZE);
            }
        }
        else {
            // Sloped; compute the slope and end the line early.
            // angle is between -pi and pi (see Math.atan2 docs)
            double angle = atan2(endPoint.getY() - conPoint.getY(),
                                 endPoint.getX() - conPoint.getX());
            double cosAngle = cos(angle);
            double sinAngle = sin(angle);
            double endPointX = NEGATED_SIZE * cosAngle;
            double endPointY = NEGATED_SIZE * sinAngle;
            
            if (angle > -0.5 * PI && angle < 0.5 * PI) {
                nameLabelTransform.translate(endPoint.getX(), endPoint.getY());
                nameLabelTransform.rotate(angle);
                nameLabelTransform.translate(LABEL_OFFSET, 0);
                numberLabelTransform.translate(endPoint.getX(), endPoint.getY());
                numberLabelTransform.rotate(angle);
                numberLabelTransform.translate(
                    -LABEL_OFFSET -numberLabelWidth, 0);
            } else {
                nameLabelTransform.translate(endPoint.getX(), endPoint.getY());
                nameLabelTransform.rotate(angle + PI);
                nameLabelTransform.translate(
                    -LABEL_OFFSET - nameLabelWidth, 0);
                numberLabelTransform.translate(endPoint.getX(), endPoint.getY());
                numberLabelTransform.rotate(angle + PI);
                numberLabelTransform.translate(LABEL_OFFSET, 0);                
            }
            
            if (negated) {
                endPoint = new Point((long) endPointX, (long) endPointY);
            
                // You need to work the trigonometry out here to understand the
                // significance of (1 - cos) and (1 - sin).
                negatedCircle = new Ellipse2D.Double(
                    endPointX - NEGATED_SIZE * (1.0 - cosAngle),
                    endPointY + NEGATED_SIZE * (1.0 - sinAngle),
                    NEGATED_SIZE,
                    NEGATED_SIZE);
            }
        }
        
        if (negated) {
            final Stroke negatedStroke = new BasicStroke(
                    NET_WIDTH, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, MITER_LIMIT);

            g.setStroke(negatedStroke);
            g.draw(negatedCircle);
        }
        
        awtPath.moveTo((double) conPoint.getX(), (double) conPoint.getY());
        awtPath.lineTo((double) endPoint.getX(), (double) endPoint.getY());
        if (pinStyles.contains(PinStyle.BUS)) {
            g.setPaint(java.awt.Color.BLUE);
        }
        g.setStroke(stroke);
        g.draw(awtPath);
        
        try {
            g.setTransform(nameLabelTransform);
            ShapePainter.paint(g, nameLabel);
            
            g.setTransform(numberLabelTransform);
            //ShapePainter.paint(g, numberLabel);
        }
        finally {
            g.setTransform(gTransform);
        }
    }
}
