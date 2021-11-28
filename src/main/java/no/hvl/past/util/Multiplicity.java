package no.hvl.past.util;


import com.google.common.base.Objects;

/**
 * A class to represent the well-known notion of multiplicities as they are known from UML.
 */
public class Multiplicity {

    private final int lower;
    private final int upper;

    private Multiplicity(int lower, int upper) {
        this.lower = (upper == 1 && lower < 0) || (upper < 0 && lower < 0) ? 0 : lower; // normalisation
        this.upper = upper;
    }

    public boolean isRequired() {
        return lower >= 1;
    }

    public boolean isOptional() {
        return lower < 1;
    }

    public boolean isCollection() {
        return upper > 1 || isUpperUnbounded();
    }

    public boolean isSingleValued() {
        return upper == 1 || upper == 0;
    }

    public boolean isLowerUnbounded() {
        return this.lower <= 0;
    }

    public boolean isUpperUnbounded() {
        return this.upper < 0;
    }

    public int getLowerBound() {
        return lower;
    }

    public int getUpperBound() {
        return upper;
    }

    public Multiplicity and(Multiplicity other) {
        if (isLowerUnbounded() && isUpperUnbounded()) {
            return other;
        }

        if (isLowerUnbounded()) {
            if (other.isUpperUnbounded()) {
                return new Multiplicity(other.lower, upper);
            } else {
                return new Multiplicity(other.lower, Math.min(upper, other.upper));
            }
        }

        if (isUpperUnbounded()) {
            if (other.isLowerUnbounded()) {
                return new Multiplicity(lower, other.upper);
            } else {
                return new Multiplicity(Math.max(lower, other.lower), other.upper);
            }
        }

        if (other.isLowerUnbounded() && other.isUpperUnbounded()) {
            return this;
        }

        //

        if (other.isLowerUnbounded()) {
            return new Multiplicity(lower, Math.min(upper, other.upper));
        }

        if (other.isUpperUnbounded()) {
            return new Multiplicity(Math.min(lower, other.lower), upper);
        }

        return new Multiplicity(Math.max(lower, other.lower), Math.min(upper, other.upper));

    }

    public Multiplicity or(Multiplicity other) {
        if (isLowerUnbounded() && isUpperUnbounded()) {
            return this;
        }

        if (isLowerUnbounded()) {
            if (other.isUpperUnbounded()) {
                return new Multiplicity(lower, other.upper);
            } else {
                return new Multiplicity(lower, Math.max(upper, other.upper));
            }
        }

        if (isUpperUnbounded()) {
            if (other.isLowerUnbounded()) {
                return new Multiplicity(other.lower, upper);
            } else {
                return new Multiplicity(Math.min(lower, other.lower), upper);
            }
        }

        if (other.isLowerUnbounded() && other.isUpperUnbounded()) {
            return other;
        }

        //

        if (other.isLowerUnbounded()) {
            return new Multiplicity(other.lower, Math.max(upper, other.upper));
        }

        if (other.isUpperUnbounded()) {
            return new Multiplicity(Math.min(lower, other.lower), other.upper);
        }

        return new Multiplicity(Math.min(lower, other.lower), Math.max(upper, other.upper));

    }


    public boolean isValid(long noOfElements) {
        if (isLowerUnbounded() && isUpperUnbounded()) {
            return true;
        }

        if (isLowerUnbounded()) {
            return noOfElements <= upper;
        }

        if (isUpperUnbounded()) {
            return noOfElements >= lower;
        }

        return noOfElements >= lower && noOfElements <= upper;
    }

    public static Multiplicity of(int lowerBound) {
        return Multiplicity.of(lowerBound, -1);
    }

    public static Multiplicity of(int lowerBound, int upperBound) {
        return new Multiplicity(lowerBound, upperBound);
    }

    public static Multiplicity of(boolean required) {
        return Multiplicity.of(required, false);
    }

    public static Multiplicity of(boolean required, boolean collectionValued) {
        if (required && collectionValued) {
            return new Multiplicity(1, -1);
        } else if (required && !collectionValued) {
            return new Multiplicity(1, 1);
        } else if (!required && collectionValued) {
            return new Multiplicity(-1, -1);
        } else {
            return new Multiplicity(0, 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Multiplicity that = (Multiplicity) o;
        return lower == that.lower && upper == that.upper;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lower, upper);
    }

    @Override
    public String toString() {
        if (lower == 1 && upper == 1) {
            return "!";
        }
        if (lower == 0 && upper == 1) {
            return "?";
        }
        if (lower == 1 && isUpperUnbounded()) {
            return "+";
        }
        if (isLowerUnbounded() && isUpperUnbounded()) {
            return "*";
        }
        return (isLowerUnbounded() ? "*" : lower) + ".." + (isUpperUnbounded() ? "*" : upper);
    }
}
