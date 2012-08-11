package kanga.kcae.object;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Extents {
    public static class Double implements Comparable<Double>, Serializable {
        private static final long serialVersionUID = 7081671937119139105L;

        public Double(final double min, final double max) {
            this.min = min;
            this.max = max;
        }
        
        @Override
        public boolean equals(final Object otherObj) {
            if      (otherObj == null) { return false; }
            else if (otherObj == this) { return true; }
            else if (!(otherObj instanceof Double)) { return false; }
            
            final Double other = (Double) otherObj;
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
        public int compareTo(final Double other) {
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
        
        public final double min;
        public final double max; 
    }
    
    public static class Long implements Comparable<Long>, Serializable {
        private static final long serialVersionUID = -6853230783288454751L;

        public Long(final long min, final long max) {
            this.min = min;
            this.max = max;
        }
        
        @Override
        public boolean equals(final Object otherObj) {
            if      (otherObj == null) { return false; }
            else if (otherObj == this) { return true; }
            else if (!(otherObj instanceof Long)) { return false; }
            
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
        public int compareTo(final Long other) {
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
    }
}
