package cz.startnet.utils.pgdiff.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A Constraint that uses some columns
 * @author laurent
 *
 */
public class PgColumnConstraint extends PgConstraint 
{
	Set<PgColumn> columns;
	
	public PgColumnConstraint(String name, String definition, PgTable table, Collection<String> colNames) {
		super(name,definition,table);
		this.columns = table.getColumns(colNames);
	}
	
	public Set<PgColumn> getColumns() {
		return Collections.unmodifiableSet(columns);
	}

	public Set<String> getColumnNames() {
		HashSet<String> names = new HashSet<String>(columns.size());
		for(PgColumn column: columns) {
			names.add(column.getName());
		}
		return names;
	}
}
