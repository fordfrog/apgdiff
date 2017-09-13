/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.util;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgTable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compare column tables
 *
 * @author byorty
 */
public class ColumnComparator {
    
    private HashMap<String, PgColumn> columns;
    
    public ColumnComparator(PgTable oldTable, PgTable newTable) {
        this.columns = new HashMap<String, PgColumn>();
        HashMap<Integer, PgColumn> diffOldColumns = new HashMap<Integer, PgColumn>();
        HashMap<Integer, PgColumn> diffNewColumns = new HashMap<Integer, PgColumn>();
        List<PgColumn> newColumns = newTable.getColumns();
        for (int i = 0;i < newColumns.size();++i) {
            PgColumn newColumn = newTable.getColumns().get(i);
            PgColumn oldColumn = oldTable.getColumn(newColumn.getName());
            if (null == oldColumn && false == diffNewColumns.containsKey(i)) {
                diffNewColumns.put(i, newColumn);
            } else {
                this.columns.put(newColumn.getName(), oldColumn);
            }
        }
        List<PgColumn> oldColumns = oldTable.getColumns();
        for (int i = 0;i < oldColumns.size();++i) {
            PgColumn oldColumn = oldColumns.get(i);
            if (false == this.columns.containsKey(oldColumn.getName())) {
                diffOldColumns.put(i, oldColumn);
            }
        }
        for (Map.Entry<Integer, PgColumn> oldEntry : diffOldColumns.entrySet()) {
            Integer oldColumnNumber = oldEntry.getKey();
            PgColumn oldColumn = oldEntry.getValue();
            String oldColumnName = oldColumn.getName();
            char[] oldColumnChars = oldColumnName.toCharArray();
            int containsCount = 0;
            int diffCount = -1;
            HashMap<Integer, HashMap<Integer, PgColumn>> variants = new HashMap<Integer, HashMap<Integer, PgColumn>>();
            for (Map.Entry<Integer, PgColumn> newEntry : diffNewColumns.entrySet()) {
                Integer newColumnNumber = newEntry.getKey();
                PgColumn newColumn = newEntry.getValue();
                String newColumnName = newColumn.getName();
                char[] newColumnChars = newColumnName.toCharArray();
                HashMap<Character, Integer> findChars = new HashMap<Character, Integer>();
                for (int i = 0;i < oldColumnChars.length;++i) {
                    findChars.put(oldColumnChars[i], 0);
                    for (int j = 0;j < newColumnChars.length;++j) {
                        if (oldColumnChars[i] == newColumnChars[j]
                            && findChars.containsKey(newColumnChars[j])
                            && 0 == findChars.get(newColumnChars[j])) {
                            findChars.put(newColumnChars[j], 1);
                        }
                    }
                }
                int currentContainsCount = 0;
                for (Map.Entry<Character, Integer> findChar : findChars.entrySet()) {
                    currentContainsCount += findChar.getValue();
                }
                if ((double)currentContainsCount >= (oldColumnName.length() + newColumnName.length()) / 4) {
                    if (containsCount < currentContainsCount) {
                        variants.remove(containsCount);
                        containsCount = currentContainsCount;
                        HashMap<Integer, PgColumn> map = new HashMap<Integer, PgColumn>();
                        map.put(newColumnNumber, newColumn);
                        variants.put(containsCount, map);
                    } else if (containsCount == currentContainsCount) {
                        variants.get(containsCount).put(newColumnNumber, newColumn);
                    }
                }
            }
            
            for (Map.Entry<Integer, HashMap<Integer, PgColumn>> variantEntry : variants.entrySet()) {
                HashMap<Integer, PgColumn> map = variantEntry.getValue();
                
                if (1 == map.size()) {
                    for (Map.Entry<Integer, PgColumn> entry : map.entrySet()) {
                        PgColumn pgColumn = entry.getValue();
                        this.columns.put(pgColumn.getName(), oldTable.getColumn(oldColumnName));
                    }
                } else {
                    for (Map.Entry<Integer, PgColumn> entry : map.entrySet()) {
                        Integer pgColumnNumber = entry.getKey();
                        PgColumn pgColumn = entry.getValue();
                        if (oldColumnNumber == pgColumnNumber) {
                            this.columns.put(pgColumn.getName(), oldTable.getColumn(oldColumnName));
                        } else {
                            int currentContainsCount = 0;
                            if (oldColumn.getNullValue() == pgColumn.getNullValue()) {
                                ++currentContainsCount;
                            }
                            if (oldColumn.getDefaultValue() == null ? pgColumn.getDefaultValue() == null : oldColumn.getDefaultValue().equals(pgColumn.getDefaultValue())) {
                                ++currentContainsCount;
                            }
                            if (oldColumn.getType() == null ? pgColumn.getType() == null : oldColumn.getType().equals(pgColumn.getType())) {
                                ++currentContainsCount;
                            }
                            if (3 == currentContainsCount) {
                                this.columns.put(pgColumn.getName(), oldTable.getColumn(oldColumnName));
                            }
                        }
                    }
                } 
            }
        }
    }
    
    public boolean hasNotColumn(String name) {
        return null == this.getColumn(name);
    }
    
    public PgColumn getColumn(String name) {
        if (this.columns.containsKey(name)) {
            return this.columns.get(name);
        } else {
            return null;
        }
    }
}