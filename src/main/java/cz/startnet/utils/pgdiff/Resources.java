/**
 * Copyright 2010 StartNet s.r.o.
 */
package cz.startnet.utils.pgdiff;

import java.util.ResourceBundle;

/**
 * Utility class for accessing localized resources.
 *
 * @author fordfrog
 */
public class Resources {

    /**
     * Resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE =
            ResourceBundle.getBundle("cz/startnet/utils/pgdiff/Resources");

    /**
     * Returns string from resource bundle based on the key.
     *
     * @param key key
     *
     * @return string
     */
    public static String getString(final String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    /**
     * Creates new instance of Resources.
     */
    private Resources() {
    }
}
