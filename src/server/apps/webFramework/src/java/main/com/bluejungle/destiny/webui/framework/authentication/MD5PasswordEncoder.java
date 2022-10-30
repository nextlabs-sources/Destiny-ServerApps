package com.bluejungle.destiny.webui.framework.authentication;

import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Mohammed Sainal Shah
 */
public class MD5PasswordEncoder extends MessageDigestPasswordEncoder {
    /**
     * The digest algorithm to use Supports the named
     * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/security/CryptoSpec.html#AppA">
     * Message Digest Algorithms</a> in the Java environment.
     *
     * @param algorithm
     */
    private static final String algorithm = "MD5";
    public MD5PasswordEncoder() {
        super(algorithm);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return super.matches(rawPassword, DatatypeConverter.printHexBinary(encodedPassword.getBytes()).toLowerCase());
    }
}
