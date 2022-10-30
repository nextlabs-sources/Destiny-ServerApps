package com.nextlabs.destiny.console.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

public class DigitalSignatureUtil {

	private DigitalSignatureUtil() {

	}

	/**
	 * Create digital signature using private key
	 * 
	 * @param algorithm  Signature algorithm
	 * @param privateKey Private key
	 * @return Digital signature
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static String sign(String algorithm, PrivateKey privateKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		return sign(algorithm, null, null, privateKey);
	}

	/**
	 * 
	 * Sign message with private key
	 * 
	 * @param algorithm       Signature algorithm
	 * @param message         Message to sign
	 * @param messageEncoding Encoding for the message, see {@link Charset}
	 * @param privateKey      Private Key
	 * @return Digital signature
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static String sign(String algorithm, String message, String messageEncoding, PrivateKey privateKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

		Signature privateSignature = Signature.getInstance(algorithm);
		privateSignature.initSign(privateKey);

		if (message != null && message.length() > 0) {
			privateSignature.update(message
					.getBytes(StringUtils.isBlank(messageEncoding) ? StandardCharsets.UTF_8.name() : messageEncoding));
		}

		byte[] signature = privateSignature.sign();
		return Base64.getEncoder().encodeToString(signature);
	}

	/**
	 * Verify digital signature with public key
	 * 
	 * @param algorithm Signature algorithm
	 * @param signature Digital signature to verify
	 * @param publicKey Public key
	 * @return true if the signature was verified, false if not
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean verify(String algorithm, String signature, PublicKey publicKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		return verify(algorithm, null, null, signature, publicKey);
	}

	/**
	 * Verify digital signature with public key
	 * 
	 * @param algorithm       Signature algorithm
	 * @param message         Message to sign
	 * @param messageEncoding Encoding for the message, see {@link Charset}
	 * @param signature       Digital signature to verify
	 * @param publicKey       Public Key
	 * @return true if the signature was verified, false if not
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean verify(String algorithm, String message, String messageEncoding, String signature,
			PublicKey publicKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
		Signature publicSignature = Signature.getInstance(algorithm);
		publicSignature.initVerify(publicKey);

		if (message != null && message.trim().length() > 0) {
			publicSignature.update(message
					.getBytes(StringUtils.isBlank(messageEncoding) ? StandardCharsets.UTF_8.name() : messageEncoding));
		}

		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return publicSignature.verify(signatureBytes);
	}

}
