package cz.startnet.utils.pgdiff.parsers;

/**
 * Thrown if parsing problem occured.
 *
 * @author fordfrog
 */
public class ParserException extends RuntimeException {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

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
