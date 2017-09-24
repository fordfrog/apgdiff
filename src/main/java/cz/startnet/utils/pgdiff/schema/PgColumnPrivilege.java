/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores tablePrivileges information.
 * 
 * @author user
 */
public class PgColumnPrivilege {

    private final String roleName;

    private boolean select = false;
    private boolean insert = false;
    private boolean update = false;
    private boolean references = false;

    private boolean selectWithGrantOption = false;
    private boolean insertWithGrantOption = false;
    private boolean updateWithGrantOption = false;
    private boolean referencesWithGrantOption = false;

    /**
     * Creates a new PgTablePrivilege object.
     * 
     * @param roleName
     *            name of the role
     */
    public PgColumnPrivilege(final String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    public void setPrivileges(final String privilege, final boolean value,
            final boolean grantOption) {
        if ("SELECT".equalsIgnoreCase(privilege)
                || "ALL".equalsIgnoreCase(privilege)) {
            if (value) {
                select = true;
                if (grantOption) {
                    selectWithGrantOption = true;
                }
            } else {
                selectWithGrantOption = false;
                if (!grantOption) {
                    select = false;
                }
            }
        }
        if ("INSERT".equalsIgnoreCase(privilege)
                || "ALL".equalsIgnoreCase(privilege)) {
            if (value) {
                insert = true;
                if (grantOption) {
                    insertWithGrantOption = true;
                }
            } else {
                insertWithGrantOption = false;
                if (!grantOption) {
                    insert = false;
                }
            }
        }
        if ("UPDATE".equalsIgnoreCase(privilege)
                || "ALL".equalsIgnoreCase(privilege)) {
            if (value) {
                update = true;
                if (grantOption) {
                    updateWithGrantOption = true;
                }
            } else {
                updateWithGrantOption = false;
                if (!grantOption) {
                    update = false;
                }
            }
        }
        if ("REFERENCES".equalsIgnoreCase(privilege)
                || "ALL".equalsIgnoreCase(privilege)) {
            if (value) {
                references = true;
                if (grantOption) {
                    referencesWithGrantOption = true;
                }
            } else {
                referencesWithGrantOption = false;
                if (!grantOption) {
                    references = false;
                }
            }
        }
    }

    /**
     * true the privileges are the same (no matter of roleName).
     * 
     * @param other
     *            privileges to compare
     * @return isSimilar
     */
    public boolean isSimilar(final PgColumnPrivilege other) {
        if (other == null) {
            return false;
        }
        if (select != other.select) {
            return false;
        }
        if (selectWithGrantOption != other.selectWithGrantOption) {
            return false;
        }
        if (insert != other.insert) {
            return false;
        }
        if (insertWithGrantOption != other.insertWithGrantOption) {
            return false;
        }
        if (update != other.update) {
            return false;
        }
        if (updateWithGrantOption != other.updateWithGrantOption) {
            return false;
        }
        if (references != other.references) {
            return false;
        }
        if (referencesWithGrantOption != other.referencesWithGrantOption) {
            return false;
        }
        return true;
    }

    public String getPrivilegesSQL(final boolean withGrantOption,
            String columnName) {
        if (withGrantOption) {
            if (selectWithGrantOption && insertWithGrantOption
                    && updateWithGrantOption && referencesWithGrantOption) {
                return "ALL (" + columnName + ")";
            }
            String result = "";
            if (selectWithGrantOption) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "SELECT (" + columnName + ")";
            }
            if (insertWithGrantOption) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "INSERT (" + columnName + ")";
            }
            if (updateWithGrantOption) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "UPDATE (" + columnName + ")";
            }
            if (referencesWithGrantOption) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "REFERENCES (" + columnName + ")";
            }
            return result;
        } else {
            if (select && insert && update && references) {
                return "ALL (" + columnName + ")";
            }
            String result = "";
            if (select) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "SELECT (" + columnName + ")";
            }
            if (insert) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "INSERT (" + columnName + ")";
            }
            if (update) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "UPDATE (" + columnName + ")";
            }
            if (references) {
                if (!"".equals(result)) {
                    result += ", ";
                }
                result += "REFERENCES (" + columnName + ")";
            }
            return result;
        }
    }

}
