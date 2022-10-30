package com.nextlabs.destiny.console.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.PropertyKey;
import com.bluejungle.framework.security.KeyManagerImpl;
import com.bluejungle.framework.security.KeyManagerImpl.KeystoreFileInfo;
import com.google.common.primitives.Bytes;
import com.nextlabs.destiny.console.dto.policymgmt.porting.EbinDTO;
import com.nextlabs.destiny.console.utils.AESUtil.Transformation;

public class PolicyPortingUtil {

	public static final String PROPERTY_KEY_KEYSTORE_FILE_NAME = "keystoreFileName";
	public static final int IV_SIZE = 16;
	public static final int KEY_SIZE = 32;
	public static final int INCREASE_LENGTH_BY_1 = 1;
	public static final int DECREASE_LENGTH_BY_1 = 1;
	public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	public static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
	public static final String FILE_EXTENSION_BIN = ".bin";
	public static final String FILE_EXTENSION_EBIN = ".ebin";
	public static final String XACML_FILE_EXTENSION = ".xml";
	public static final String HOSTNAME_REGEX = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

	public enum DataTransportationMode {
		PLAIN, SANDE;
	}

	private PolicyPortingUtil() {

	}

	/**
	 * Configure and initialize {@link KeyManagerImpl}
	 * 
	 * @param alias                      Alias of the keystore entry
	 * @param keystoreFileInfoIdentifier Identifier for {@link KeystoreFileInfo}
	 * @param keystoreFilePath           File path of the keystore
	 * @param keystoreType               Keystore's type
	 * @param keystoreFilePassword       Password of the keystore
	 * @return {@link KeyManagerImpl}
	 */
	public static KeyManagerImpl createKeyManagerImpl(String alias, String keystoreFileInfoIdentifier,
			String keystoreFilePath, String keystoreType, String keystoreFilePassword) {
		KeystoreFileInfo keystoreFileInfo = new KeystoreFileInfo(keystoreFileInfoIdentifier, keystoreFilePath,
				keystoreType, keystoreFilePassword);
		KeyManagerImpl keyManagerImpl = new KeyManagerImpl();
		HashMapConfiguration configuration = new HashMapConfiguration();
		Set<KeystoreFileInfo> keystoreFileInfoSet = new HashSet<>();
		keystoreFileInfoSet.add(keystoreFileInfo);
		PropertyKey<Set<KeystoreFileInfo>> propertyKey = new PropertyKey<>(PROPERTY_KEY_KEYSTORE_FILE_NAME);
		configuration.setProperty(propertyKey, keystoreFileInfoSet);
		keyManagerImpl.setConfiguration(configuration);
		keyManagerImpl.init();
		return keyManagerImpl;
	}

	/**
	 * Get private key
	 * 
	 * @param alias                      Alias of the keystore entry
	 * @param keystoreFileInfoIdentifier Identifier for {@link KeystoreFileInfo}
	 * @param keystoreFilePath           File path of the keystore
	 * @param keystoreType               Keystore's type
	 * @param keystoreFilePassword       Password of the keystore
	 * @return {@link PrivateKey}
	 */
	public static PrivateKey getPrivateKey(String alias, String keystoreFileInfoIdentifier, String keystoreFilePath,
			String keystoreType, String keystoreFilePassword) {
		KeyManagerImpl keyManagerImpl = createKeyManagerImpl(alias, keystoreFileInfoIdentifier, keystoreFilePath,
				keystoreType, keystoreFilePassword);
		return keyManagerImpl.getPrivateKey(alias);
	}

	/**
	 * Get public key
	 * 
	 * @param alias                      Alias of the keystore entry
	 * @param keystoreFileInfoIdentifier Name of the keystore file info
	 * @param keystoreFilePath           File path of the keystore
	 * @param keystoreType               Keystore's type
	 * @param keystoreFilePassword       Password of the keystore
	 * @return {@link PublicKey}
	 */
	public static PublicKey getPublicKey(String alias, String keystoreFileInfoIdentifier, String keystoreFilePath,
			String keystoreType, String keystoreFilePassword) {
		KeyManagerImpl keyManagerImpl = createKeyManagerImpl(alias, keystoreFileInfoIdentifier, keystoreFilePath,
				keystoreType, keystoreFilePassword);
		return keyManagerImpl.getPublicKey(alias);
	}
	
	/**
	 * Check whether keystore contains the required public key
	 * 
	 * @param alias                      Alias of the keystore entry
	 * @param keystoreFileInfoIdentifier Name of the keystore file info
	 * @param keystoreFilePath           File path of the keystore
	 * @param keystoreType               Keystore's type
	 * @param keystoreFilePassword       Password of the keystore
	 * @return true if keystore contains the required public key, false if not
	 */
	public static boolean containsPublicKey(String alias, String keystoreFileInfoIdentifier, String keystoreFilePath,
			String keystoreType, String keystoreFilePassword) {
		KeyManagerImpl keyManagerImpl = createKeyManagerImpl(alias, keystoreFileInfoIdentifier, keystoreFilePath,
				keystoreType, keystoreFilePassword);
		return keyManagerImpl.containsPublicKey(alias);
	}

	/**
	 * Generate ebin content for policy bundle
	 * 
	 * @param policyBundle      Policy bundle to encrypt
	 * @param secretKey         Secret key for AES encryption
	 * @param signatureKeyAlias Signature key alias
	 * @param digitalSignature  Digital signature generated with private key
	 * @return Byte array of ebin content
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static byte[] generateEbinContent(String policyBundle, String secretKey, String signatureKeyAlias,
			String digitalSignature) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String content = signatureKeyAlias + digitalSignature + policyBundle;
		byte[] encrypted = AESUtil.encrypt(Transformation.AES_CBC_NOPADDING, content, IV_SIZE, secretKey, KEY_SIZE,
				MESSAGE_DIGEST_ALGORITHM);
		String info = signatureKeyAlias.length() + "|" + digitalSignature.length() + "|";
		byte[] ebinContent = new byte[info.length() + encrypted.length];
		System.arraycopy(info.getBytes(), 0, ebinContent, 0, info.length());
		System.arraycopy(encrypted, 0, ebinContent, info.length(), encrypted.length);
		return ebinContent;
	}

	/**
	 * Extract Signature Key Alias, Digital Signature and Policy Bundle from ebin
	 * content
	 * 
	 * @param ebinContent Byte array of ebin content
	 * @param secretKey   Secret key for AES decryption
	 * @return {@link EbinDTO}
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyExceptionn
	 */
	public static EbinDTO extractEbinContent(byte[] ebinContent, String secretKey)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		// Get length of signature key alias
		int signatureKeyAliasIndex = Bytes.indexOf(ebinContent, "|".getBytes());
		byte[] signatureKeyAliasBytes = new byte[signatureKeyAliasIndex];
		System.arraycopy(ebinContent, 0, signatureKeyAliasBytes, 0, signatureKeyAliasBytes.length);
		int signatureKeyAliasSize = Integer.parseInt(new String(signatureKeyAliasBytes));

		// Get length of digital signature
		int remainingSize = ebinContent.length - signatureKeyAliasBytes.length - DECREASE_LENGTH_BY_1;
		byte[] remainingBytes = new byte[remainingSize];
		System.arraycopy(ebinContent, signatureKeyAliasIndex + INCREASE_LENGTH_BY_1, remainingBytes, 0, remainingSize);
		int digitalSignatureIndex = Bytes.indexOf(remainingBytes, "|".getBytes());
		byte[] digitalSignatureByte = new byte[digitalSignatureIndex];
		System.arraycopy(remainingBytes, 0, digitalSignatureByte, 0, digitalSignatureByte.length);
		int digitalSignatureSize = Integer.parseInt(new String(digitalSignatureByte));

		// Get encrypted data
		int encryptedSize = remainingBytes.length - digitalSignatureByte.length - DECREASE_LENGTH_BY_1;
		byte[] encryptedBytes = new byte[encryptedSize];
		System.arraycopy(remainingBytes, digitalSignatureByte.length + INCREASE_LENGTH_BY_1, encryptedBytes, 0, encryptedSize);

		// Get value of signature key alias, digital signature & decrypted data
		String content = AESUtil.decrypt(AESUtil.Transformation.AES_CBC_NOPADDING, encryptedBytes, IV_SIZE, secretKey,
				KEY_SIZE, MESSAGE_DIGEST_ALGORITHM);
		String alias = content.substring(0, signatureKeyAliasSize);
		String signature = content.substring(alias.length(), alias.length() + digitalSignatureSize);
		String plainData = content.substring(alias.length() + signature.length());
		EbinDTO dto = new EbinDTO();
		dto.setAlias(alias);
		dto.setSignature(signature);
		dto.setPolicyBundle(plainData);
		return dto;
	}

	/**
	 * Generate digital signature
	 * 
	 * @param alias                      Alias of the keystore entry
	 * @param keystoreFileInfoIdentifier Identifier for {@link KeystoreFileInfo}
	 * @param keystoreFilePath           File path of the keystore
	 * @param keystoreType               Keystore's type
	 * @param keystoreFilePassword       Password of the keystore
	 * @param signatureAlgorithm         Signature algorithm
	 * @return Digital signature
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static String createDigitalSignature(String alias, String keystoreFileInfoIdentifier,
			String keystoreFilePath, String keystoreType, String keystoreFilePassword, String signatureAlgorithm)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException {
		PrivateKey privateKey = PolicyPortingUtil.getPrivateKey(alias, keystoreFileInfoIdentifier, keystoreFilePath,
				keystoreType, keystoreFilePassword);
		return DigitalSignatureUtil.sign(signatureAlgorithm, privateKey);
	}

	/**
	 * Verify digital signature
	 * 
	 * @param alias                      Signature key alias
	 * @param signature                  Digital signature
	 * @param keystoreFileInfoIdentifier Identifier for {@link KeystoreFileInfo}
	 * @param keystoreFilePath           File path of the keystore
	 * @param keystoreType               Keystore's type
	 * @param keystoreFilePassword       Password of the keystore
	 * @param signatureAlgorithm         Signature algorithm
	 * @return true if the signature was verified, false if not
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean verifyDigitalSignature(String alias, String signature, String keystoreFileInfoIdentifier,
			String keystoreFilePath, String keystoreType, String keystoreFilePassword, String signatureAlgorithm)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException {
		PublicKey publicKey = PolicyPortingUtil.getPublicKey(alias, keystoreFileInfoIdentifier, keystoreFilePath,
				keystoreType, keystoreFilePassword);
		return DigitalSignatureUtil.verify(signatureAlgorithm, signature, publicKey);
	}

}
