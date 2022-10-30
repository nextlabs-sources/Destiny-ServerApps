package com.nextlabs.destiny.console.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	public static final int BLOCK_SIZE_IN_BYTE = 16;
	public static final String KEY_ALGORITHM_AES = "AES";

	/**
	 * Transformation enum
	 * 
	 * @value AES_CBC_NOPADDING - AES/CBC/NoPadding
	 * 
	 * @value AES_CBC_PKCS5PADDING - AES/CBC/PKCS5Padding
	 *
	 */
	public enum Transformation {

		AES_CBC_NOPADDING("AES/CBC/NoPadding"),

		AES_CBC_PKCS5PADDING("AES/CBC/PKCS5Padding");

		private String name;

		private Transformation(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	private AESUtil() {

	}

	/**
	 * Encrypt data using AES symmetric key
	 * 
	 * @param transformation         {@link Transformation} transformation for the
	 *                               cipher object
	 * @param plainText              Data to encrypt
	 * @param ivSize                 IV size
	 * @param key                    AES symmetric key
	 * @param keySize                AES key size in byte (16, 24 or 32)
	 * @param messageDigestAlgorithm Algorithm for message digest (MG5, SHA-1 or
	 *                               SHA-256. Depend on keySize)
	 * @return Byte array of IV value and cipher text
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encrypt(Transformation transformation, String plainText, int ivSize, String key, int keySize,
			String messageDigestAlgorithm)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] original = plainText.getBytes();

		byte[] iv = new byte[ivSize];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		MessageDigest digest = MessageDigest.getInstance(messageDigestAlgorithm);
		digest.update(key.getBytes(StandardCharsets.UTF_8.name()));
		byte[] keyBytes = new byte[keySize];
		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM_AES);

		Cipher cipher = Cipher.getInstance(transformation.getName());
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] encrypted = doFinal(transformation, original, cipher);

		byte[] ivAndCipherText = new byte[ivSize + encrypted.length];
		System.arraycopy(iv, 0, ivAndCipherText, 0, ivSize);
		System.arraycopy(encrypted, 0, ivAndCipherText, ivSize, encrypted.length);

		return ivAndCipherText;
	}

	/**
	 * Decrypt encrypted data
	 * 
	 * @param transformation         {@link Transformation} transformation for the
	 *                               cipher object
	 * @param ivAndCipherText        Byte array of IV value and cipher text
	 * @param ivSize                 IV size
	 * @param key                    AES symmetric key
	 * @param keySize                AES key size in byte (16, 24 or 32)
	 * @param messageDigestAlgorithm Algorithm for message digest (MG5, SHA-1 or
	 *                               SHA-256. Depend on keySize)
	 * @return Decrypted data
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String decrypt(Transformation transformation, byte[] ivAndCipherText, int ivSize, String key,
			int keySize, String messageDigestAlgorithm) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] iv = new byte[ivSize];
		System.arraycopy(ivAndCipherText, 0, iv, 0, iv.length);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		int encryptedSize = ivAndCipherText.length - ivSize;
		byte[] encryptedBytes = new byte[encryptedSize];
		System.arraycopy(ivAndCipherText, ivSize, encryptedBytes, 0, encryptedSize);

		byte[] keyBytes = new byte[keySize];
		MessageDigest md = MessageDigest.getInstance(messageDigestAlgorithm);
		md.update(key.getBytes());
		System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM_AES);

		Cipher cipherDecrypt = Cipher.getInstance(transformation.getName());
		cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);
		String plainText = new String(decrypted);

		if (Transformation.AES_CBC_NOPADDING == transformation) {
			plainText = plainText.replaceAll(String.valueOf((char) 0), "");
		}

		return plainText;
	}

	private static byte[] doFinal(Transformation transformation, byte[] original, Cipher cipher)
			throws IllegalBlockSizeException, BadPaddingException {
		switch (transformation) {
		case AES_CBC_NOPADDING:
			return cipher.doFinal(Arrays.copyOf(original,
					((original.length / BLOCK_SIZE_IN_BYTE) + (original.length % BLOCK_SIZE_IN_BYTE))
							* BLOCK_SIZE_IN_BYTE));
		case AES_CBC_PKCS5PADDING:
			return cipher.doFinal(original);
		default:
			throw new RuntimeException(
					String.format("Transformation %s has not been implemented", transformation.name()));
		}
	}

}
