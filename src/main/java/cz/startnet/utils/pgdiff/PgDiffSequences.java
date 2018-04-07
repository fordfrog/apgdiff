/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;
import cz.startnet.utils.pgdiff.schema.PgSequencePrivilege;

import java.io.PrintWriter;

/**
 * Diffs sequences.
 *
 * @author fordfrog
 */
public class PgDiffSequences {

    /**
     * Outputs statements for creation of new sequences.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createSequences(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        // Add new sequences
        for (final PgSequence sequence : newSchema.getSequences()) {
            if (oldSchema == null
                    || !oldSchema.containsSequence(sequence.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(sequence.getCreationSQL());

                for (PgSequencePrivilege sequencePrivilege : sequence
                        .getPrivileges()) {
                    writer.println("REVOKE ALL ON TABLE "
                            + PgDiffUtils.getQuotedName(sequence.getName())
                            + " FROM " + sequencePrivilege.getRoleName() + ";");
                    if (!"".equals(sequencePrivilege.getPrivilegesSQL(true))) {
                        writer.println("GRANT "
                                + sequencePrivilege.getPrivilegesSQL(true)
                                + " ON TABLE "
                                + PgDiffUtils.getQuotedName(sequence.getName())
                                + " TO " + sequencePrivilege.getRoleName()
                                + " WITH GRANT OPTION;");
                    }
                    if (!"".equals(sequencePrivilege.getPrivilegesSQL(false))) {
                        writer.println("GRANT "
                                + sequencePrivilege.getPrivilegesSQL(false)
                                + " ON TABLE "
                                + PgDiffUtils.getQuotedName(sequence.getName())
                                + " TO " + sequencePrivilege.getRoleName()
                                + ";");
                    }
                }

            }
        }
    }

    /**
     * Outputs statements for altering of new sequences.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterCreatedSequences(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        // Alter created sequences
        for (final PgSequence sequence : newSchema.getSequences()) {
            if ((oldSchema == null
                    || !oldSchema.containsSequence(sequence.getName()))
                    && sequence.getOwnedBy() != null
                    && !sequence.getOwnedBy().isEmpty()) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(sequence.getOwnedBySQL());
            }
        }
    }

    /**
     * Outputs statements for dropping of sequences that do not exist anymore.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropSequences(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        // Drop sequences that do not exist in new schema
        for (final PgSequence sequence : oldSchema.getSequences()) {
            if (!newSchema.containsSequence(sequence.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(sequence.getDropSQL());
            }
        }
    }

    /**
     * Outputs statement for modified sequences.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterSequences(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        final StringBuilder sbSQL = new StringBuilder(100);

        for (final PgSequence newSequence : newSchema.getSequences()) {
            final PgSequence oldSequence =
                    oldSchema.getSequence(newSequence.getName());

            if (oldSequence == null) {
                continue;
            }

            sbSQL.setLength(0);

            final String oldIncrement = oldSequence.getIncrement();
            final String newIncrement = newSequence.getIncrement();

            if (newIncrement != null
                    && !newIncrement.equals(oldIncrement)) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tINCREMENT BY ");
                sbSQL.append(newIncrement);
            }

            final String oldMinValue = oldSequence.getMinValue();
            final String newMinValue = newSequence.getMinValue();

            if (newMinValue == null && oldMinValue != null) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tNO MINVALUE");
            } else if (newMinValue != null
                    && !newMinValue.equals(oldMinValue)) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tMINVALUE ");
                sbSQL.append(newMinValue);
            }

            final String oldMaxValue = oldSequence.getMaxValue();
            final String newMaxValue = newSequence.getMaxValue();

            if (newMaxValue == null && oldMaxValue != null) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tNO MAXVALUE");
            } else if (newMaxValue != null
                    && !newMaxValue.equals(oldMaxValue)) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tMAXVALUE ");
                sbSQL.append(newMaxValue);
            }

            if (!arguments.isIgnoreStartWith()) {
                final String oldStart = oldSequence.getStartWith();
                final String newStart = newSequence.getStartWith();

                if (newStart != null && !newStart.equals(oldStart)) {
                    sbSQL.append(System.getProperty("line.separator"));
                    sbSQL.append("\tRESTART WITH ");
                    sbSQL.append(newStart);
                }
            }

            final String oldCache = oldSequence.getCache();
            final String newCache = newSequence.getCache();

            if (newCache != null && !newCache.equals(oldCache)) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tCACHE ");
                sbSQL.append(newCache);
            }

            final boolean oldCycle = oldSequence.isCycle();
            final boolean newCycle = newSequence.isCycle();

            if (oldCycle && !newCycle) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tNO CYCLE");
            } else if (!oldCycle && newCycle) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tCYCLE");
            }

            final String oldOwnedBy = oldSequence.getOwnedBy();
            final String newOwnedBy = newSequence.getOwnedBy();

            if (newOwnedBy != null && !newOwnedBy.equals(oldOwnedBy)) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tOWNED BY ");
                sbSQL.append(newOwnedBy);
            }

            if (sbSQL.length() > 0) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("ALTER SEQUENCE "
                        + PgDiffUtils.getQuotedName(newSequence.getName()));
                writer.print(sbSQL.toString());
                writer.println(';');
            }

            if (oldSequence.getComment() == null
                    && newSequence.getComment() != null
                    || oldSequence.getComment() != null
                    && newSequence.getComment() != null
                    && !oldSequence.getComment().equals(
                    newSequence.getComment())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON SEQUENCE ");
                writer.print(PgDiffUtils.getQuotedName(newSequence.getName()));
                writer.print(" IS ");
                writer.print(newSequence.getComment());
                writer.println(';');
            } else if (oldSequence.getComment() != null
                    && newSequence.getComment() == null) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON SEQUENCE ");
                writer.print(newSequence.getName());
                writer.println(" IS NULL;");
            }

            alterPrivileges(writer, oldSequence, newSequence, searchPathHelper);
        }
    }

    private static void alterPrivileges(final PrintWriter writer,
            final PgSequence oldSequence, final PgSequence newSequence,
            final SearchPathHelper searchPathHelper) {
        boolean emptyLinePrinted = false;
        for (PgSequencePrivilege oldSequencePrivilege : oldSequence
                .getPrivileges()) {
            PgSequencePrivilege newSequencePrivilege = newSequence
                    .getPrivilege(oldSequencePrivilege.getRoleName());
            if (newSequencePrivilege == null) {
                if (!emptyLinePrinted) {
                    writer.println();
                }
                writer.println("REVOKE ALL ON SEQUENCE "
                        + PgDiffUtils.getQuotedName(oldSequence.getName())
                        + " FROM " + oldSequencePrivilege.getRoleName() + ";");
            } else if (!oldSequencePrivilege.isSimilar(newSequencePrivilege)) {
                if (!emptyLinePrinted) {
                    writer.println();
                }
                writer.println("REVOKE ALL ON SEQUENCE "
                        + PgDiffUtils.getQuotedName(newSequence.getName())
                        + " FROM " + newSequencePrivilege.getRoleName() + ";");
                if (!"".equals(newSequencePrivilege.getPrivilegesSQL(true))) {
                    writer.println("GRANT "
                            + newSequencePrivilege.getPrivilegesSQL(true)
                            + " ON SEQUENCE "
                            + PgDiffUtils.getQuotedName(newSequence.getName())
                            + " TO " + newSequencePrivilege.getRoleName()
                            + " WITH GRANT OPTION;");
                }
                if (!"".equals(newSequencePrivilege.getPrivilegesSQL(false))) {
                    writer.println("GRANT "
                            + newSequencePrivilege.getPrivilegesSQL(false)
                            + " ON SEQUENCE "
                            + PgDiffUtils.getQuotedName(newSequence.getName())
                            + " TO " + newSequencePrivilege.getRoleName() + ";");
                }
            } // else similar privilege will not be updated
        }
        for (PgSequencePrivilege newSequencePrivilege : newSequence
                .getPrivileges()) {
            PgSequencePrivilege oldSequencePrivilege = oldSequence
                    .getPrivilege(newSequencePrivilege.getRoleName());
            if (oldSequencePrivilege == null) {
                if (!emptyLinePrinted) {
                    writer.println();
                }
                writer.println("REVOKE ALL ON SEQUENCE "
                        + PgDiffUtils.getQuotedName(newSequence.getName())
                        + " FROM " + newSequencePrivilege.getRoleName() + ";");
                if (!"".equals(newSequencePrivilege.getPrivilegesSQL(true))) {
                    writer.println("GRANT "
                            + newSequencePrivilege.getPrivilegesSQL(true)
                            + " ON SEQUENCE "
                            + PgDiffUtils.getQuotedName(newSequence.getName())
                            + " TO " + newSequencePrivilege.getRoleName()
                            + " WITH GRANT OPTION;");
                }
                if (!"".equals(newSequencePrivilege.getPrivilegesSQL(false))) {
                    writer.println("GRANT "
                            + newSequencePrivilege.getPrivilegesSQL(false)
                            + " ON SEQUENCE "
                            + PgDiffUtils.getQuotedName(newSequence.getName())
                            + " TO " + newSequencePrivilege.getRoleName() + ";");
                }
            }
        }
    }

    /**
     * Creates a new instance of PgDiffSequences.
     */
    private PgDiffSequences() {
    }
}
