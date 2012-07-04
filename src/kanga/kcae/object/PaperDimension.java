package kanga.kcae.object;

public enum PaperDimension {
    AnsiA( 215900000,  279400000, true, false),
    AnsiB( 355600000,  431800000, true, false),
    AnsiC( 431800000,  558800000, true, false),
    AnsiD( 558800000,  863600000, true, false),
    AnsiE( 863600000, 1117600000, true, false),
    A0(    841000000, 1189000000, false, true),
    A1(    594000000,  841000000, false, true),
    A2(    420000000,  594000000, false, true),
    A3(    297000000,  420000000, false, true),
    A4(    210000000,  297000000, false, true),
    A5(    148000000,  210000000, false, true),
    A6(    105000000,  148000000, false, true),
    A7(     74000000,  105000000, false, true),
    A8(     52000000,   74000000, false, true),
    A9(     37000000,   52000000, false, true),
    A10(    26000000,   37000000, false, true),
    
    MinA4AnsiA(210000000, 279400000, false, false),
    MinA3AnsiB(297000000, 431800000, false, false),
    MinA2AnsiC(420000000, 558800000, false, false),
    MinA1AnsiD(558800000, 841000000, false, false),
    MinA0AnsiE(841000000, 1117600000, false, false);
    
    PaperDimension(
        final long portraitWidth,
        final long portraitHeight,
        final boolean isAnsi,
        final boolean isISO)
    {
        this.portrait = new Dimension(portraitWidth, portraitHeight);
        this.landscape = new Dimension(portraitHeight, portraitWidth);
        this.isAnsi = isAnsi;
        this.isISO = isISO;
    }
    
    public final Dimension portrait;
    public final Dimension landscape;
    public final boolean isAnsi;
    public final boolean isISO;
}
