/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores sequence information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgSequence {
    /**
     * Value for CACHE or null if no value is specified.
     */
    private String cache = null;

    /**
     * Value for INCREMENT BY or null if no value is specified.
     */
    private String increment = null;

    /**
     * Value for MAXVALUE or null if no value is specified.
     */
    private String maxValue = null;

    /**
     * Value for MINVALUE or null if no value is specified.
     */
    private String minValue = null;

    /**
     * Name of the sequence.
     */
    private String name = null;

    /**
     * Value for START WITH or null if no value is specified.
     */
    private String startWith = null;

    /**
     * True if CYCLE, false if NO CYCLE.
     */
    private boolean cycle = false;

    /**
     * Creates a new PgSequence object.
     *
     * @param name name of the sequence
     */
    public PgSequence(final String name) {
        this.setName(name);
    }

    /**
     * Setter for {@link #cache cache}.
     *
     * @param cache {@link #cache cache}
     */
    public void setCache(final String cache) {
        this.cache = cache;
    }

    /**
     * Getter for {@link #cache cache}.
     *
     * @return {@link #cache cache}
     */
    public String getCache() {
        return cache;
    }

    /**
     * Setter for {@link #cycle cycle}.
     *
     * @param cycle {@link #cycle cycle}
     */
    public void setCycle(final boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * Getter for {@link #cycle cycle}.
     *
     * @return {@link #cycle cycle}
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * Setter for {@link #increment increment}.
     *
     * @param increment {@link #increment increment}
     */
    public void setIncrement(final String increment) {
        this.increment = increment;
    }

    /**
     * Getter for {@link #increment increment}.
     *
     * @return {@link #increment increment}
     */
    public String getIncrement() {
        return increment;
    }

    /**
     * Setter for {@link #maxValue maxValue}.
     *
     * @param maxValue {@link #maxValue maxValue}
     */
    public void setMaxValue(final String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Getter for {@link #maxValue maxValue}.
     *
     * @return {@link #maxValue maxValue}
     */
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * Setter for {@link #minValue minValue}.
     *
     * @param minValue {@link #minValue minValue}
     */
    public void setMinValue(final String minValue) {
        this.minValue = minValue;
    }

    /**
     * Getter for {@link #minValue minValue}.
     *
     * @return {@link #minValue minValue}
     */
    public String getMinValue() {
        return minValue;
    }

    /**
     * Setter for {@link #name name}.
     *
     * @param name {@link #name name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name name}.
     *
     * @return {@link #name name}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns SQL command for creation of SEQUENCE.
     *
     * @return SQL command for creation of SEQUENCE
     */
    public String getSequenceSQL() {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE SEQUENCE ");
        sbSQL.append(name);

        if (startWith != null) {
            sbSQL.append("\n\tSTART WITH ");
            sbSQL.append(startWith);
        }

        if (increment != null) {
            sbSQL.append("\n\tINCREMENT BY ");
            sbSQL.append(increment);
        }

        sbSQL.append("\n\t");

        if (maxValue == null) {
            sbSQL.append("NO MAXVALUE");
        } else {
            sbSQL.append("MAXVALUE ");
            sbSQL.append(maxValue);
        }

        sbSQL.append("\n\t");

        if (maxValue == null) {
            sbSQL.append("NO MINVALUE");
        } else {
            sbSQL.append("MINVALUE ");
            sbSQL.append(maxValue);
        }

        if (cache != null) {
            sbSQL.append("\n\tCACHE ");
            sbSQL.append(cache);
        }

        if (cycle) {
            sbSQL.append("\n\tCYCLE");
        }

        sbSQL.append(';');

        return sbSQL.toString();
    }

    /**
     * Setter for {@link #startWith startWith}.
     *
     * @param startWith {@link #startWith startWith}
     */
    public void setStartWith(final String startWith) {
        this.startWith = startWith;
    }

    /**
     * Getter for {@link #startWith startWith}.
     *
     * @return {@link #startWith startWith}
     */
    public String getStartWith() {
        return startWith;
    }
}
