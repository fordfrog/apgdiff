package cz.startnet.utils.pgdiff.schema;

import java.util.List;

/**
 * Primary Key constraint
 * @author laurent
 *
 */
public class PgPkConstraint extends PgColumnConstraint 
{
	public PgPkConstraint(String name, String definition, PgTable table, List<String> colNames) {
		super(name, definition, table, colNames);
	}
}
