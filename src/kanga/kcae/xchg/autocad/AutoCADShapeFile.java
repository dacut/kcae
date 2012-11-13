package kanga.kcae.xchg.autocad;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.Charsets.US_ASCII;
import static org.apache.commons.io.EndianUtils.readSwappedShort;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class AutoCADShapeFile {
    public static final int HEADER_LENGTH = 0x18;
    public static final int MAX_DESCRIPTION_LENGTH = 128;
    
    public enum ShapeFileType {
        NORMAL("AutoCAD-86 Shapes 1.0\r\n\u001a"),
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
    

    public AutoCADShapeFile(Map<Integer, AutoCADShape> shapes) {
        this(null, 0, 0, 0, shapes, false);
    }
    
    AutoCADShapeFile(
        String fontName,
        int above,
        int below,
        int modes,
        Map<Integer, AutoCADShape> shapes, boolean takeOwnership)
    {
        this.fontName = fontName;
        this.above = above;
        this.below = below;
        this.modes = modes;
        this.shapes = (takeOwnership ? shapes :
                       new HashMap<Integer, AutoCADShape>(shapes));
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
            "Unknown shape file type (header='" + header + "'");
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
        Map<Integer, AutoCADShape> shapes;
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
        shapes = new HashMap<Integer, AutoCADShape>(numChars);
        
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
                readTerminatedString(bais, bais.available());
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
                shapes.put(shapeHeader.shapeId, readShape(is, shapeHeader));
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
                }
                else {
                    instructions.add(
                        ddlParser.parse(bais, verticalOnly,
                                        shapeHeader.shapeId));
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
        if (result > 0)
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
    
    public String getFontName() {
        return this.fontName;
    }

    public int getAbove() {
        return this.above;
    }

    public int getBelow() {
        return this.below;
    }

    public int getModes() {
        return this.modes;
    }

    public Map<Integer, AutoCADShape> getShapes() {
        return this.shapes;
    }

    private final String fontName;
    private final int above;
    private final int below;
    private final int modes;
    private final Map<Integer, AutoCADShape> shapes;
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
