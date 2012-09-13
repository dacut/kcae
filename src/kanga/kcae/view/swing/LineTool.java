package kanga.kcae.view.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import kanga.kcae.object.ClosePath;
import kanga.kcae.object.LineTo;
import kanga.kcae.object.MoveTo;
import kanga.kcae.object.Path;
import kanga.kcae.object.Point;
import kanga.kcae.object.Symbol;

public class LineTool extends MeasuredViewToolAdapter {
    public LineTool(final SymbolView symView) {
        this.symView = symView;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        this.trackingPoint = this.symView.roundToGrid(
            this.symView.screenPointToQuanta(e.getPoint()));
        this.symView.repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        this.mouseMoved(e);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            final Point finalPoint = this.symView.roundToGrid(
                this.symView.screenPointToQuanta(e.getPoint()));
            
            if (e.getClickCount() == 2) {
                final Symbol sym = this.symView.getSymbol();                
                final Path symPath = new Path();
            
                boolean firstPoint = true;
                for (final Point p : this.points) {
                    if (firstPoint) {
                        symPath.addInstruction(new MoveTo(p));
                        firstPoint = false;
                    } else {
                        symPath.addInstruction(new LineTo(p));
                    }
                }
                
                if (finalPoint.equals(this.points.get(0))) {
                    symPath.addInstruction(new ClosePath());
                } else {
                    symPath.addInstruction(new LineTo(finalPoint));
                }
                
                sym.getShapes().addShape(symPath);
                this.points.clear();
            } else {
                this.points.add(finalPoint);
            }
            
            this.trackingPoint = null;
            this.symView.repaint();
        }
    }
    
    @Override
    @SuppressWarnings(value={"BC_UNCONFIRMED_CAST"})
    public void paintOverlay(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        Path2D.Double path = new Path2D.Double();
        boolean firstPoint = true;
        
        for (final Point p : this.points) {
            if (firstPoint) {
                path.moveTo(p.getX(), p.getY());
                firstPoint = false;
            }
            else {
                path.lineTo(p.getX(), p.getY());
            }
        }
        
        if (this.trackingPoint != null) {
            if (! firstPoint) {
                path.lineTo(this.trackingPoint.getX(),
                            this.trackingPoint.getY());
            }
            
            final AffineTransform originalTransform = g.getTransform();
            try {
                g.setTransform(new AffineTransform());
                g.setColor(Color.BLUE);
                g.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE,
                                            BasicStroke.JOIN_MITER, 2.0f));
                Point2D tpScreenCenter = this.symView.quantaPointToScreen(
                    this.trackingPoint);
                Rectangle2D.Double tpBox = new Rectangle2D.Double(
                    tpScreenCenter.getX() - 5,
                    tpScreenCenter.getY() - 5,
                    10,
                    10);
                g.draw(tpBox);
            }
            finally {
                g.setTransform(originalTransform);
            }
        }
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1e5f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 2.0f));
        g.draw(path);
    }
    
    @Override
    public void enabled() {
        this.symView.setCursor(
            Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    @Override
    public void disabled() {
        this.symView.setCursor(
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    final List<Point> points = new ArrayList<Point>();
    Point trackingPoint = null;
    final SymbolView symView;
    
    private static final long serialVersionUID = 1L;
}
