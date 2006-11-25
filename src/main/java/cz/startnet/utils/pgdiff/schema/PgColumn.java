/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores column information.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgColumn {
    /**
     * Specific statistics value.
     */
    private Integer statistics = null;

    /**
     * Column constraint.
     */
    private String constraint = null;

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
     * Setter for {@link #constraint constraint}.
     *
     * @param constraint {@link #constraint constraint}
     */
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }

    /**
     * Getter for {@link #constraint constraint}.
     *
     * @return {@link #constraint constraint}
     */
    public String getConstraint() {
        return constraint;
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
     *
     * @todo Rewrite and improve.
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

        if ((getConstraint() != null) && (getConstraint().length() > 0)) {
            sbDefinition.append(" " + getConstraint());
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
        String def = definition;

        if (def.startsWith("timestamp without time zone")) {
            type = "timestamp without time zone";

            if ("timestamp without time zone".equals(def)) {
                def = "";
            } else {
                def = def.substring("timestamp without time zone".length())
                         .trim();
            }
        } else if (def.startsWith("character varying(")) {
            if (def.matches("^character varying\\([0-9]*\\)$")) {
                type = def;
                def = "";
            } else {
                type =
                    def.substring(
                            0,
                            def.indexOf(' ', "character varying(".length()))
                       .trim();
                def = def.substring(type.length()).trim();
            }
        } else {
            if (def.indexOf(' ') == -1) {
                type = def;
                def = "";
            } else {
                type = def.substring(0, def.indexOf(' ')).trim();
                def = def.substring(def.indexOf(' ')).trim();
            }
        }

        if (def.startsWith("DEFAULT ")) {
            def = def.substring("DEFAULT ".length()).trim();

            if (def.indexOf(' ') == -1) {
                defaultValue = def;
                def = "";
            } else if (def.matches(".*::character varying.*")) {
                defaultValue =
                    def.substring(
                            0,
                            def.indexOf("::character varying")
                            + "::character varying".length()).trim();
                def = def.substring(defaultValue.length()).trim();
            } else {
                defaultValue = def.substring(0, def.indexOf(' ')).trim();
                def = def.substring(defaultValue.length()).trim();
            }
        }

        if ("NULL".equals(def)) {
            nullValue = true;
            def = "";
        } else if (def.startsWith("NULL ")) {
            nullValue = true;
            def = def.substring("NULL ".length()).trim();
        } else if ("NOT NULL".equals(def)) {
            nullValue = false;
            def = "";
        } else if (def.startsWith("NOT NULL ")) {
            nullValue = false;
            def = def.substring("NOT NULL ".length()).trim();
        }

        if (def.length() > 0) {
            constraint = def;
        }
    }
}
