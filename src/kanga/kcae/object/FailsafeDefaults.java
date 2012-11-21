package kanga.kcae.object;

import kanga.kcae.xchg.autocad.AutoCADShapeFile;

public abstract class FailsafeDefaults {
    public static final String ISO_TYPEFACE_NAME = "iso3098b";
    public static final Typeface ISO_TYPEFACE = 
        AutoCADShapeFile.find(ISO_TYPEFACE_NAME).toTypeface();
    public static final Font ISO_FONT_8MM = new Font(ISO_TYPEFACE, 8000000);

}
