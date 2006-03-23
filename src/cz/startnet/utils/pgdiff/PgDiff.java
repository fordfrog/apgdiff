/*
 * PgDiff.java
 *
 * Created on 22. bøezen 2006, 20:59
 */
package cz.startnet.utils.pgdiff;

import java.util.Map;
import java.util.Set;


/**
 * Creates diff of two schemas.
 * @author fordfrog
 */
public class PgDiff {
    /**
     * Creates a new instance of PgDiff.
     */
    private PgDiff() {
    }

    /**
     * Creates diff on the two schemas.
     * @param file1 name of file containing dump of the original schema
     * @param file2 name of file containing dump of the new schema
     */
    public static void createDiff(String file1, String file2) {
        PgSchema schema1 = PgDumpLoader.loadSchema(file1);
        PgSchema schema2 = PgDumpLoader.loadSchema(file2);
        diffSchemas(schema1, schema2);
    }

    /**
     * Creates diff of table constraints.
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffConstraints(PgSchema schema1, PgSchema schema2) {
        Map<String, PgTable> tables1 = schema1.getTables();
        Map<String, PgTable> tables2 = schema2.getTables();
        Set<String> tableNames1 = tables1.keySet();
        Set<String> tableNames2 = tables2.keySet();

        // For each table that exists in the new schema compare the constraints
        for (String tableName : tableNames2) {
            Map<String, PgConstraint> constraints1 = null;
            Set<String> constraintNames1 = null;

            if (tableNames1.contains(tableName)) {
                constraints1 = tables1.get(tableName).getConstraints();
                constraintNames1 = constraints1.keySet();
            }

            Map<String, PgConstraint> constraints2 =
                tables2.get(tableName).getConstraints();
            Set<String> constraintNames2 = constraints2.keySet();

            // Check what constraints should be dropped
            if (tableNames1.contains(tableName)) {
                for (String constraintName : constraintNames1) {
                    if (!constraintNames2.contains(constraintName)) {
                        System.out.println("\nALTER TABLE " + tableName);
                        System.out.println(
                                "\tDROP CONSTRAINT " + constraintName + ";");
                    }
                }
            }

            // Check what constraints should be created
            for (String constraintName : constraintNames2) {
                if (
                    (constraintNames1 == null)
                        || !constraintNames1.contains(constraintName)) {
                    System.out.println("\nALTER TABLE " + tableName);
                    System.out.println(
                            "\tCREATE CONSTRAINT " + constraintName + " "
                            + constraints2.get(constraintName).getDefinition()
                            + ";");
                }
            }

            // Check what constraints are modified
            if (constraintNames1 != null) {
                for (String constraintName : constraintNames2) {
                    if (
                        constraintNames1.contains(constraintName)
                            && !constraints1.get(constraintName).getDefinition()
                                                .contentEquals(
                                    constraints2.get(constraintName)
                                                    .getDefinition())) {
                        System.out.println(
                                "\nMODIFIED CONSTRAINT " + constraintName
                                + " ON TABLE " + tableName);
                        System.out.println(
                                "ORIGINAL: "
                                + constraints1.get(constraintName)
                                              .getDefinition());
                        System.out.println(
                                "NEW: "
                                + constraints2.get(constraintName)
                                              .getDefinition());
                    }
                }
            }
        }
    }

    /**
     * Creates diff of table indexes.
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffIndexes(PgSchema schema1, PgSchema schema2) {
        Map<String, PgTable> tables1 = schema1.getTables();
        Map<String, PgTable> tables2 = schema2.getTables();
        Set<String> tableNames1 = tables1.keySet();
        Set<String> tableNames2 = tables2.keySet();

        // For each table that exists in the new schema compare the indexes
        for (String tableName : tableNames2) {
            Map<String, PgIndex> indexes1 = null;
            Set<String> indexNames1 = null;

            if (tableNames1.contains(tableName)) {
                indexes1 = tables1.get(tableName).getIndexes();
                indexNames1 = indexes1.keySet();
            }

            Map<String, PgIndex> indexes2 = tables2.get(tableName).getIndexes();
            Set<String> indexNames2 = indexes2.keySet();

            // Check what indexes should be dropped
            if (tableNames1.contains(tableName)) {
                for (String indexName : indexNames1) {
                    if (!indexNames2.contains(indexName)) {
                        System.out.println("DROP INDEX " + indexName + ";");
                    }
                }
            }

            // Check what indexes should be created
            for (String indexName : indexNames2) {
                if ((indexNames1 == null) || !indexNames1.contains(indexName)) {
                    PgIndex index = indexes2.get(indexName);
                    System.out.println(
                            "\nCREATE INDEX " + index.getName() + " ON "
                            + tableName + " " + index.getDefinition() + ";");
                }
            }

            // Check what indexes are modified
            if (indexNames1 != null) {
                for (String indexName : indexNames2) {
                    if (
                        indexNames1.contains(indexName)
                            && !indexes1.get(indexName).getDefinition().contentEquals(
                                    indexes2.get(indexName).getDefinition())) {
                        System.out.println(
                                "\nMODIFIED INDEX " + indexName + " ON TABLE "
                                + tableName);
                        System.out.println(
                                "ORIGINAL: "
                                + indexes1.get(indexName).getDefinition());
                        System.out.println(
                                "NEW: "
                                + indexes2.get(indexName).getDefinition());
                    }
                }
            }
        }
    }

    /**
     * Creates diff from comparison of two schemas.
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffSchemas(PgSchema schema1, PgSchema schema2) {
        diffTables(schema1, schema2);
        diffSequences(schema1, schema2);
        diffConstraints(schema1, schema2);
        diffIndexes(schema1, schema2);
    }

    /**
     * Creates diff of table sequences.
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffSequences(PgSchema schema1, PgSchema schema2) {
        // Check what sequences should be dropped
        Map<String, PgSequence> sequences1 = schema1.getSequences();
        Map<String, PgSequence> sequences2 = schema2.getSequences();
        Set<String> sequenceNames1 = sequences1.keySet();
        Set<String> sequenceNames2 = sequences2.keySet();

        // Check what sequences should be dropped
        for (String sequenceName : sequenceNames1) {
            if (!sequenceNames2.contains(sequenceName)) {
                System.out.println("\nDROP SEQUENCE " + sequenceName + ";");
            }
        }

        // Check what sequences should be created
        for (String sequenceName : sequenceNames2) {
            if (!sequenceNames1.contains(sequenceName)) {
                System.out.println("\nCREATE SEQUENCE " + sequenceName);
                System.out.println(
                        sequences2.get(sequenceName).getDefinition() + ";");
            }
        }

        // Check what sequences are modified
        for (String sequenceName : sequenceNames1) {
            if (
                sequenceNames2.contains(sequenceName)
                    && !sequences1.get(sequenceName).getDefinition().contentEquals(
                            sequences2.get(sequenceName).getDefinition())) {
                System.out.println("\nMODIFIED SEQUENCE " + sequenceName);
                System.out.println(
                        "ORIGINAL: "
                        + sequences1.get(sequenceName).getDefinition());
                System.out.println(
                        "NEW: " + sequences2.get(sequenceName).getDefinition());
            }
        }
    }

    /**
     * Creates diff of tables.
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffTables(PgSchema schema1, PgSchema schema2) {
        Map<String, PgTable> tables1 = schema1.getTables();
        Map<String, PgTable> tables2 = schema2.getTables();
        Set<String> tableNames1 = tables1.keySet();
        Set<String> tableNames2 = tables2.keySet();

        // Check what tables should be dropped
        for (String tableName : tableNames1) {
            if (!tableNames2.contains(tableName)) {
                System.out.println("\nDROP TABLE " + tableName + ";");
            }
        }

        // Check what tables should be created
        for (String tableName : tableNames2) {
            if (!tableNames1.contains(tableName)) {
                System.out.println("\n" + tables2.get(tableName).getTableSQL());
            }
        }

        // Check what columns should be dropped, created and modified
        for (String tableName : tableNames1) {
            if (tableNames2.contains(tableName)) {
                Map<String, PgColumn> columns1 =
                    tables1.get(tableName).getColumns();
                Map<String, PgColumn> columns2 =
                    tables2.get(tableName).getColumns();
                Set<String> columnNames1 = columns1.keySet();
                Set<String> columnNames2 = columns2.keySet();

                // Check what columns should be dropped
                for (String columnName : columnNames1) {
                    if (!columnNames2.contains(columnName)) {
                        System.out.println("\nALTER TABLE " + tableName);
                        System.out.println("\tDROP COLUMN " + columnName + ";");
                    }
                }

                // Check what columns should be created
                for (String columnName : columnNames2) {
                    if (!columnNames1.contains(columnName)) {
                        System.out.println("\nALTER TABLE " + tableName);
                        System.out.println(
                                "\tADD COLUMN "
                                + columns2.get(columnName).getFullDefinition()
                                + ";");
                    }
                }

                // Check what columns should be modified
                for (String columnName : columnNames1) {
                    if (
                        columnNames2.contains(columnName)
                            && !columns1.get(columnName).getDefinition().contentEquals(
                                    columns2.get(columnName).getDefinition())) {
                        System.out.println(
                                "\nMODIFIED COLUMN " + columnName
                                + " IN TABLE " + tableName);
                        System.out.println(
                                "ORIGINAL: "
                                + columns1.get(columnName).getDefinition());
                        System.out.println(
                                "NEW: "
                                + columns2.get(columnName).getDefinition());
                    }
                }
            }
        }
    }
}
