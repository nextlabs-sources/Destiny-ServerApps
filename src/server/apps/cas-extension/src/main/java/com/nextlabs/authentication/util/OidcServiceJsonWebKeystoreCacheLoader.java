package com.nextlabs.authentication.util;

import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.oidc.jwks.OidcJsonWebKeyStoreUtils;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.PublicJsonWebKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class OidcServiceJsonWebKeystoreCacheLoader implements CacheLoader<OAuthRegisteredService, Optional<PublicJsonWebKey>> {

    private static final Logger logger = LoggerFactory.getLogger(OidcServiceJsonWebKeystoreCacheLoader.class);

    private final ApplicationContext applicationContext;

    public OidcServiceJsonWebKeystoreCacheLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Optional<PublicJsonWebKey> load(@NonNull final OAuthRegisteredService service) {
        if (service instanceof OidcRegisteredService) {
            val svc = (OidcRegisteredService) service;
            val jwks = getJsonWebKeySet(svc, applicationContext);
            if (jwks.isEmpty() || jwks.get().getJsonWebKeys().isEmpty()) {
                return Optional.empty();
            }
            val key = OidcJsonWebKeyStoreUtils.getJsonWebKeyFromJsonWebKeySet(jwks.get());
            if (key == null) {
                return Optional.empty();
            }
            return Optional.of(key);
        }
        return Optional.empty();
    }

    private Optional<JsonWebKeySet> getJsonWebKeySet(final OidcRegisteredService service,
                                                           final ResourceLoader resourceLoader) {
        try {
            logger.trace("Loading JSON web key from [{}]", service.getJwks());

            val resource = getJsonWebKeySetResource(service, resourceLoader);
            if (resource == null) {
                logger.warn("No JSON web keys or keystore resource could be found for [{}]", service);
                return Optional.empty();
            }
            val jsonWebKeySet = buildJsonWebKeySet(resource);

            if (jsonWebKeySet == null || jsonWebKeySet.getJsonWebKeys().isEmpty()) {
                logger.warn("No JSON web keys could be found for [{}]", service);
                return Optional.empty();
            }

            val badKeysCount = jsonWebKeySet.getJsonWebKeys().stream().filter(k ->
                    StringUtils.isBlank(k.getAlgorithm())
                            && StringUtils.isBlank(k.getKeyId())
                            && StringUtils.isBlank(k.getKeyType())).count();

            if (badKeysCount == jsonWebKeySet.getJsonWebKeys().size()) {
                logger.warn("No valid JSON web keys could be found for [{}]", service);
                return Optional.empty();
            }

            val webKey = OidcJsonWebKeyStoreUtils.getJsonWebKeyFromJsonWebKeySet(jsonWebKeySet);
            if (Objects.requireNonNull(webKey).getPublicKey() == null) {
                logger.warn("JSON web key retrieved [{}] has no associated public key", webKey.getKeyId());
                return Optional.empty();
            }
            return Optional.of(jsonWebKeySet);

        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    private Resource getJsonWebKeySetResource(final OidcRegisteredService service,
                                                     final ResourceLoader resourceLoader) {
        if (StringUtils.isNotBlank(service.getJwks())) {
            return resourceLoader.getResource(service.getJwks());
        }
        return null;
    }

    private JsonWebKeySet buildJsonWebKeySet(final Resource resource) throws Exception {
        logger.debug("Loading JSON web key from [{}]", resource);
        val json = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        logger.debug("Retrieved JSON web key from [{}] as [{}]", resource, json);
        return buildJsonWebKeySet(json);
    }

    private JsonWebKeySet buildJsonWebKeySet(final String json) throws Exception {
        val jsonWebKeySet = new JsonWebKeySet(json);
        val webKey = OidcJsonWebKeyStoreUtils.getJsonWebKeyFromJsonWebKeySet(jsonWebKeySet);
        if (webKey == null || webKey.getPublicKey() == null) {
            logger.warn("JSON web key retrieved [{}] is not found or has no associated public key", webKey);
            return null;
        }
        return jsonWebKeySet;
    }
}
