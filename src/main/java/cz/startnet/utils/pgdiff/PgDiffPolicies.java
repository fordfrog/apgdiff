/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.*;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class PgDiffPolicies {

    public static void createPolicies(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final String newTableName = newTable.getName();
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTableName);
            }

            for (final PgPolicy policy : newTable.getPolicies()) {
                PgPolicy oldPolicy = oldTable != null?oldTable.getPolicy(policy.getName()):null;
                if(oldPolicy == null){
                    searchPathHelper.outputSearchPath(writer);
                    createPolicySQL(writer, policy);
                }
            }
        }
    }

    public static void alterPolicies(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final String newTableName = newTable.getName();

            if (oldSchema != null) {
                final PgTable oldTable = oldSchema.getTable(newTableName);
                if (oldTable != null){
                  for (final PgPolicy policy : oldTable.getPolicies()) {
                      PgPolicy newPolicy = newTable.getPolicy(policy.getName());
                      if(newPolicy != null){
                          // ALTER POLICY doesn't support changing command(ALL,
                          // SELECT..) so we drop it and create it
                          String newCommand = newPolicy.getCommand();
                          String oldCommand = policy.getCommand();
                          if(newCommand != null && oldCommand != null
                             && !newCommand.equals(oldCommand)){
                              searchPathHelper.outputSearchPath(writer);
                              dropPolicySQL(writer, newPolicy);
                              createPolicySQL(writer, newPolicy);
                          } else if (
                              (policy.getUsing() == null && newPolicy.getUsing() != null)
                            ||(policy.getUsing() != null && newPolicy.getUsing() == null)
                            ||(policy.getUsing() != null && newPolicy.getUsing() != null
                               && !policy.getUsing().equals(newPolicy.getUsing()))
                            ){
                              searchPathHelper.outputSearchPath(writer);
                              alterPolicySQL(writer, newPolicy);
                          } else if (
                              (policy.getWithCheck() == null && newPolicy.getWithCheck() != null)
                            ||(policy.getWithCheck() != null && newPolicy.getWithCheck() == null)
                            ||(policy.getWithCheck() != null && newPolicy.getWithCheck() != null
                               && !policy.getWithCheck().equals(newPolicy.getWithCheck()))
                            ){
                              searchPathHelper.outputSearchPath(writer);
                              alterPolicySQL(writer, newPolicy);
                          } else {
                              List<String> tempOldRoles = new ArrayList<String>(policy.getRoles());
                              boolean equalRoles =
                                  newPolicy.getRoles().containsAll(policy.getRoles()) &&
                                  policy.getRoles().containsAll(newPolicy.getRoles());
                              if(!equalRoles){
                                  searchPathHelper.outputSearchPath(writer);
                                  alterPolicySQL(writer, newPolicy);
                              }
                          }
                      }
                  }
                }
            }
        }
    }

    public static void dropPolicies(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final String newTableName = newTable.getName();

            if (oldSchema != null) {
                final PgTable oldTable = oldSchema.getTable(newTableName);
                if (oldTable != null){
                  for (final PgPolicy policy : oldTable.getPolicies()) {
                      if(newTable.getPolicy(policy.getName()) == null){
                        searchPathHelper.outputSearchPath(writer);
                        dropPolicySQL(writer, policy);
                      }
                  }
                }
            }
        }
    }

    private static void createPolicySQL(final PrintWriter writer, final PgPolicy policy){
        writer.print("CREATE POLICY "
                + PgDiffUtils.getQuotedName(policy.getName())
                + " ON "
                + PgDiffUtils.getQuotedName(policy.getTableName()));
        writer.print(" FOR " + policy.getCommand());
        String roles = "";
        writer.print(" TO ");
        for (Iterator<String> iterator = policy.getRoles().iterator(); iterator.hasNext();)
            roles += iterator.next() + (iterator.hasNext()? ", " : "");
        writer.print(roles);
        if (policy.getUsing() != null){
          writer.println();
          writer.println("USING (");
          writer.print("  ");
          writer.println(policy.getUsing());
          writer.print(")");
        }
        if (policy.getWithCheck() != null){
          writer.println();
          writer.println("WITH CHECK (");
          writer.print("  ");
          writer.println(policy.getWithCheck());
          writer.print(")");
        }
        writer.println(";");
    }

    private static void alterPolicySQL(final PrintWriter writer, final PgPolicy policy){
        writer.print("ALTER POLICY "
            + PgDiffUtils.getQuotedName(policy.getName())
            + " ON "
            + PgDiffUtils.getQuotedName(policy.getTableName()));
        String roles = "";
        writer.print(" TO ");
        for (Iterator<String> iterator = policy.getRoles().iterator(); iterator.hasNext();)
            roles += iterator.next() + (iterator.hasNext()? ", " : "");
        writer.print(roles);
        if (policy.getUsing() != null){
          writer.println();
          writer.println("USING (");
          writer.print("  ");
          writer.println(policy.getUsing());
          writer.print(")");
        }
        if (policy.getWithCheck() != null){
          writer.println();
          writer.println("WITH CHECK (");
          writer.print("  ");
          writer.println(policy.getWithCheck());
          writer.print(")");
        }
        writer.println(";");
    }

    private static void dropPolicySQL(final PrintWriter writer, final PgPolicy policy){
        writer.println("DROP POLICY "
            + PgDiffUtils.getQuotedName(policy.getName())
            + " ON "
            + PgDiffUtils.getQuotedName(policy.getTableName())
            + ";");
    }

    private PgDiffPolicies() {
    }
}
