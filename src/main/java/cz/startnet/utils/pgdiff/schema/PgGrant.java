/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores table information.
 *
 * @author fordfrog
 */
public class PgGrant {

    /**
     * List of columns defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")

    private ArrayList<String> privileges = new ArrayList<>();
    private String role;
    private String object;

    
    /**
     * Creates a new PgTable object.
     *
     */
    public PgGrant() {

    }

    /**
     * Creates and returns SQL for creation of the table.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(1000);
        sbSQL.append("GRANT ");
        boolean first = true;
        for (String privilege : getPrivileges()) {
            if (first) {
                first = false;
            } else {
                sbSQL.append(",\n");
            }
            sbSQL.append("\t");
            sbSQL.append(privilege);
        }
        sbSQL.append(" ON ");
        sbSQL.append(getObject());
        sbSQL.append(" TO ");
        sbSQL.append(getRole());
        sbSQL.append(';');
        
        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL statement for dropping the table.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        final StringBuilder sbSQL = new StringBuilder(1000);
        sbSQL.append("REVOKE ");
        boolean first = true;
        for (String privilege : getPrivileges()) {
            if (first) {
                first = false;
            } else {
                sbSQL.append(",\n");
            }
            sbSQL.append("\t");
            sbSQL.append(privilege);
        }
        sbSQL.append(" ON ");
        sbSQL.append(getObject());
        sbSQL.append(" FROM ");
        sbSQL.append(getRole());
        sbSQL.append(';');
        
        return sbSQL.toString();
    }

    /**
     * @return the privileges
     */
    public ArrayList<String> getPrivileges() {
        return privileges;
    }

    /**
     * @param privileges the privileges to set
     */
    public void setPrivileges(ArrayList<String> privileges) {
        this.privileges = privileges;
    }
    
    /**
     * @param privilege the privilege to add
     */
    public void addPrivilege(String privilege) {
        this.privileges.add(privilege);
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the object
     */
    public String getObject() {
        return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(String object) {
        this.object = object;
    }

}
