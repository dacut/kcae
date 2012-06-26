package kanga.kcae.object;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.rint;
import static java.lang.Math.round;
import static java.lang.Math.signum;

/** Utility class for formatting values in engineering notation.
 */
public abstract class EngFormatter {
    private final static String prefixes[] = {
        "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P",
    };

    /** Format a value in engineering notation.
    *
    *  <p>Engineering notation is similar to scientific notation with powers
    *  of ten restricted to multiples of 3 (15.47e+6 instead of 1.547e+7)
    *  and, where possible, replaced with a Greek prefix.</p>
    *
    *  <p>This version of the API inserts a space between the digits and
    *  Greek prefix/unit and uses a default epsilon value of 1e-12.</p>
    *
    *  @param value    The value to format.
    *  @param significantFigures The number of significant figures to show.
    *  @param units    The units to display after the Greek prefix (e.g.,
    *                  {@code "m"} for meters).
    *
    *  @return The {@code value} formatted in engineering notation.
    */
   public static String format(
       final double value,
       final int significantFigures,
       final String units)
   {
       return format(value, significantFigures, units, " ", 1e-12);
   }

   /** Format a value in engineering notation.
     *
     *  <p>Engineering notation is similar to scientific notation with powers
     *  of ten restricted to multiples of 3 (15.47e+6 instead of 1.547e+7)
     *  and, where possible, replaced with a Greek prefix.</p>
     *
     *  <p>This version of the API inserts a space between the digits and
     *  Greek prefix/unit.</p>
     *
     *  @param value    The value to format.
     *  @param significantFigures The number of significant figures to show.
     *  @param units    The units to display after the Greek prefix (e.g.,
     *                  {@code "m"} for meters).
     *
     *  @return The {@code value} formatted in engineering notation.
     */
    public static String format(
        final double value,
        final int significantFigures,
        final String units,
        final double epsilon)
    {
        return format(value, significantFigures, units, " ", epsilon);
    }

    /** Format a value in engineering notation.
     *
     *  Engineering notation is similar to scientific notation with powers
     *  of ten restricted to multiples of 3 (15.47e+6 instead of 1.547e+7)
     *  and, where possible, replaced with a Greek prefix.
     *
     *  @param value    The value to format.
     *  @param significantFigures The number of significant figures to show.
     *  @param units    The units to display after the Greek prefix (e.g.,
     *                  {@code "m"} for meters).
     *  @param interspace The string to insert between the number and the
     *                  Greek prefix (typically {@code " "}).
     *  @param epsilon  The threshold for values to be considered equal to zero.
     *
     *  @return The {@code value} formatted in engineering notation.
     */
    public static String format(
        final double value,
        final int significantFigures,
        String units,
        String interspace,
        double epsilon)
    {
        if (significantFigures <= 0) {
            throw new IllegalArgumentException(
                "significantFigures must be positive");
        }

        if (units == null) {
            units = "";
        }

        if (interspace == null) {
            interspace = "";
        }

        if (value < epsilon && value > -epsilon) {
            return (String.format("%." + (significantFigures - 1) + "f" , 0.0) +
                    interspace + units);
        }

        // value == sign * normalized * 10**scale, where:
        // sign is -1.0 if value < 0.0, +1.0 if value > 0.0.
        // avalue is the absolute value.
        // scale is the greatest integral power of ten below avalue
        // normalized is between 1.0 (inclusive) and 10.0 (exclusive).
        final double sign = signum(value);
        final double avalue = abs(value);
        final double scale = floor(log10(avalue));
        double normalized = avalue * pow(10.0, -scale);

        // Round the normalized value out to the specified significant
        // figures.  For example:
        //     normalized   sigFig | engNormalized
        //     --------------------+---------------
        //        2.51749        1 |  3.0
        //        2.51749        2 |  2.5
        //        2.51749        3 |  2.52
        //        2.51749        4 |  2.517
        //        2.51749        5 |  2.5175
        //        2.51749        6 |  2.51749

        double engNormalized = pow(10.0, -significantFigures + 1) *
            rint(normalized * pow(10.0, significantFigures - 1));

        // Make scale an integral value.
        long iscale = round(scale);

        // Move iscale down to the nearest engineering unit (power of three).
        // This will affect the precision formatting -- %.3f specifies 3 decimal
        // places, not 3 significant figures (e.g. 4.579, not 4.57).
        int engPrecision = significantFigures - 1;
        while (iscale % 3 != 0) {
            --iscale;
            --engPrecision;
            engNormalized *= 10.0;
        }

        if (engPrecision < 0) {
            engPrecision = 0;
        }

        int prefix = (int) (iscale / 3) + 5;
        if (prefix < 0 || prefix >= prefixes.length) {
            // Value is smaller than femto or greater than peta.  Give up and
            // format it in scientific notation.
            return (String.format("%." + (significantFigures - 1) + "e",
                                  value) + interspace + units);
        }
        
        return (String.format("%." + engPrecision + "f",
                              sign * engNormalized) +
                interspace + prefixes[prefix] + units);
    }
}