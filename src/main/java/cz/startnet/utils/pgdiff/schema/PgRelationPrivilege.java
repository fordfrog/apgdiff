/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores relPrivileges information.
 * 
 * @author user
 */
public class PgRelationPrivilege {

	private final String roleName;

	private boolean select = false;
	private boolean insert = false;
	private boolean update = false;
	private boolean delete = false;
	private boolean truncate = false;
	private boolean references = false;
	private boolean trigger = false;

	private boolean selectWithGrantOption = false;
	private boolean insertWithGrantOption = false;
	private boolean updateWithGrantOption = false;
	private boolean deleteWithGrantOption = false;
	private boolean truncateWithGrantOption = false;
	private boolean referencesWithGrantOption = false;
	private boolean triggerWithGrantOption = false;

	/**
	 * Creates a new PgTablePrivilege object.
	 * 
	 * @param roleName
	 *            name of the role
	 */
	public PgRelationPrivilege(final String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	public void setPrivileges(final String privilege, final boolean value,
			final boolean grantOption) {
		if ("SELECT".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				select = true;
				if (grantOption) {
					selectWithGrantOption = true;
				}
			} else {
				selectWithGrantOption = false;
				if (!grantOption) {
					select = false;
				}
			}
		}
		if ("INSERT".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				insert = true;
				if (grantOption) {
					insertWithGrantOption = true;
				}
			} else {
				insertWithGrantOption = false;
				if (!grantOption) {
					insert = false;
				}
			}
		}
		if ("UPDATE".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				update = true;
				if (grantOption) {
					updateWithGrantOption = true;
				}
			} else {
				updateWithGrantOption = false;
				if (!grantOption) {
					update = false;
				}
			}
		}
		if ("DELETE".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				delete = true;
				if (grantOption) {
					deleteWithGrantOption = true;
				}
			} else {
				deleteWithGrantOption = false;
				if (!grantOption) {
					delete = false;
				}
			}
		}
		if ("TRUNCATE".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				truncate = true;
				if (grantOption) {
					truncateWithGrantOption = true;
				}
			} else {
				truncateWithGrantOption = false;
				if (!grantOption) {
					truncate = false;
				}
			}
		}
		if ("REFERENCES".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				references = true;
				if (grantOption) {
					referencesWithGrantOption = true;
				}
			} else {
				referencesWithGrantOption = false;
				if (!grantOption) {
					references = false;
				}
			}
		}
		if ("TRIGGER".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				trigger = true;
				if (grantOption) {
					triggerWithGrantOption = true;
				}
			} else {
				triggerWithGrantOption = false;
				if (!grantOption) {
					trigger = false;
				}
			}
		}

	}

	/**
	 * true the privileges are the same (no matter of roleName).
	 * 
	 * @param other
	 *            privileges to compare
         * @return isSimilar
	 */
  	public boolean isSimilar(final PgRelationPrivilege other) {
		if (other == null) {
			return false;
		}
		if (select != other.select) {
	 		return false;
		}
		if (selectWithGrantOption != other.selectWithGrantOption) {
			return false;
		}
		if (insert != other.insert) {
			return false;
		}
		if (insertWithGrantOption != other.insertWithGrantOption) {
			return false;
		}
		if (update != other.update) {
			return false;
		}
		if (updateWithGrantOption != other.updateWithGrantOption) {
			return false;
		}
		if (delete != other.delete) {
			return false;
		}
		if (deleteWithGrantOption != other.deleteWithGrantOption) {
			return false;
		}
		if (truncate != other.truncate) {
			return false;
		}
		if (truncateWithGrantOption != other.truncateWithGrantOption) {
			return false;
		}
		if (references != other.references) {
			return false;
		}
		if (referencesWithGrantOption != other.referencesWithGrantOption) {
			return false;
		}
		if (trigger != other.trigger) {
			return false;
		}
		if (triggerWithGrantOption != other.triggerWithGrantOption) {
			return false;
		}
		return true;
	}

	public String getPrivilegesSQL(final boolean withGrantOption) {
		if (withGrantOption) {
			if (selectWithGrantOption && insertWithGrantOption
					&& updateWithGrantOption && deleteWithGrantOption
					&& truncateWithGrantOption && referencesWithGrantOption
					&& triggerWithGrantOption) {
				return "ALL";
			}
			String result = "";
			if (selectWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "SELECT";
			}
			if (insertWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "INSERT";
			}
			if (updateWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "UPDATE";
			}
			if (deleteWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "DELETE";
			}
			if (truncateWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "TRUNCATE";
			}
			if (referencesWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "REFERENCES";
			}
			if (triggerWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "TRIGGER";
			}
			return result;
		} else {
			if (select && insert && update && delete && truncate && references
					&& trigger) {
				return "ALL";
			}
			String result = "";
			if (select) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "SELECT";
			}
			if (insert) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "INSERT";
			}
			if (update) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "UPDATE";
			}
			if (delete) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "DELETE";
			}
			if (truncate) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "TRUNCATE";
			}
			if (references) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "REFERENCES";
			}
			if (trigger) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "TRIGGER";
			}
			return result;
		}
	}

}
