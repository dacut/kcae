package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Extents {
    /** A range of double values expressing the extent of some geometry.
     */
    public static class Double implements Comparable<Double>, Serializable {
        public Double(double min, double max) {
            if (min > max) {
                throw new IllegalArgumentException(
                    "min cannot be greater than max");
            }

            this.min = min;
            this.max = max;
        }
        
        @Override
        @SuppressWarnings(value = {"FE_FLOATING_POINT_EQUALITY"} )
        public boolean equals(@CheckForNull Object otherObj) {
            if      (otherObj == null)                       { return false; }
            else if (otherObj == this)                       { return true; }
            else if (otherObj.getClass() != this.getClass()) { return false; }
            
            final Double other = Double.class.cast(otherObj);
            return this.min == other.min && this.max == other.max;
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder(23, 29)
                .append(this.min)
                .append(this.max)
                .toHashCode();
        }
        
        @Override
        public int compareTo(@Nonnull Double other) {
            if      (this.min < other.min) { return -1; }
            else if (this.min > other.min) { return +1; }
            else if (this.max < other.max) { return -1; }
            else if (this.max > other.max) { return +1; }
            else                           { return  0; }
        }
        
        @Override
        @Nonnull
        public String toString() {
            return "Extents(" + this.min + ", " + this.max + ")";
        }
        
        public final double min;
        public final double max;
        private static final long serialVersionUID = 1L;
    }
    
    /** A range of long values expressing the extent of some geometry.
     */
    public static class Long implements Comparable<Long>, Serializable {
        public Long(final long min, final long max) {
            this.min = min;
            this.max = max;
        }
        
        @Override
        public boolean equals(@CheckForNull Object otherObj) {
            if (otherObj == null) { return false; }
            else if (otherObj == this) { return true; }
            else if (this.getClass() != otherObj.getClass()) { return false; }
            
            final Long other = (Long) otherObj;
            return this.min == other.min && this.max == other.max;
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder(23, 29)
                .append(this.min)
                .append(this.max)
                .toHashCode();
        }
        
        @Override
        public int compareTo(@Nonnull Long other) {
            if      (this.min < other.min) { return -1; }
            else if (this.min > other.min) { return +1; }
            else if (this.max < other.max) { return -1; }
            else if (this.max > other.max) { return +1; }
            else                           { return  0; }
        }
        
        @Override
        public String toString() {
            return "Extents(" + this.min + ", " + this.max + ")";
        }
        
        public final long min;
        public final long max;

        private static final long serialVersionUID = 1L;
    }
}
