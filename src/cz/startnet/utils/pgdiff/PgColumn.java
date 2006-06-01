/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

/**
 * Stores column information.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgColumn {
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
     * Creates a new instance of PgColumn.
     */
    public PgColumn() {
    }

    /**
     * Creates a new PgColumn object.
     *
     * @param name name of the column
     */
    public PgColumn(String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #constraint constraint}.
     *
     * @param constraint {@link #constraint constraint}
     */
    public void setConstraint(String constraint) {
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
    public void setDefaultValue(String defaultValue) {
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
        StringBuilder sbDefinition = new StringBuilder();
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
    public void setName(String name) {
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
    public void setNullValue(boolean nullValue) {
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
     * Setter for {@link #type type}.
     *
     * @param type {@link #type type}
     */
    public void setType(String type) {
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
    public void parseDefinition(String definition) {
        if (definition.startsWith("timestamp without time zone")) {
            type = "timestamp without time zone";

            if (definition.contentEquals("timestamp without time zone")) {
                definition = "";
            } else {
                definition =
                    definition.substring(
                            "timestamp without time zone".length()).trim();
            }
        } else if (definition.startsWith("character varying(")) {
            if (definition.matches("^character varying\\([0-9]*\\)$")) {
                type = definition;
                definition = "";
            } else {
                type =
                    definition.substring(
                            0,
                            definition.indexOf(
                                    " ",
                                    "character varying(".length())).trim();
                definition = definition.substring(type.length()).trim();
            }
        } else {
            if (definition.indexOf(" ") == -1) {
                type = definition;
                definition = "";
            } else {
                type = definition.substring(0, definition.indexOf(" ")).trim();
                definition = definition.substring(definition.indexOf(" ")).trim();
            }
        }

        if (definition.startsWith("DEFAULT ")) {
            definition = definition.substring("DEFAULT ".length()).trim();

            if (definition.indexOf(" ") == -1) {
                defaultValue = definition;
                definition = "";
            } else if (definition.matches(".*::character varying.*")) {
                defaultValue =
                    definition.substring(
                            0,
                            definition.indexOf("::character varying")
                            + "::character varying".length()).trim();
                definition = definition.substring(defaultValue.length()).trim();
            } else {
                defaultValue = definition.substring(0, definition.indexOf(" "))
                                         .trim();
                definition = definition.substring(defaultValue.length()).trim();
            }
        }

        if (definition.contentEquals("NULL")) {
            nullValue = true;
            definition = "";
        } else if (definition.startsWith("NULL ")) {
            nullValue = true;
            definition = definition.substring("NULL ".length()).trim();
        } else if (definition.contentEquals("NOT NULL")) {
            nullValue = false;
            definition = "";
        } else if (definition.startsWith("NOT NULL ")) {
            nullValue = false;
            definition = definition.substring("NOT NULL ".length()).trim();
        }

        if (definition.length() > 0) {
            constraint = definition;
        }
    }
}
