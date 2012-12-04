package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Dialog.ModalityType;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import static com.google.common.collect.Maps.unmodifiableNavigableMap;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import kanga.kcae.object.FailsafeDefaults;
import kanga.kcae.object.Glyph;
import kanga.kcae.object.Path;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Typeface;
import kanga.kcae.xchg.autocad.AutoCADShapeFile;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import static org.apache.commons.io.IOUtils.closeQuietly;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TypefaceEditor extends JFrame {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(TypefaceEditor.class);
    
    public static enum TypefaceFormat {
        AUTOCAD_SHX,
        SERIALIZED_TYPEFACE
    }

    public TypefaceEditor() {
        super(makeTitle(null));
        this.file = null;
        this.glyphs = new TreeMap<Character, Glyph>();
        this.replacementGlyph = null;
        this.modified = false;
        this.menu = new AppMenuBar(this);
        this.setJMenuBar(this.menu);

        // this.getContentPane().setLayout(new BorderLayout());
        
        this.fontPreview = new PreviewPanel();
        this.getContentPane().add(this.fontPreview);//, BorderLayout.EAST);
        this.fontPreview.setVisible(true);
    }
    
    public static void newDocument() {
        new TypefaceEditor();
    }
    
    public void open() {
        OpenFileDialog dlg = new OpenFileDialog(this);
        dlg.setModalityType(ModalityType.DOCUMENT_MODAL);
        dlg.setVisible(true);
    }
    
    public void save() {
        save(this.getFile());
    }
    
    public void saveAs() {
        SaveAsFileDialog dlg = new SaveAsFileDialog(this);
        String saveAsFilename;
        File file = this.getFile();
        
        if (file != null) {
            dlg.setDirectory(file.getParent());
        }

        while (true) {
            dlg.setVisible(true);
            saveAsFilename = dlg.getFile();

            if (saveAsFilename == null) {
                return;
            }

            File saveAsFile = new File(saveAsFilename);
            try {
                this.saveAsSerializedTypeface(saveAsFile);
                this.setFile(saveAsFile);
                return;
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not write to " + saveAsFile + ":\n" + e.toString(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void close() {
        if (this.isModified()) {
            String[] options = new String[] {
                "Save", "Don't save", "Cancel close" };
            
            final File file = this.getFile();
            String message = "There are unsaved changes to this typeface";
            if (file == null) {
                message += ".";
            } else {
                message += ":\n" + file.getPath();
            }
            
            int result = JOptionPane.showOptionDialog(
                this, message, "Unsaved Changes", 
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, 
                null, options, options[0]);
            
            switch (result) {
                case JOptionPane.YES_OPTION:
                this.save();
                return;
                
                case JOptionPane.CANCEL_OPTION:
                return;
                
                default:
                break;
            }
        }
        
        this.dispose();
        return;
    }

    public String getTypefaceName() {
        return this.typefaceName;
    }

    public void setTypefaceName(String typefaceName) {
        this.typefaceName = typefaceName;
    }

    @CheckForNull
    public File getFile() {
        return this.file;
    }
    
    public void setFile(@CheckForNull File file) {
        this.file = file;
        this.setTitle(makeTitle(file));
    }
    
    public NavigableMap<Character, Glyph> getGlyphs() {
        return unmodifiableNavigableMap(this.glyphs);
    }

    public boolean isEmpty() {
        log.debug("isEmpty: name=" + this.getTypefaceName() + " file=" + this.getFile() + " gsize=" + this.glyphs.size() + " replacement=" + this.replacementGlyph);
        return ((this.getTypefaceName() == null ||
                 this.getTypefaceName().length() == 0) &&
                this.getFile() == null &&
                this.glyphs.size() == 0 &&
                this.replacementGlyph == null);
    }
    
    public boolean isModified() {
        return this.modified;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    void loadFont(File file) throws IOException {
        this.loadFont(file, detectFormatFromFilename(file));
    }
    
    void loadFont(File file, TypefaceFormat format) throws IOException {
        switch (format) {
            case AUTOCAD_SHX:
            this.loadFromAutoCADShapeFile(file);
            break;
            
            case SERIALIZED_TYPEFACE:
            this.loadFromSerializedTypeface(file);
            break;
            
            default:
            throw new IllegalArgumentException("Unknown format " + format);
        }
        
        this.setFile(file);
        
        return;
    }
    
    void loadFromAutoCADShapeFile(File file) throws IOException {
        InputStream fileStream = new FileInputStream(file);
        try {
            AutoCADShapeFile shapeFile = AutoCADShapeFile.fromCompiledFile(
                fileStream);
            this.loadFromTypeface(shapeFile.toTypeface(
                FailsafeDefaults.BLACK_500UM));
        }
        finally {
            closeQuietly(fileStream);
        }
    }
    
    void loadFromSerializedTypeface(File file) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(file));
        try {
            this.loadFromTypeface(Typeface.class.cast(ois.readObject()));
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                "File " + file + " does not contain a valid typeface.");
        }
        catch (ObjectStreamException e) {
            throw new IllegalArgumentException(
                "File " + file + " does not contain a valid typeface.");
        }
        finally {
            closeQuietly(ois);
        }
    }
    
    void loadFromTypeface(Typeface typeface) {
        this.setTypefaceName(typeface.getName());
        this.glyphs = new TreeMap<Character, Glyph>(typeface.stealGlyphs());
        this.replacementGlyph = typeface.getReplacementGlyph();
        
        this.repaint();
    }
                
    private void save(File file) {
        if (file == null) {
            this.saveAs();
        } else {
            save(file, detectFormatFromFilename(file));
        }
    }
    
    private void save(File file, TypefaceFormat format) {
        try {
            switch (format) {
                case AUTOCAD_SHX:
                throw new IllegalArgumentException(
                    "Cannot save in AutoCAD format.");

                case SERIALIZED_TYPEFACE:
                this.saveAsSerializedTypeface(file);
            }
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(
                this, "Could not write to " + file + ":\n" + e.toString(),
                "Error", JOptionPane.ERROR_MESSAGE);
            this.saveAs();
        }
    }
    
    private void saveAsSerializedTypeface(File file) throws IOException {
        Typeface tf = new Typeface(this.getName(), 0, 0, 0, 0, 0,
            this.glyphs, this.replacementGlyph);
        ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(file));
        try {
            oos.writeObject(tf);
        }
        finally {
            closeQuietly(oos);
        }
    }
    
    static String makeTitle(File file) {
        if (file == null) {
            return "Untitled - Typeface Editor";
        } else {
            return file.getName() + " - Typeface Editor";
        }
    }

    public static TypefaceFormat detectFormatFromFilename(File file) {
        return detectFormatFromFilename(file.getName());
    }
    
    public static TypefaceFormat detectFormatFromFilename(String filename) {
        if (filename.endsWith(".shx")) {
            return TypefaceFormat.AUTOCAD_SHX;
        } else if (filename.endsWith(".ctf")) {
            return TypefaceFormat.SERIALIZED_TYPEFACE;
        } else {
            throw new IllegalArgumentException("Unknown typeface format");
        }
    }
    
    /** Entry point for the typeface editor.
     *  @param  args    Initialization arguments.
     */
    public static void main(String[] args) {
        File initialFile;
        TypefaceEditor app = new TypefaceEditor();
        
        if (args.length > 0) {
            initialFile = new File(args[0]);
            try {
                app.loadFont(initialFile);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(
                    app, "Could not open " + initialFile + ":\n" + e.toString(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }
    
    @CheckForNull
    private String typefaceName;
    
    @CheckForNull
    private File file;
    
    @Nonnull
    private NavigableMap<Character, Glyph> glyphs;
    
    @CheckForNull
    private Glyph replacementGlyph;

    private transient boolean modified;
    
    @Nonnull
    private final AppMenuBar menu;

    @Nonnull
    private final PreviewPanel fontPreview;

    private static final long serialVersionUID = 1L;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("BC_UNCONFIRMED_CAST")
    class PreviewPanel extends JPanel {
        @java.lang.SuppressWarnings("synthetic-access")
        @Override
        public void paintComponent(final Graphics gstd) {
            final Graphics2D g = Graphics2D.class.cast(gstd);
            final NavigableMap<Character, Glyph> glyphs =
                TypefaceEditor.this.getGlyphs();
            final AffineTransform origTransform = g.getTransform();
            
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            
            log.debug("width=" + this.getWidth() + " height=" + this.getHeight());
            
            g.setColor(Color.BLACK);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (final Map.Entry<Character, Glyph> e : glyphs.entrySet()) {
                final char c = e.getKey();
                final Glyph glyph = e.getValue();
                final Path path = glyph.getPath();
                final Rectangle bbox = path.getBoundingBox();
                final int x = ((c - ' ') % 16) * 20;
                final int y = ((c - ' ') / 16 + 1) * 40;
                
                g.setTransform(origTransform);
                g.translate(x, y);
                
                long width = bbox.getWidth();
                if (width <= 0) {
                    width = 20;
                }
                
                long height = bbox.getHeight();
                if (height <= 0) {
                    height = 40;
                }

                log.debug("Drawing character " + c + " at " + x + "," + y + " width=" + width + " height=" + height + " bbox=" + bbox);
                
                g.scale(1e-6, -1e-6);
                ShapePainter.paint(g, path);
            }
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    static class OpenFileDialog extends FileDialog {
        OpenFileDialog() {
            this(null);
        }
        
        OpenFileDialog(TypefaceEditor frame) {
            super(frame, "Open Typeface", FileDialog.LOAD);
            this.setModalityType(ModalityType.MODELESS);
            this.setFilenameFilter(new SuffixFileFilter(
                new String[] {".typeface", ".shx" }));
            this.addWindowListener(new Listener());
        }

        static class Listener extends WindowAdapter {
            @java.lang.SuppressWarnings("synthetic-access")
            @Override
            public void windowClosed(WindowEvent e) {
                final OpenFileDialog dlg = OpenFileDialog.class.cast(
                    e.getSource());
                final String filename;
                final File file;
                
                filename = dlg.getFile();
                if (filename == null) {
                    // Operation canceled; don't do any further processing.
                    return;
                }
                    
                file = new File(dlg.getDirectory(), filename);
                dlg.setFile(null);
                dlg.setDirectory(file.getParent());

                try {
                    // If we were opened from a blank, unmodified
                    // TypefaceEditor, reuse that window.
                    Component frame = dlg.getParent();
                    
                    if (frame != null && (frame instanceof TypefaceEditor)) {
                        TypefaceEditor doc = TypefaceEditor.class.cast(frame);
                        log.info("doc found; isEmpty=" + doc.isEmpty() + " isModified=" + doc.isModified());
                        if (doc.isEmpty() && ! doc.isModified()) {
                            doc.loadFont(file);
                            return;
                        }
                    }
                    
                    log.info("frame is null? " + (frame == null));
                    log.info("frame instanceof TFE? " + (frame instanceof TypefaceEditor));
                    
                    // Need to create a new document.
                    TypefaceEditor tfe = new TypefaceEditor();
                    tfe.loadFont(file);
                    tfe.setVisible(true);
                    return;
                }
                catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(
                        null, "Typeface file " + file + " not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        null, "Typeface file " + filename +" could not " +
                            "be opened: " + ex.toString(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                    
                dlg.setVisible(true);
            }
        }

        private static final long serialVersionUID = 1L;
    }
    
    static class SaveAsFileDialog extends FileDialog {
        SaveAsFileDialog(TypefaceEditor editor) {
            super(editor, "Save Typeface", FileDialog.SAVE);
            this.setModalityType(ModalityType.DOCUMENT_MODAL);
            this.setFilenameFilter(new SuffixFileFilter(
                new String[] { ".shx" }));
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    private static class AppMenuBar extends JMenuBar {
        AppMenuBar(TypefaceEditor editor) {
            this.file = new FileMenu(editor);
            this.edit = new EditMenu(editor);
            
            this.add(this.file);
            this.add(this.edit);
        }
        
        final FileMenu file;
        final EditMenu edit;
        private static final long serialVersionUID = 1L;
    }
    
    private static class FileMenu extends JMenu {
        FileMenu(final TypefaceEditor editor) {
            super("File");
            this.newDoc = new JMenuItem("New", KeyEvent.VK_N);
            this.open = new JMenuItem("Open...", KeyEvent.VK_O);
            this.save = new JMenuItem("Save", KeyEvent.VK_S);
            this.saveAs = new JMenuItem("Save As...", KeyEvent.VK_A);
            this.close = new JMenuItem("Close", KeyEvent.VK_C);
            
            this.newDoc.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
            this.open.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));

            this.add(this.newDoc);
            this.add(this.open);
            this.add(this.save);
            this.add(this.saveAs);
            this.add(this.close);
            
            this.newDoc.addActionListener(new NewDocumentAction());
            this.open.addActionListener(new OpenDocumentAction(editor));
            this.save.addActionListener(new SaveDocumentAction(editor));
            this.saveAs.addActionListener(new SaveAsDocumentAction(editor));
            this.close.addActionListener(new CloseDocumentAction(editor));
        }
        
        static class NewDocumentAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                TypefaceEditor.newDocument();
            }
        };
        
        abstract static class DocumentAction implements ActionListener {
            DocumentAction(final TypefaceEditor editor) {
                this.editor = editor;
            }
            
            protected final TypefaceEditor editor;
        };
        
        static class OpenDocumentAction extends DocumentAction {
            OpenDocumentAction(final TypefaceEditor editor) {
                super(editor);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.editor.open();
            }
        };

        static class SaveDocumentAction extends DocumentAction {
            SaveDocumentAction(final TypefaceEditor editor) {
                super(editor);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.editor.save();
            }
        };
        
        static class SaveAsDocumentAction extends DocumentAction {
            SaveAsDocumentAction(final TypefaceEditor editor) {
                super(editor);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.editor.saveAs();
            }
        };

        static class CloseDocumentAction extends DocumentAction {
            CloseDocumentAction(final TypefaceEditor editor) {
                super(editor);
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                this.editor.close();
            }
        };

        final JMenuItem newDoc;
        final JMenuItem open;
        final JMenuItem save;
        final JMenuItem saveAs;
        final JMenuItem close;

        private static final long serialVersionUID = 1L;
    }
    
    private static class EditMenu extends JMenu {
        EditMenu(TypefaceEditor editor) {
            super("Edit");
            this.undo = new JMenuItem("Undo");
            this.redo = new JMenuItem("Redo");
            this.cut = new JMenuItem("Cut");
            this.copy = new JMenuItem("Copy");
            this.paste = new JMenuItem("Paste");
            
            this.add(this.undo);
            this.add(this.redo);
            this.add(this.cut);
            this.add(this.copy);
            this.add(this.paste);
        }
        
        final JMenuItem undo;
        final JMenuItem redo;
        final JMenuItem cut;
        final JMenuItem copy;
        final JMenuItem paste;
        
        private static final long serialVersionUID = 1L;
    }
}
