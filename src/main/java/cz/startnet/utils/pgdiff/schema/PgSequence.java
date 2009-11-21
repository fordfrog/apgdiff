package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

/**
 * Stores sequence information.
 *
 * @author fordfrog
 */
public class PgSequence {

    /**
     * Value for CACHE or null if no value is specified.
     */
    private String cache;
    /**
     * Value for INCREMENT BY or null if no value is specified.
     */
    private String increment;
    /**
     * Value for MAXVALUE or null if no value is specified.
     */
    private String maxValue;
    /**
     * Value for MINVALUE or null if no value is specified.
     */
    private String minValue;
    /**
     * Name of the sequence.
     */
    private String name;
    /**
     * Value for START WITH or null if no value is specified.
     */
    private String startWith;
    /**
     * True if CYCLE, false if NO CYCLE.
     */
    private boolean cycle;

    /**
     * Creates a new PgSequence object.
     *
     * @param name name of the sequence
     */
    public PgSequence(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #cache}.
     *
     * @param cache {@link #cache}
     */
    public void setCache(final String cache) {
        this.cache = cache;
    }

    /**
     * Getter for {@link #cache}.
     *
     * @return {@link #cache}
     */
    public String getCache() {
        return cache;
    }

    /**
     * Creates and returns SQL command for creation of the sequence.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL command
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE SEQUENCE ");
        sbSQL.append(PgDiffUtils.getQuotedName(name, quoteNames));

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

        if (minValue == null) {
            sbSQL.append("NO MINVALUE");
        } else {
            sbSQL.append("MINVALUE ");
            sbSQL.append(minValue);
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
     * Setter for {@link #cycle}.
     *
     * @param cycle {@link #cycle}
     */
    public void setCycle(final boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * Getter for {@link #cycle}.
     *
     * @return {@link #cycle}
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * Creates and returns SQL command for dropping the sequence.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getDropSQL(final boolean quoteNames) {
        return "DROP SEQUENCE "
                + PgDiffUtils.getQuotedName(getName(), quoteNames) + ";";
    }

    /**
     * Setter for {@link #increment}.
     *
     * @param increment {@link #increment}
     */
    public void setIncrement(final String increment) {
        this.increment = increment;
    }

    /**
     * Getter for {@link #increment}.
     *
     * @return {@link #increment}
     */
    public String getIncrement() {
        return increment;
    }

    /**
     * Setter for {@link #maxValue}.
     *
     * @param maxValue {@link #maxValue}
     */
    public void setMaxValue(final String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Getter for {@link #maxValue}.
     *
     * @return {@link #maxValue}
     */
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * Setter for {@link #minValue}.
     *
     * @param minValue {@link #minValue}
     */
    public void setMinValue(final String minValue) {
        this.minValue = minValue;
    }

    /**
     * Getter for {@link #minValue}.
     *
     * @return {@link #minValue}
     */
    public String getMinValue() {
        return minValue;
    }

    /**
     * Setter for {@link #name}.
     *
     * @param name {@link #name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name}.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link #startWith}.
     *
     * @param startWith {@link #startWith}
     */
    public void setStartWith(final String startWith) {
        this.startWith = startWith;
    }

    /**
     * Getter for {@link #startWith}.
     *
     * @return {@link #startWith}
     */
    public String getStartWith() {
        return startWith;
    }
}
