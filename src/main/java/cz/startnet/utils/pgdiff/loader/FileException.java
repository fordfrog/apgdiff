/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.loader;

/**
 * Exception thrown if problem occured while reading or writing file.
 *
 * @author fordfrog
 * @version $Id$
 */
public class FileException extends RuntimeException {
    /**
     * Predefined message that can be used when cannot read file.
     */
    public static final String CANNOT_READ_FILE = "Cannot read file";

    /**
     * Creates a new instance of <code>FileException</code> without
     * detail message.
     */
    public FileException() {
        super();
    }

    /**
     * Constructs an instance of <code>FileException</code> with the
     * specified detail message.
     *
     * @param msg the detail message
     */
    public FileException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>FileException</code> with the
     * specified detail message.
     *
     * @param msg the detail message
     * @param cause cause of the exception
     */
    public FileException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
