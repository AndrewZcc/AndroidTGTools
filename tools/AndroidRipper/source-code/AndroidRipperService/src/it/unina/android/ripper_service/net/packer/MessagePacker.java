package it.unina.android.ripper_service.net.packer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

/**
 * Message Packer/UnPacker class
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class MessagePacker {

	/**
	 * Pack Map into a byte array
	 * 
	 * @param map Map to pack
	 * @return packed Map
	 */
	public static byte[] pack(Map map) {
		if (map != null) {
			try {
				JSONObject jsonObject = new JSONObject();

				for (Object k : map.keySet()) {
					String value = (String) map.get(k);
					jsonObject.put((String) k, value);
				}

				System.out.println(jsonObject.toString());
				
				return jsonObject.toString().getBytes();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * UnPack Map from byte array
	 * 
	 * @param b byte array
	 * @return UnPacked Map
	 */
	public static Map unpack(byte[] b) {
		if (b != null) {
			String s = new String(b);

			try {
				JSONObject jsonObject = new JSONObject(s);
				Map<String, Object> map = new HashMap();
				
				Iterator iterator = jsonObject.keys();
				while (iterator.hasNext()) {
					String key = (String)iterator.next();
					map.put(key, jsonObject.get(key));				
				}
				return map;

			} catch (Throwable t) {
				t.printStackTrace();
			}

		}

		return null;
	}
}
