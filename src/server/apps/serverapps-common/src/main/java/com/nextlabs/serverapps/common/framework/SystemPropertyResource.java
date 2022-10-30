package com.nextlabs.serverapps.common.framework;

import com.google.common.io.CharSource;
import org.apache.commons.io.input.ReaderInputStream;
import org.springframework.core.io.AbstractFileResolvingResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SystemPropertyResource extends AbstractFileResolvingResource {

    private static final String DESCRIPTION = "SystemPropertyResource";
    public static final String PROPERTY_URL_PREFIX = "cc_property:/";

    private String key;
    private String value;

    public SystemPropertyResource(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ReaderInputStream(CharSource.wrap(value).openStream(),
                Charset.defaultCharset());
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
