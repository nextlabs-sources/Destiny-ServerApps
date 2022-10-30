package com.nextlabs.serverapps.common.authentication;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Mohammed Sainal Shah
 */
public class MD5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return DatatypeConverter.printHexBinary(DigestUtils.md5(rawPassword.toString()));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equalsIgnoreCase(encodedPassword);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return true;
    }
}
