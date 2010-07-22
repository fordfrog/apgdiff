package cz.startnet.utils.pgdiff.schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores function information.
 *
 * @author fordfrog
 */
public class PgFunction {

    /**
     * Pattern for checking whether function definition contains CREATE
     * OR REPLACE FUNCTION string.
     */
    private static final Pattern PATTERN_CREATE_FUNCTION = Pattern.compile(
            "(?:CREATE[\\s]+FUNCTION)([\\s]+.*)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Declaration of the function. Contains function name and
     * arguments.
     */
    private String declaration;
    /**
     * Whole definition of the function.
     */
    private String definition;
    /**
     * Name of the function including argument types.
     */
    private String name;

    /**
     * Returns creation SQL of the function.
     *
     * @return creation SQL
     */
    public String getCreationSQL() {
        final String result;
        final Matcher matcher = PATTERN_CREATE_FUNCTION.matcher(definition);

        if (matcher.matches()) {
            result = "CREATE OR REPLACE FUNCTION" + matcher.group(1);
        } else {
            result = getDefinition();
        }

        return result;
    }

    /**
     * Setter for {@link #declaration}.
     *
     * @param declaration {@link #declaration}
     */
    public void setDeclaration(final String declaration) {
        this.declaration = declaration;
    }

    /**
     * Getter for {@link #declaration}.
     *
     * @return {@link #declaration}
     */
    public String getDeclaration() {
        return declaration;
    }

    /**
     * Setter for {@link #definition}.
     *
     * @param definition {@link #definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Getter for {@link #definition}.
     *
     * @return {@link #definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Creates and returns SQL for dropping the function.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP FUNCTION " + getDeclaration() + ";";
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
     * {@inheritDoc}
     *
     * @param object {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof PgFunction)) {
            return false;
        }

        return equals(object, false);
    }

    /**
     * Compares two objects whether they are equal. If both objects are of the
     * same class but they equal just in whitespace in {@link #definition},
     * they are considered being equal.
     *
     * @param object object to be compared
     * @param ignoreFunctionWhitespace whether multiple whitespaces in function
     * {@link #definition} should be ignored
     *
     * @return true if <code>object</code> is pg function and the function code
     * is the same when compared ignoring whitespace, otherwise returns false
     */
    public boolean equals(final Object object,
            final boolean ignoreFunctionWhitespace) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgFunction) {
            final PgFunction function = (PgFunction) object;
            final String thisDefinition;
            final String thatDefinition;

            if (ignoreFunctionWhitespace) {
                thisDefinition = getDefinition().replaceAll("\\s+", " ");
                thatDefinition =
                        function.getDefinition().replaceAll("\\s+", " ");
            } else {
                thisDefinition = getDefinition();
                thatDefinition = function.getDefinition();
            }
            equals = declaration.equals(function.getDeclaration())
                    && thisDefinition.equals(thatDefinition)
                    && name.equals(function.getName());
        }

        return equals;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (getClass().getName() + "|" + declaration + "|" + getDefinition()
                + "|" + name).hashCode();
    }
}
