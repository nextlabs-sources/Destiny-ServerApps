package com.nextlabs.destiny.cc.installer.config.properties.bindings;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.nextlabs.destiny.cc.installer.config.properties.Version;

/**
 * Bind version string to version object.
 *
 * @author Sachindra Dasun
 */
@Component
@ConfigurationPropertiesBinding
public class VersionBinding implements Converter<String, Version> {

    @Override
    public Version convert(String source) {
        return new Version(source.trim());
    }

}
