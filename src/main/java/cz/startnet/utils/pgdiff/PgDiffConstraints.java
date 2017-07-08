/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgFkConstraint;
import cz.startnet.utils.pgdiff.schema.PgPkConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

/**
 * Diffs constraints.
 *
 * @author fordfrog
 */
public class PgDiffConstraints
{
	public static int OTHER_CONSTRAINTS = 0;
	public static int PRIMARY_KEYS = 1;
	public static int FOREIGN_KEYS = 2;

    /**
     * Outputs statements for creation of new constraints.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param primaryKey       determines whether primary keys should be
     *                         processed or any other constraints should be
     *                         processed
     * @param searchPathHelper search path helper
     */
    public static void createConstraints(final PrintWriter writer,
    		final PgDiffArguments arguments,
            final PgSchema oldSchema, final PgSchema newSchema,
            final int constraintType, final SearchPathHelper searchPathHelper)
    {
        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            boolean first=true;
            // Add new constraints
            for (final PgConstraint constraint :
                    getNewConstraints(arguments, oldTable, newTable, constraintType)) {
                searchPathHelper.outputSearchPath(writer);
                if (arguments.isGroupAlterTables()) {
                	if (first) {
                        writer.println();
                		writer.print(constraint.getCreationSQL(PgConstraint.Mode.StandAlone));
                		first = false;
                	} else {
                		writer.println(",");
                		writer.print(constraint.getCreationSQL(PgConstraint.Mode.GroupElement));
                	}
                } else {
                    writer.println();
                	writer.println(constraint.getCreationSQL());
                	printComment(writer,constraint);
                }
            }
            if (!first) {
            	writer.println(";");
                for (final PgConstraint constraint :
                    getNewConstraints(arguments, oldTable, newTable, constraintType)) {
                	printComment(writer,constraint);
                }
            }
        }
    }

    public static void printComment(final PrintWriter writer, final PgConstraint constraint) {
    	String comment = constraint.getCommentSQL();
    	if (!comment.isEmpty())
    		writer.println(comment);
    }

    public static Map<PgTable,PgPkConstraint> getPKConstraints(
    		final PgDiffArguments arguments,
            final PgSchema oldSchema,
            final PgSchema newSchema)
    {
    	Map<PgTable,PgPkConstraint> constraints = new HashMap<PgTable,PgPkConstraint>();

        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            List<PgConstraint> newConstraints = getNewConstraints(arguments, oldTable, newTable, PRIMARY_KEYS);
            if (!newConstraints.isEmpty()) {
            	if (newConstraints.size()>1)
            		throw new RuntimeException("Too many PRIMARY KEY constaints for new table "+newTable.getName());
                constraints.put(newTable,(PgPkConstraint)newConstraints.get(0));
            }
        }

    	return constraints;
    }

    public static Map<PgTable,Map<Set<String>,PgFkConstraint>> getFKConstraints(
    		final PgDiffArguments arguments,
            final PgSchema oldSchema,
            final PgSchema newSchema)
    {
    	Map<PgTable,Map<Set<String>,PgFkConstraint>> constraints = new HashMap<PgTable, Map<Set<String>,PgFkConstraint>>();

        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            List<PgConstraint> newConstraints = getNewConstraints(arguments, oldTable, newTable, FOREIGN_KEYS);
            if (!newConstraints.isEmpty()) {
            	Map<Set<String>,PgFkConstraint> foreignKeys = new HashMap<Set<String>, PgFkConstraint>();
            	for(final PgConstraint constraint: newConstraints) {
            		final PgFkConstraint fk = (PgFkConstraint)constraint;
            		foreignKeys.put(fk.getColumnNames(), fk);
            	}
                constraints.put(newTable,foreignKeys);
            }
        }

    	return constraints;
    }

    /**
     * Outputs statements for dropping non-existent or modified constraints.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param primaryKey       determines whether primary keys should be
     *                         processed or any other constraints should be
     *                         processed
     * @param searchPathHelper search path helper
     */
    public static void dropConstraints(final PrintWriter writer,
    		final PgDiffArguments arguments,
            final PgSchema oldSchema, final PgSchema newSchema,
            final boolean primaryKey, final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            // Drop constraints that no more exist or are modified
            for (final PgConstraint constraint :
                    getDropConstraints(arguments, oldTable, newTable, primaryKey)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(constraint.getDropSQL());
            }
        }
    }

    /**
     * Returns list of constraints that should be dropped.
     *
     * @param oldTable   original table or null
     * @param newTable   new table or null
     * @param primaryKey determines whether primary keys should be processed or
     *                   any other constraints should be processed
     *
     * @return list of constraints that should be dropped
     *
     * @todo Constraints that are depending on a removed field should not be
     * added to drop because they are already removed.
     */
    private static List<PgConstraint> getDropConstraints(
    		final PgDiffArguments arguments,
    		final PgTable oldTable,
            final PgTable newTable, final boolean primaryKey) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgConstraint> list = new ArrayList<PgConstraint>();

        if (newTable != null && oldTable != null) {
            for (final PgConstraint constraint : oldTable.getConstraints()) {
                if (constraint.isPrimaryKeyConstraint() == primaryKey) {
                	if (arguments.isIgnoreConstraintNames()) {
                		PgConstraint newConstraint = newTable.findConstraint(constraint.getDefinition());
                		if (newConstraint==null) {
                        	list.add(constraint);                			
                		}
                	} else { 
                        if ( !newTable.containsConstraint(constraint.getName())
                             || !newTable.getConstraint(constraint.getName()).equals(constraint) ) {
                        	list.add(constraint);
                        }
                	}
                }
            }
        }

        return list;
    }

    /**
     * Returns list of constraints that should be added.
     *
     * @param oldTable   original table
     * @param newTable   new table
     * @param constraintType determines whether primary keys, foreign keys or
     *                   any other constraints should be processed
     *
     * @return list of constraints that should be added
     */
    private static List<PgConstraint> getNewConstraints(
    		final PgDiffArguments arguments,
    		final PgTable oldTable,
            final PgTable newTable,
            final int constraintType)
    {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgConstraint> list = new ArrayList<PgConstraint>();

        if (newTable != null) {
            if (oldTable == null) {
                for (final PgConstraint constraint :
                        newTable.getConstraints()) {
                	boolean isPK = constraint instanceof PgPkConstraint;
                	boolean isFK = constraint instanceof PgFkConstraint;
                    if ( (constraintType == PRIMARY_KEYS && isPK)
                    	|| (constraintType == FOREIGN_KEYS && isFK)
                    	|| (constraintType == OTHER_CONSTRAINTS && !(isPK || isFK)) ) {
                        list.add(constraint);
                    }
                }
            } else {
                for (final PgConstraint constraint : newTable.getConstraints()) {
                	boolean isPK = constraint instanceof PgPkConstraint;
                	boolean isFK = constraint instanceof PgFkConstraint;
                    if ((constraintType == PRIMARY_KEYS && isPK)
                        	|| (constraintType == FOREIGN_KEYS && isFK)
                        	|| (constraintType == OTHER_CONSTRAINTS && !(isPK || isFK)))
                    {
                    	if (arguments.isIgnoreConstraintNames()) {
	                    	PgConstraint oldConstraint = oldTable.findConstraint(constraint.getDefinition()); 
	                    	if (oldConstraint == null) {
	                    		list.add(constraint);
	                    	}
	                    } else {
	                    	if ( !oldTable.containsConstraint(constraint.getName())
	                             || !oldTable.getConstraint(constraint.getName()).equals(constraint) ) {
	                    		list.add(constraint);
	                    	}
	                    }
                	}
                }
            }
        }

        return list;
    }

    /**
     * Outputs statements for constraint comments that have changed.
     *
     * @param writer           writer
     * @param oldSchema        old schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterComments(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (PgTable oldTable : oldSchema.getTables()) {
            final PgTable newTable = newSchema.getTable(oldTable.getName());

            if (newTable == null) {
                continue;
            }

            for (final PgConstraint oldConstraint : oldTable.getConstraints()) {
                final PgConstraint newConstraint =
                        newTable.getConstraint(oldConstraint.getName());

                if (newConstraint == null) {
                    continue;
                }

                if (oldConstraint.getComment() == null
                        && newConstraint.getComment() != null
                        || oldConstraint.getComment() != null
                        && newConstraint.getComment() != null
                        && !oldConstraint.getComment().equals(
                        newConstraint.getComment())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON ");

                    if (newConstraint.isPrimaryKeyConstraint()) {
                        writer.print("INDEX ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                    } else {
                        writer.print("CONSTRAINT ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                        writer.print(" ON ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getTableName()));
                    }

                    writer.print(" IS ");
                    writer.print(newConstraint.getComment());
                    writer.println(';');
                } else if (oldConstraint.getComment() != null
                        && newConstraint.getComment() == null) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON ");

                    if (newConstraint.isPrimaryKeyConstraint()) {
                        writer.print("INDEX ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                    } else {
                        writer.print("CONSTRAINT ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                        writer.print(" ON ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getTableName()));
                    }

                    writer.println(" IS NULL;");
                }
            }
        }
    }

    /**
     * Creates a new instance of PgDiffConstraints.
     */
    private PgDiffConstraints() {
    }
}
