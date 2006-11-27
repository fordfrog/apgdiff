/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores column information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgColumn {
    /**
     * Specific statistics value.
     */
    private Integer statistics = null;

    /**
     * Default value of the column.
     */
    private String defaultValue = null;

    /**
     * Name of the column.
     */
    private String name = null;

    /**
     * Type of the column.
     */
    private String type = null;

    /**
     * Determines whether null value is allowed in the column.
     */
    private boolean nullValue = true;

    /**
     * Creates a new PgColumn object.
     *
     * @param name name of the column
     */
    public PgColumn(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #defaultValue defaultValue}.
     *
     * @param defaultValue {@link #defaultValue defaultValue}
     */
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for {@link #defaultValue defaultValue}.
     *
     * @return {@link #defaultValue defaultValue}
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns full definition of the column.
     *
     * @return full definition of the column
     */
    public String getFullDefinition() {
        final StringBuilder sbDefinition = new StringBuilder();
        sbDefinition.append(name + " " + type);

        if ((defaultValue != null) && (defaultValue.length() > 0)) {
            sbDefinition.append(" DEFAULT " + defaultValue);
        }

        if (!nullValue) {
            sbDefinition.append(" NOT NULL");
        }

        return sbDefinition.toString();
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
     * Setter for {@link #nullValue nullValue}.
     *
     * @param nullValue {@link #nullValue nullValue}
     */
    public void setNullValue(final boolean nullValue) {
        this.nullValue = nullValue;
    }

    /**
     * Getter for {@link #nullValue nullValue}.
     *
     * @return {@link #nullValue nullValue}
     */
    public boolean getNullValue() {
        return nullValue;
    }

    /**
     * Setter for {@link #statistics statistics}.
     *
     * @param statistics {@link #statistics statistics}
     */
    public void setStatistics(final Integer statistics) {
        this.statistics = statistics;
    }

    /**
     * Getter for {@link #statistics statistics}.
     *
     * @return {@link #statistics statistics}
     */
    public Integer getStatistics() {
        return statistics;
    }

    /**
     * Setter for {@link #type type}.
     *
     * @param type {@link #type type}
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Getter for {@link #type type}.
     *
     * @return {@link #type type}
     */
    public String getType() {
        return type;
    }

    /**
     * Parses definition of the column
     *
     * @param definition definition of the column
     */
    public void parseDefinition(final String definition) {
        final int posDefault = definition.indexOf(" DEFAULT ");
        final int posNotNull = definition.indexOf(" NOT NULL");

        if (posDefault > -1) {
            setType(definition.substring(0, posDefault).trim());
        } else if (posNotNull > -1) {
            setType(definition.substring(0, posNotNull).trim());
        } else {
            setType(definition.trim());
        }

        if (posDefault > -1) {
            if (posNotNull > -1) {
                setDefaultValue(
                        definition.substring(
                                posDefault + " DEFAULT ".length(),
                                posNotNull).trim());
            } else {
                setDefaultValue(
                        definition.substring(posDefault + " DEFAULT ".length())
                                  .trim());
            }
        }

        setNullValue(posNotNull == -1);
    }
}
