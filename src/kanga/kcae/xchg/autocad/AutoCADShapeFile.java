package kanga.kcae.xchg.autocad;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kanga.kcae.object.Glyph;
import kanga.kcae.object.Path;
import kanga.kcae.object.Typeface;
import kanga.kcae.object.UnknownTypefaceException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import static org.apache.commons.io.Charsets.US_ASCII;
import static org.apache.commons.io.EndianUtils.readSwappedShort;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

public class AutoCADShapeFile {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(AutoCADShapeFile.class);
    public static final int HEADER_LENGTH = 0x18;
    public static final int MAX_DESCRIPTION_LENGTH = 128;
    
    public enum ShapeFileType {
        NORMAL("AutoCAD-86 shapes 1.0\r\n\u001a"),
        BIGFONT("AutoCAD-86 bigfont 1.0\r\n"),
        UNIFONT("AutoCAD-86 unifont 1.0\r\n");
        
        ShapeFileType(String headerName) {
            this.headerName = headerName;
            assert this.headerName.length() == HEADER_LENGTH;
        }
        
        public String getHeaderName() {
            return this.headerName;
        }
        
        private final String headerName;
    }
    

    public AutoCADShapeFile(Map<Character, AutoCADShape> shapes) {
        this(null, 0, 0, 0, shapes, false);
    }
    
    AutoCADShapeFile(
        @Nullable final String fontName,
        final int above,
        final int below,
        final int modes,
        @Nonnull final Map<Character, AutoCADShape> shapes,
        final boolean takeOwnership)
    {
        this.fontName = fontName;
        this.above = above;
        this.below = below;
        this.modes = modes;
        this.shapes = (takeOwnership ? shapes :
                       new TreeMap<Character, AutoCADShape>(shapes));
    }
    
    public boolean isFont() {
        return this.fontName != null;
    }
    
    public String getFontName() {
        return this.fontName;
    }

    public int getAbove() {
        if (! this.isFont()) {
            throw new UnsupportedOperationException(
                "Non-font shape file does not support getAbove()");
        }
        
        return this.above;
    }

    public int getBelow() {
        if (! this.isFont()) {
            throw new UnsupportedOperationException(
                "Non-font shape file does not support getBelow()");
        }

        return this.below;
    }

    public int getModes() {
        if (! this.isFont()) {
            throw new UnsupportedOperationException(
                "Non-font shape file does not support getModes()");
        }

        return this.modes;
    }

    public Map<Character, AutoCADShape> getShapes() {
        return this.shapes;
    }
    
    public Typeface toTypeface() {
        final Map<Character, AutoCADShape> shapes = this.getShapes();
        final Map<Character, Glyph> glyphs = new HashMap<Character, Glyph>();
        final Glyph replacement = new Glyph(new Path(), 0, null);
        
        for (final Map.Entry<Character, AutoCADShape> entry : shapes.entrySet())
        {
            glyphs.put(entry.getKey(), entry.getValue().toGlyph(shapes));
        }
        
        return new Typeface(
            this.getFontName(),
            this.getBelow(),
            5,
            this.getAbove(),
            this.getAbove(),
            5,
            glyphs,
            replacement);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        else if (otherObj == this) { return true; }
        else if (otherObj.getClass() != this.getClass()) { return false; }
        
        AutoCADShapeFile other = AutoCADShapeFile.class.cast(otherObj);

        EqualsBuilder eb = new EqualsBuilder();

        if (this.isFont()) {
            if (! other.isFont()) { return false; }
            eb.append(this.getFontName(), other.getFontName())
                .append(this.getAbove(), other.getAbove())
                .append(this.getBelow(), other.getBelow())
                .append(this.getModes(), other.getModes());
        }
        
        eb.append(this.getShapes(), other.getShapes());
        
        return eb.isEquals();
    }
    
    @Override
    public int hashCode() {
        boolean isFont = this.isFont();
        
        HashCodeBuilder hb = new HashCodeBuilder(686498797, 507758837);
        hb.append(isFont);
        if (isFont) {
            hb.append(this.getFontName()).append(this.getAbove())
                .append(this.getBelow()).append(this.getModes());
        }
        
        hb.append(this.getShapes());
        return hb.toHashCode();
    }
    
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this, SHORT_PREFIX_STYLE);
        if (this.isFont()) {
            sb.append("fontName", this.getFontName())
              .append("above", this.getAbove())
              .append("below", this.getBelow())
              .append("modes", this.getModes());
        }
        
        sb.append("shapes", this.getShapes());
        return sb.toString();
    }
    
    private static String resourcePackage = "kanga/kcae/res/";
    public static AutoCADShapeFile find(String name) {
        InputStream typefaceStream = ClassLoader.getSystemResourceAsStream(
            resourcePackage + name + ".shx");
        if (typefaceStream == null) {
            throw new UnknownTypefaceException("Unknown typeface " + name);
        }
        
        try {
            return fromCompiledFile(typefaceStream);
        }
        catch (Exception e) {
            throw new UnknownTypefaceException("Unknown typeface " + name, e);
        }
        finally {
            closeQuietly(typefaceStream);
        }
    }

    public static AutoCADShapeFile fromCompiledFile(InputStream is)
        throws IOException
    {
        ShapeFileType sft = identifyFileType(is);
        switch (sft) {
            case NORMAL:
            return parseNormalFontFile(is);
            
            case BIGFONT:
            return parseBigFontFile(is);
            
            case UNIFONT:
            default:
            return parseUnicodeFontFile(is);
        }
    }
    
    public static ShapeFileType identifyFileType(InputStream is)
        throws IOException
    {
        byte[] headerBytes = new byte[HEADER_LENGTH];
        String header;
        
        is.read(headerBytes);
        header = new String(headerBytes, US_ASCII);
        
        for (ShapeFileType sft : ShapeFileType.values()) {
            if (sft.getHeaderName().equals(header)) {
                return sft;
            }
        }
        
        throw new UnknownShapeFileTypeException(
            "Unknown shape file type (header=\"" + escapeJava(header) + "\")");
    }
    
    static class ShapeEntryHeader {
        ShapeEntryHeader(InputStream is) throws IOException {
            this.shapeId = readSwappedUShort(is);
            this.byteLength = readSwappedUShort(is);
        }
        
        int shapeId;
        int byteLength;
    };

    public static AutoCADShapeFile parseNormalFontFile(InputStream is)
        throws IOException
    {
        long skipBytes;
        int numChars;
        ShapeEntryHeader[] shapeHeaders;
        Map<Character, AutoCADShape> shapes =
            new TreeMap<Character, AutoCADShape>();
        String fontName = null;
        int above = 0;
        int below = 0;
        int modes = 0;
        
        // Skip two unknown shorts.
        skipBytes = is.skip(4);
        if (skipBytes != 4)
            throw new InvalidShapeFileException("File too short");
        
        numChars = readSwappedUShort(is);
        shapeHeaders = new ShapeEntryHeader[numChars];
        
        // Read the character names and the byte lengths they occupy.
        for (int i = 0; i < numChars; ++i) {
            shapeHeaders[i] = new ShapeEntryHeader(is);
        }
        
        // Read the data for each character.
        for (ShapeEntryHeader shapeHeader : shapeHeaders) {
            if (shapeHeader.shapeId == 0) {
                // Font header; read data about the font.
                ByteArrayInputStream bais = readDataBlock(
                    is, shapeHeader.byteLength, 0);
                fontName = readTerminatedString(bais, bais.available());
                above = bais.read();
                below = bais.read();
                modes = bais.read();
                int terminator = bais.read();
                
                if (above == -1 || below == -1 || modes == -1 ||
                    terminator == -1)
                {
                    throw new EOFException(
                        "Unexpected end-of-file while reading font header");
                }
            } else {
                // Normal shape entry.
                shapes.put(Character.valueOf((char) shapeHeader.shapeId),
                           readShape(is, shapeHeader));
            }
        }
        
        return new AutoCADShapeFile(
            fontName, above, below, modes, shapes, true);
    }
    
    static AutoCADShape readShape(InputStream is, ShapeEntryHeader shapeHeader)
        throws IOException
    {
        List<ShapeInstruction> instructions = new ArrayList<ShapeInstruction>();

        // Read the entire record; this prevents the shape from reading beyond
        // its bounds.
        ByteArrayInputStream bais = readDataBlock(
            is, shapeHeader.byteLength, shapeHeader.shapeId);

        try {
            // Shape name starts the record, followed by a nul.
            String shapeName = readTerminatedString(bais, bais.available());
            
            boolean verticalOnly = false;
            while (true) {
                int icode = bais.read();
                
                if (icode == -1) {
                    throw new InvalidShapeFileException(
                        "Shape " + shapeHeader.shapeId + " truncated");
                }
                else if (icode == 0) {
                    break;
                }
                else if (icode == 14) {
                    verticalOnly = true;
                }
                else if (icode < 15) {
                    ShapeInstructionParser<?> parser = parsers.get(icode);
                    
                    try {
                        do {
                            ShapeInstruction si = parser.parse(
                                bais, verticalOnly, shapeHeader.shapeId);
                            instructions.add(si);
                            
                            // If this is an array, keep reading instructions
                            // until the array reader aborts.
                        } while (parser.isArray());
                    }
                    catch (ShapeInstructionParser.ArrayDone e) {
                        assert parser.isArray() :
                            ("Parser threw ArrayDone but we weren't reading " +
                             "an array");
                    }
                    
                    verticalOnly = false;
                }
                else {
                    instructions.add(
                        ddlParser.parse(bais, verticalOnly,
                                        shapeHeader.shapeId));

                    verticalOnly = false;
                }
            }
            
            return new AutoCADShape(shapeName, instructions);
        }
        finally {
            closeQuietly(bais);
        }
    }

    /** Parse a big (Asian) font file.
     * 
     *  TODO: Implement.
     * 
     *  @param is       The input stream containing the font, positioned after
     *                  the header.
     *  @return The parsed font.
     *  @throws IOException if an I/O operation fails.
     */
    public static AutoCADShapeFile parseBigFontFile(InputStream is)
        throws IOException
    {
        throw new UnsupportedOperationException(
            "parseBigFontFile not implemented");
    }

    /** Parse a Unicode font file.
     * 
     *  TODO: Implement.
     * 
     *  @param is       The input stream containing the font, positioned after
     *                  the header.
     *  @return The parsed font.
     *  @throws IOException if an I/O operation fails.
     */
    public static AutoCADShapeFile parseUnicodeFontFile(InputStream is)
        throws IOException
    {
        throw new UnsupportedOperationException(
            "parseUnicodeFontFile not implemented");
    }
    
    public static int readSwappedUShort(InputStream is) throws IOException {
        short result = readSwappedShort(is);
        if (result >= 0)
            return result;
        else
            return 65536 + (int) result;
    }
    
    public static ByteArrayInputStream readDataBlock(
        InputStream is,
        int length,
        int shapeId)
        throws IOException
    {
        byte[] data = new byte[length];
        int nRead = is.read(data);
        if (nRead != length) {
            throw new EOFException(
                "Unexpected end-of-file while reading shape " + shapeId);
        }
        
        return new ByteArrayInputStream(data);
    }
    
    public static String readTerminatedString(InputStream is, int maxLength)
        throws IOException
    {
        byte[] str = new byte[maxLength];
        
        for (int i = 0; i < maxLength; ++i) {
            int c = is.read();
            if (c == -1) {
                throw new java.io.EOFException(
                    "Unexpected end-of-file while reading terminated string");
            }
            
            str[i] = (byte) c;
            
            if (c == 0) {
                // Terminator found.
                return new String(str, 0, i, US_ASCII);
            }
        }
        
        return new String(str, US_ASCII);
    }
    
    private final String fontName;
    private final int above;
    private final int below;
    private final int modes;
    private final Map<Character, AutoCADShape> shapes;
    private static final
        List<ShapeInstructionParser<? extends ShapeInstruction>> parsers;
    private static final DrawDirectionalLine.Parser ddlParser =
        new DrawDirectionalLine.Parser();
    
    static {
        parsers =
            new ArrayList<ShapeInstructionParser<? extends ShapeInstruction>>();
        parsers.add(null);
        parsers.add(new ActivateDraw.Parser());
        parsers.add(new DeactivateDraw.Parser());
        parsers.add(new DivideVectorLength.Parser());
        parsers.add(new MultiplyVectorLength.Parser());
        parsers.add(new PushLocation.Parser());
        parsers.add(new PopLocation.Parser());
        parsers.add(new DrawSubshape.Parser());
        parsers.add(new RelativeMoveTo.Parser());
        parsers.add(new RelativeMoveTo.ArrayParser());
        parsers.add(new DrawArc.OctantArcParser());
        parsers.add(new DrawArc.FractionalArcParser());
        parsers.add(new DrawArc.BulgeArcParser());
        parsers.add(new DrawArc.ArrayBulgeArcParser());
    }
}
