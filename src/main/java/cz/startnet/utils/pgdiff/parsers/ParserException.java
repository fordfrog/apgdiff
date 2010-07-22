package cz.startnet.utils.pgdiff.parsers;

/**
 * Thrown if parsing problem occured.
 *
 * @author fordfrog
 */
public class ParserException extends RuntimeException {

    /**
     * Predefined message string that can be used as default exception
     * message with provided parsed line.
     */
    public static final String CANNOT_PARSE_COMMAND = "Cannot parse command: ";

    /**
     * Creates a new instance of <code>ParserException</code> without
     * detail message.
     */
    public ParserException() {
    }

    /**
     * Constructs an instance of <code>ParserException</code> with the
     * specified detail message.
     *
     * @param msg the detail message
     */
    public ParserException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ParserException</code> with the
     * specified detail message.
     *
     * @param msg the detail message
     * @param cause cause of the exception
     */
    public ParserException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
