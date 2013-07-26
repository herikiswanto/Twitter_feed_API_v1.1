package me.kaidul.twitter7feed;

import java.io.InputStream;
import java.io.OutputStream;

public class CommonUtils {
	// twitter app-only oauth credentials
	final static String URL = "https://api.twitter.com/1.1/lists/statuses.json?slug=android-7-feed&owner_screen_name=Kaidul&count=50&include_entities=false&include_rts=false";
	final static String CONSUMER_KEY = "OdGwojyZwKk0P8jpY4FMjQ";
	final static String CONSUMER_SECRET = "gWBke4Au6ajYIw6tu0oMZGi9LHxfxrF2WC9o2XLM8";
	final static String BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAMRFSAAAAAAAQ%2BV3uw%2F07YQlBA%2BNOZPk%2BopAg%2Fg%3DklsQfMxL4tRrfWXGPJN8suTh0UvqRo8Ng7KKSJTXEg0";

	final static String KEY_USER = "user";
	final static String KEY_NAME = "name";
	final static String KEY_PROFILE_IMG = "profile_image_url";
	final static String KEY_TXT = "text";
	final static String KEY_DATE = "created_at";
	final static String KEY_SCREEN_NAME = "screen_name";

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
}
