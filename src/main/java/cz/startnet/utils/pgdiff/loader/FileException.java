/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.loader;

/**
 * Exception thrown if problem occurred while reading or writing file.
 *
 * @author fordfrog
 */
public class FileException extends RuntimeException {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of {@code FileException} without detail message.
     */
    public FileException() {
    }

    /**
     * Constructs an instance of {@code FileException} with the specified detail
     * message.
     *
     * @param msg the detail message
     */
    public FileException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of {@code FileException} with the specified detail
     * message.
     *
     * @param msg   the detail message
     * @param cause cause of the exception
     */
    public FileException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
