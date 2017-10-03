/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgColumnUtils;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgType;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diffs types.
 *
 * @author fordfrog
 */
public class PgDiffTypes {

    /**
     * Outputs statements for altering types.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterTypes(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        for (final PgType newType : newSchema.getTypes()) {
            if (oldSchema == null
                    || !oldSchema.containsType(newType.getName())) {
                continue;
            }

            final PgType oldType = oldSchema.getType(newType.getName());
            updateTypeColumns(
                    writer, arguments, oldType, newType, searchPathHelper);

        }
    }


    /**
     * Adds statements for creation of new columns to the list of statements.
     *
     * @param statements          list of statements
     * @param arguments           object containing arguments settings
     * @param oldType            original type
     * @param newType            new type
     * @param dropDefaultsColumns list for storing columns for which default
     *                            value should be dropped
     */
    private static void addCreateTypeColumns(final List<String> statements,
            final PgDiffArguments arguments, final PgType oldType,
            final PgType newType, final List<PgColumn> dropDefaultsColumns) {
        for (final PgColumn column : newType.getColumns()) {
            if (!oldType.containsColumn(column.getName())) {
                statements.add("\tADD ATTRIBUTE "
                        + column.getFullDefinition(arguments.isAddDefaults()));

                if (arguments.isAddDefaults() && !column.getNullValue()
                        && (column.getDefaultValue() == null
                        || column.getDefaultValue().isEmpty())) {
                    dropDefaultsColumns.add(column);
                }
            }
        }
    }

    /**
     * Adds statements for removal of columns to the list of statements.
     *
     * @param statements list of statements
     * @param oldType   original type
     * @param newType   new type
     */
    private static void addDropTypeColumns(final List<String> statements,
            final PgType oldType, final PgType newType) {
        for (final PgColumn column : oldType.getColumns()) {
            if (!newType.containsColumn(column.getName())) {
                statements.add("\tDROP ATTRIBUTE "
                        + PgDiffUtils.getQuotedName(column.getName()));
            }
        }
    }

    /**
     * Adds statements for modification of columns to the list of statements.
     *
     * @param statements          list of statements
     * @param arguments           object containing arguments settings
     * @param oldType            original type
     * @param newType            new type
     * @param dropDefaultsColumns list for storing columns for which default
     *                            value should be dropped
     */
    private static void addModifyTypeColumns(final List<String> statements,
            final PgDiffArguments arguments, final PgType oldType,
            final PgType newType, final List<PgColumn> dropDefaultsColumns) {
        for (final PgColumn newColumn : newType.getColumns()) {
            if (!oldType.containsColumn(newColumn.getName())) {
                continue;
            }

            final PgColumn oldColumn = oldType.getColumn(newColumn.getName());
            final String newColumnName =
                    PgDiffUtils.getQuotedName(newColumn.getName());

            if (!oldColumn.getType().equals(newColumn.getType())) {
                statements.add("\tALTER ATTRIBUTE " + newColumnName + " TYPE "
                        + newColumn.getType() + " /* "
                        + MessageFormat.format(
                        Resources.getString("TypeParameterChange"),
                        newType.getName(), oldColumn.getType(),
                        newColumn.getType()) + " */");
            }

            final String oldDefault = (oldColumn.getDefaultValue() == null) ? ""
                    : oldColumn.getDefaultValue();
            final String newDefault = (newColumn.getDefaultValue() == null) ? ""
                    : newColumn.getDefaultValue();

            if (!oldDefault.equals(newDefault)) {
                if (newDefault.length() == 0) {
                    statements.add("\tALTER ATTRIBUTE " + newColumnName
                            + " DROP DEFAULT");
                } else {
                    statements.add("\tALTER ATTRIBUTE " + newColumnName
                            + " SET DEFAULT " + newDefault);
                }
            }

            if (oldColumn.getNullValue() != newColumn.getNullValue()) {
                if (newColumn.getNullValue()) {
                    statements.add("\tALTER ATTRIBUTE " + newColumnName
                            + " DROP NOT NULL");
                } else {
                    if (arguments.isAddDefaults()) {
                        final String defaultValue =
                                PgColumnUtils.getDefaultValue(
                                newColumn.getType());

                        if (defaultValue != null) {
                            statements.add("\tALTER ATTRIBUTE " + newColumnName
                                    + " SET DEFAULT " + defaultValue);
                            dropDefaultsColumns.add(newColumn);
                        }
                    }

                    statements.add("\tALTER ATTRIBUTE " + newColumnName
                            + " SET NOT NULL");
                }
            }
        }
    }

    /**
     * Outputs statements for creation of new types.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createTypes(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper
            ) {
        for (final PgType type : newSchema.getTypes()) {
            if (oldSchema == null
                    || !oldSchema.containsType(type.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(type.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping types.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropTypes(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper
            ) {
        if (oldSchema == null) {
            return;
        }

        for (final PgType type : oldSchema.getTypes()) {
            if (!newSchema.containsType(type.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(type.getDropSQL());
            }
        }
    }

    /**
     * Outputs statements for addition, removal and modifications of type
     * columns.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldType         original type
     * @param newType         new type
     * @param searchPathHelper search path helper
     */
    private static void updateTypeColumns(final PrintWriter writer,
            final PgDiffArguments arguments, final PgType oldType,
            final PgType newType, final SearchPathHelper searchPathHelper) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<String> statements = new ArrayList<String>();
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgColumn> dropDefaultsColumns = new ArrayList<PgColumn>();
        addDropTypeColumns(statements, oldType, newType);
        addCreateTypeColumns(
                statements, arguments, oldType, newType, dropDefaultsColumns);
        addModifyTypeColumns(
                statements, arguments, oldType, newType, dropDefaultsColumns);

        if (!statements.isEmpty()) {
            final String quotedTypeName =
                    PgDiffUtils.getQuotedName(newType.getName());
            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.println("ALTER TYPE " + quotedTypeName);

            for (int i = 0; i < statements.size(); i++) {
                writer.print(statements.get(i));
                writer.println((i + 1) < statements.size() ? "," : ";");
            }

            if (!dropDefaultsColumns.isEmpty()) {
                writer.println();
                writer.println("ALTER TYPE " + quotedTypeName);

                for (int i = 0; i < dropDefaultsColumns.size(); i++) {
                    writer.print("\tALTER ATTRIBUTE ");
                    writer.print(PgDiffUtils.getQuotedName(
                            dropDefaultsColumns.get(i).getName()));
                    writer.print(" DROP DEFAULT");
                    writer.println(
                            (i + 1) < dropDefaultsColumns.size() ? "," : ";");
                }
            }
        }
    }



    /**
     * Creates a new instance of PgDiffTypes.
     */
    private PgDiffTypes() {
    }
}
