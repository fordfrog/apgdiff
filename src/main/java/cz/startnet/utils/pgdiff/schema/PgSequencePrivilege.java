/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores sequencePrivileges information.
 * 
 * @author user
 */
public class PgSequencePrivilege {

	private final String roleName;

	private boolean usage = false;
	private boolean select = false;
	private boolean update = false;

	private boolean usageWithGrantOption = false;
	private boolean selectWithGrantOption = false;
	private boolean updateWithGrantOption = false;

	/**
	 * Creates a new PgSequencePrivilege object.
	 * 
	 * @param roleName
	 *            name of the role
	 */
	public PgSequencePrivilege(final String roleName) {
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
		if ("USAGE".equalsIgnoreCase(privilege)
				|| "ALL".equalsIgnoreCase(privilege)) {
			if (value) {
				usage = true;
				if (grantOption) {
					usageWithGrantOption = true;
				}
			} else {
				usageWithGrantOption = false;
				if (!grantOption) {
					usage = false;
				}
			}
		}
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
	}

	/**
	 * true the privileges are the same (no matter of roleName).
	 * 
	 * @param other
	 *            privileges to compare
         * @return isSimilar
	 */
  	public boolean isSimilar(final PgSequencePrivilege other) {
		if (other == null) {
			return false;
		}
		if (usage != other.usage) {
			return false;
		}
		if (usageWithGrantOption != other.usageWithGrantOption) {
			return false;
		}
		if (select != other.select) {
			return false;
		}
		if (selectWithGrantOption != other.selectWithGrantOption) {
			return false;
		}
		if (update != other.update) {
			return false;
		}
		if (updateWithGrantOption != other.updateWithGrantOption) {
			return false;
		}
		return true;
	}

	public String getPrivilegesSQL(final boolean withGrantOption) {
		if (withGrantOption) {
			if (usageWithGrantOption && selectWithGrantOption
					&& updateWithGrantOption) {
				return "ALL";
			}
			String result = "";
			if (usageWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "USAGE";
			}
			if (selectWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "SELECT";
			}
			if (updateWithGrantOption) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "UPDATE";
			}
			return result;
		} else {
			if (usage && select && update) {
				return "ALL";
			}
			String result = "";
			if (select) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "SELECT";
			}
			if (usage) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "USAGE";
			}
			if (update) {
				if (!"".equals(result)) {
					result += ", ";
				}
				result += "UPDATE";
			}
			return result;
		}
	}

}
