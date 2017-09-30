/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;


import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Stores function information.
 *
 * @author fordfrog
 */
public class PgFunction {

    /**
     * Name of the function including argument types.
     */
    private String name;
    /**
     * List of arguments.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<Argument> arguments = new ArrayList<Argument>();
    /**
     * Whole definition of the function from RETURNS keyword.
     */
    private String body;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Getter for {@link #comment}.
     *
     * @return {@link #comment}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link #comment}.
     *
     * @param comment {@link #comment}
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Returns creation SQL of the function.
     *
     * @return creation SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(500);
        sbSQL.append("CREATE OR REPLACE FUNCTION ");
        sbSQL.append(PgDiffUtils.getQuotedName(name));
        sbSQL.append('(');

        boolean addComma = false;

        for (final Argument argument : arguments) {
            if (addComma) {
                sbSQL.append(", ");
            }

            sbSQL.append(argument.getDeclaration(true));

            addComma = true;
        }

        sbSQL.append(") ");
        sbSQL.append(body);
        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON FUNCTION ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append('(');

            addComma = false;

            for (final Argument argument : arguments) {
                if (addComma) {
                    sbSQL.append(", ");
                }

                sbSQL.append(argument.getDeclaration(false));

                addComma = true;
            }

            sbSQL.append(") IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Setter for {@link #body}.
     *
     * @param body {@link #body}
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * Getter for {@link #body}.
     *
     * @return {@link #body}
     */
    public String getBody() {
        return body;
    }

    /**
     * Creates and returns SQL for dropping the function.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        final StringBuilder sbString = new StringBuilder(100);
        sbString.append("DROP FUNCTION ");
        sbString.append(PgDiffUtils.getDropIfExists());
        sbString.append(name);
        sbString.append('(');

        boolean addComma = false;

        for (final Argument argument : arguments) {
            if ("OUT".equalsIgnoreCase(argument.getMode())) {
                continue;
            }

            if (addComma) {
                sbString.append(", ");
            }

            sbString.append(argument.getDeclaration(false));

            addComma = true;
        }

        sbString.append(");");

        return sbString.toString();
    }

    /**
     * Setter for {@link #name}.
     *
     * @param name {@link #name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name}.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for {@link #arguments}. List cannot be modified.
     *
     * @return {@link #arguments}
     */
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    /**
     * Adds argument to the list of arguments.
     *
     * @param argument argument
     */
    public void addArgument(final Argument argument) {
        arguments.add(argument);
    }

    /**
     * Returns function signature. It consists of unquoted name and argument
     * data types.
     *
     * @return function signature
     */
    public String getSignature() {
        final StringBuilder sbString = new StringBuilder(100);
        sbString.append(name);
        sbString.append('(');

        boolean addComma = false;

        for (final Argument argument : arguments) {
            if ("OUT".equalsIgnoreCase(argument.getMode())) {
                continue;
            }

            if (addComma) {
                sbString.append(',');
            }

            sbString.append(argument.getDataType().toLowerCase(Locale.ENGLISH));

            addComma = true;
        }

        sbString.append(')');

        return sbString.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof PgFunction)) {
            return false;
        } else if (object == this) {
            return true;
        }

        return equals(object, false);
    }

    /**
     * Compares two objects whether they are equal. If both objects are of the
     * same class but they equal just in whitespace in {@link #body}, they are
     * considered being equal.
     *
     * @param object                   object to be compared
     * @param ignoreFunctionWhitespace whether multiple whitespaces in function
     *                                 {@link #body} should be ignored
     *
     * @return true if {@code object} is pg function and the function code is
     *         the same when compared ignoring whitespace, otherwise returns
     *         false
     */
    public boolean equals(final Object object,
            final boolean ignoreFunctionWhitespace) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgFunction) {
            final PgFunction function = (PgFunction) object;

            if (name == null && function.getName() != null
                    || name != null && !name.equals(function.getName())) {
                return false;
            }

            final String thisBody;
            final String thatBody;

            if (ignoreFunctionWhitespace) {
                thisBody = body.replaceAll("\\s+", " ");
                thatBody =
                        function.getBody().replaceAll("\\s+", " ");
            } else {
                thisBody = body;
                thatBody = function.getBody();
            }

            if (thisBody == null && thatBody != null
                    || thisBody != null && !thisBody.equals(thatBody)) {
                return false;
            }

            if (arguments.size() != function.getArguments().size()) {
                return false;
            } else {
                for (int i = 0; i < arguments.size(); i++) {
                    if (!arguments.get(i).equals(function.getArguments().get(i))) {
                        return false;
                    }
                }
            }

            return true;
        }

        return equals;
    }

    @Override
    public int hashCode() {
        final StringBuilder sbString = new StringBuilder(500);
        sbString.append(body);
        sbString.append('|');
        sbString.append(name);

        for (final Argument argument : arguments) {
            sbString.append('|');
            sbString.append(argument.getDeclaration(true));
        }

        return sbString.toString().hashCode();
    }

    /**
     * Function argument information.
     */
    @SuppressWarnings("PublicInnerClass")
    public static class Argument {

        /**
         * Argument mode.
         */
        private String mode = "IN";
        /**
         * Argument name.
         */
        private String name;
        /**
         * Argument data type.
         */
        private String dataType;
        /**
         * Argument default expression.
         */
        private String defaultExpression;

        /**
         * Getter for {@link #dataType}.
         *
         * @return {@link #dataType}
         */
        public String getDataType() {
            return dataType;
        }

        /**
         * Setter for {@link #dataType}.
         *
         * @param dataType {@link #dataType}
         */
        public void setDataType(final String dataType) {
            this.dataType = dataType;
        }

        /**
         * Getter for {@link #defaultExpression}.
         *
         * @return {@link #defaultExpression}
         */
        public String getDefaultExpression() {
            return defaultExpression;
        }

        /**
         * Setter for {@link #defaultExpression}.
         *
         * @param defaultExpression {@link #defaultExpression}
         */
        public void setDefaultExpression(final String defaultExpression) {
            this.defaultExpression = defaultExpression;
        }

        /**
         * Getter for {@link #mode}.
         *
         * @return {@link #mode}
         */
        public String getMode() {
            return mode;
        }

        /**
         * Setter for {@link #mode}.
         *
         * @param mode {@link #mode}
         */
        public void setMode(final String mode) {
            this.mode = mode == null || mode.isEmpty() ? "IN" : mode;
        }

        /**
         * Getter for {@link #name}.
         *
         * @return {@link #name}
         */
        public String getName() {
            return name;
        }

        /**
         * Setter for {@link #name}.
         *
         * @param name {@link #name}
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Creates argument declaration.
         *
         * @param includeDefaultValue whether to include default value
         *
         * @return argument declaration
         */
        public String getDeclaration(final boolean includeDefaultValue) {
            final StringBuilder sbString = new StringBuilder(50);

            if (mode != null && !"IN".equalsIgnoreCase(mode)) {
                sbString.append(mode);
                sbString.append(' ');
            }

            if (name != null && !name.isEmpty()) {
                sbString.append(PgDiffUtils.getQuotedName(name));
                sbString.append(' ');
            }

            sbString.append(dataType);

            if (includeDefaultValue && defaultExpression != null
                    && !defaultExpression.isEmpty()) {
                sbString.append(" = ");
                sbString.append(defaultExpression);
            }

            return sbString.toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Argument)) {
                return false;
            } else if (this == obj) {
                return true;
            }

            final Argument argument = (Argument) obj;

            return (dataType == null ? argument.getDataType() == null
                    : dataType.equalsIgnoreCase(argument.getDataType()))
                    && (defaultExpression == null
                    ? argument.getDefaultExpression() == null
                    : defaultExpression.equals(defaultExpression))
                    && (mode == null ? argument.getMode() == null
                    : mode.equalsIgnoreCase(argument.getMode()))
                    && (name == null ? argument.getName() == null
                    : name.equals(argument.getName()));
        }

        @Override
        public int hashCode() {
            final StringBuilder sbString = new StringBuilder(50);
            sbString.append(
                    mode == null ? null : mode.toUpperCase(Locale.ENGLISH));
            sbString.append('|');
            sbString.append(name);
            sbString.append('|');
            sbString.append(dataType == null ? null
                    : dataType.toUpperCase(Locale.ENGLISH));
            sbString.append('|');
            sbString.append(defaultExpression);

            return sbString.toString().hashCode();
        }
    }
}
