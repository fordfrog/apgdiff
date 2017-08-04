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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Objects;

/**
 *
 * Stores POLICY information.
 *
 */
public class PgPolicy {

    private String name;
    private String tableName;
    private String command;
    private List<String> roles = new ArrayList<String>();
    private String using;
    private String withCheck;

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(final String role) {
        roles.add(role);
    }

    public void setUsing(final String using) {
        this.using = using;
    }

    public String getUsing() {
        return using;
    }

    public void setWithCheck(final String withCheck) {
        this.withCheck = withCheck;
    }

    public String getWithCheck() {
        return withCheck;
    }
}
