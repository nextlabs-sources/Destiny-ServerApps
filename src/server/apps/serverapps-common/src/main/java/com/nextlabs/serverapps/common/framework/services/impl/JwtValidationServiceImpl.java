package com.nextlabs.serverapps.common.framework.services.impl;

import com.nextlabs.serverapps.common.framework.services.JwtValidationService;
import com.nextlabs.serverapps.common.properties.CCOIDCService;
import com.nextlabs.serverapps.common.properties.CasOidcProperties;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;

/**
 * JWT validation service implementation.
 *
 * @author Mohammed Sainal Shah
 */
public class JwtValidationServiceImpl implements JwtValidationService {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationServiceImpl.class);

    private CasOidcProperties casOidcProperties;

    public JwtValidationServiceImpl(CasOidcProperties casOidcProperties) {
        this.casOidcProperties = casOidcProperties;
    }

    public JwtClaims validateJwt(String idToken) {
        try {
            JwtClaims jwtClaims = validateToken(idToken);
            logger.debug("jwtClaims: {}", jwtClaims);
            return jwtClaims;
        } catch (InvalidJwtException | JoseException e) {
            logger.warn("JWT validation failed {}", e.getCause());
        }
        return null;
    }

    // Decrypts, then verifies the signature and claims
    private JwtClaims validateToken(String idToken) throws InvalidJwtException, JoseException {

        RsaJsonWebKey rsaJwkSign = (RsaJsonWebKey) new JsonWebKeySet(
                casOidcProperties.getSigningJwks()).getJsonWebKeys().get(0);
        RsaJsonWebKey rsaJwkDecrypt = (RsaJsonWebKey) new JsonWebKeySet(
                casOidcProperties.getEncryptionJwks()).getJsonWebKeys().get(0);

        JwtConsumer firstPassJwtConsumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .setDecryptionKey(rsaJwkDecrypt.getRsaPrivateKey())
                .setJweAlgorithmConstraints(getEncryptionAlgorithmConstraints())
                .build();
        JwtContext jwtContext = firstPassJwtConsumer.process(idToken);
        Key verificationKey = rsaJwkSign.getKey();
        JwtConsumer secondPassJwtConsumer = new JwtConsumerBuilder()
                .setExpectedIssuer(casOidcProperties.getOidcIssuer())
                .setVerificationKey(verificationKey)
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(0)
                .setRequireSubject()
                .setExpectedAudience(casOidcProperties.getServices()
                        .stream()
                        .map(CCOIDCService::getClientId).toArray(String[]::new))
                .setJwsAlgorithmConstraints(getSigningAlgorithmConstraints())
                .build();

        // Finally using the second JwtConsumer to actually validate the JWT. This operates on
        // the JwtContext from the first processing pass, which avoids redundant parsing/processing.
        secondPassJwtConsumer.processContext(jwtContext);
        return jwtContext.getJwtClaims();
    }

    private AlgorithmConstraints getEncryptionAlgorithmConstraints(){
        return new AlgorithmConstraints(
                AlgorithmConstraints.ConstraintType.WHITELIST,
                casOidcProperties.getEncryptionAlgorithm(),
                casOidcProperties.getEncryptionEncoding());
    }

    private AlgorithmConstraints getSigningAlgorithmConstraints(){
        return new AlgorithmConstraints(
                AlgorithmConstraints.ConstraintType.WHITELIST,
                casOidcProperties.getSigningAlgorithm());
    }
}
