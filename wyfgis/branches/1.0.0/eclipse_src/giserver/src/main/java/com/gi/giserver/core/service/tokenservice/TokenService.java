package com.gi.giserver.core.service.tokenservice;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.gi.giserver.core.config.ConfigManager;
import com.gi.giserver.core.i18n.ResourceManager;

/**
 * @author Wu Yongfeng
 * 
 */
public class TokenService {
	static Logger logger = Logger.getLogger(TokenService.class);

	public final static String TOKEN_INVALID_TIP = "Token is not verified or expired.";
	
	private static Timer timer = null;
	private static SecretKey key = null;
	private static Cipher cipher = null;

	static class ClearTask extends TimerTask {
		public void run() {
			try {
				TokensManager.clearExpiredTokens();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void startClearTimer() {
		if (timer != null) {
			timer.cancel();
		} else {
			timer = new Timer();
		}

		long period = ConfigManager.getServerConfig().getTokenClearInterval() * 60000;
		timer.schedule(new ClearTask(), 0, period);
	}

	public static void stopClearTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

	public static SecretKey getKey() throws NoSuchAlgorithmException {
		if (key == null) {
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56);
			key = keyGen.generateKey();
		}

		return key;
	}

	public static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		if (cipher == null) {
			cipher = Cipher.getInstance("DES");
		}

		return cipher;
	}

	public static String getToken(String username, String password, String clientId, int expireMinutes) {
		String result = null;

		try {
			HashMap<String, String> users = ConfigManager.getTokenConfig().getUsers();
			if (users.containsKey(username)) {
				if (users.get(username).equals(password)) {
					String tokenMark = ConfigManager.getServerConfig().getTokenMark();
					String tokenId = tokenMark + "&" + username + "=" + password + "@" + clientId;
					Date dateNow = new Date();

					int minExpire = ConfigManager.getServerConfig().getTokenMinExpire();
					int maxExpire = ConfigManager.getServerConfig().getTokenMaxExpire();
					if (expireMinutes < 0) {
						expireMinutes = maxExpire;
					}
					if (expireMinutes < minExpire) {
						expireMinutes = minExpire;
					}
					if (expireMinutes > maxExpire) {
						expireMinutes = maxExpire;
					}
					long expire = dateNow.getTime() + expireMinutes * 60000;
					Date dateExpire = new Date(expire);

					String tokenSource = tokenId + ":" + dateNow.getTime();
					byte[] bytes = tokenSource.getBytes();

					SecretKey key = getKey();
					Cipher cipher = getCipher();
					cipher.init(Cipher.ENCRYPT_MODE, key);
					byte[] encryptBytes = cipher.doFinal(bytes);
					BASE64Encoder encoder = new BASE64Encoder();
					String token = encoder.encode(encryptBytes);

					Hashtable<String, Date> tokens = TokensManager.getTokens();
					tokens.put(token, dateExpire);

					result = token;
				}
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleTokenServiceLog()
					.getString("ERROR.GET_TOKEN"), ex);
		}

		return result;
	}

	public static boolean verifyToken(String token) {
		boolean result = false;

		try {
			Hashtable<String, Date> tokens = TokensManager.getTokens();
			if (token != null && tokens.containsKey(token)) {
				Date dateExpire = tokens.get(token);
				Date dateNow = new Date();
				if (dateNow.before(dateExpire)) {
					result = true;
				} else {
					tokens.remove(token);
				}
			}
		} catch (Exception ex) {
			logger.error(ResourceManager.getResourceBundleTokenServiceLog()
					.getString("ERROR.VERIFY_TOKEN"), ex);
		}

		return result;
	}

}
