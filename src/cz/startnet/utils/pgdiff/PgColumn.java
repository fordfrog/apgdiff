/*
 * PgColumn.java
 *
 * Created on 23. bøezen 2006, 9:58
 */
package cz.startnet.utils.pgdiff;


/**
 * Stores column information.
 * @author fordfrog
 */
public class PgColumn {
    private String constraint = null;
    private String defaultValue = null;
    private String name = null;
    private String type = null;
    private boolean nullValue = true;

    /**
     * Creates a new instance of PgColumn.
     */
    public PgColumn() {
    }

    public PgColumn(String name) {
        this.name = name;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    public boolean getNullValue() {
        return nullValue;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

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
