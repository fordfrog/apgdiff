package cz.startnet.utils.pgdiff.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Foreign Key constraint
 * @author laurent
 *
 */
public class PgFkConstraint extends PgColumnConstraint 
{
	private String targetTableName;
	private Collection<String> targetColumnNames;
	
	public PgFkConstraint(
			String name, String definition, PgTable table, List<String> colNames, 
			String targetTableName, Collection<String> targetColumnNames) 
	{
		super(name, definition, table, colNames);
		this.targetTableName = targetTableName;
		this.targetColumnNames = Collections.unmodifiableCollection(targetColumnNames);
	}
	
	public String getTargetTableName() {
		return this.targetTableName;
	}

	public Collection<String> getTargetColumnNames() {
		return this.targetColumnNames;
	}
}
