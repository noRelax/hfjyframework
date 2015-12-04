package com.hfjy.framework.common.util;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hfjy.framework.exception.DateJsonHandlerException;
import com.hfjy.framework.logging.LoggerFactory;

public class JsonUtil {
	private static final Map<String, Gson> gsonMap = new HashMap<>();
	private static final String DEFAULT = "DEFAULT";
	private static final String ALL = "ALL";
	private static final String SHOW = "SHOW";

	public static GsonBuilder getGsonBuilder() {
		GsonBuilder gsonBuilder = newGsonBuilder();
		DateJsonHandler dateJsonHandler = new DateJsonHandler();
		gsonBuilder.registerTypeAdapter(Date.class, dateJsonHandler);
		gsonBuilder.registerTypeAdapter(Date.class, dateJsonHandler);
		return gsonBuilder;
	}

	public static Gson getGson() {
		Gson gson = gsonMap.get(DEFAULT);
		if (gson == null) {
			gson = newGson();
			gsonMap.put(DEFAULT, gson);
		}
		return gson;
	}

	public static String toJson(Object object) {
		return toJson(getGson(), object);
	}

	public static String showJson(Object object) {
		Gson gson = gsonMap.get(SHOW);
		if (gson == null) {
			gson = getGsonBuilder().setPrettyPrinting().create();
			gsonMap.put(SHOW, gson);
		}
		return toJson(gson, object);
	}

	public static String toJsonAll(Object object) {
		Gson gson = gsonMap.get(ALL);
		if (gson == null) {
			gson = getGsonBuilder().serializeNulls().create();
			gsonMap.put(ALL, gson);
		}
		return toJson(gson, object);
	}

	public static String toJson(Object object, String dateFormat) {
		Gson gson = gsonMap.get(dateFormat);
		if (gson == null) {
			gson = newGsonBuilder().setDateFormat(dateFormat).create();
			gsonMap.put(dateFormat, gson);
		}
		return toJson(gson, object);
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		return toObject(getGson(), json, clazz);
	}

	public static <T> T toObject(JsonElement json, Class<T> clazz) {
		return toObject(getGson(), json, clazz);
	}

	public static <T> T toObject(String json, Class<T> clazz, String dateFormat) {
		Gson gson = gsonMap.get(dateFormat);
		if (gson == null) {
			gson = newGsonBuilder().setDateFormat(dateFormat).create();
			gsonMap.put(dateFormat, gson);
		}
		return toObject(gson, json, clazz);
	}

	public static JsonElement toJsonElement(Object object) {
		return toTree(getGson(), object);
	}

	public static JsonObject toJsonObject(Object object) {
		return getJsonObject(toJsonElement(object));
	}

	public static JsonObject toJsonObject(String json) {
		return getJsonObject(toObject(json, JsonObject.class));
	}

	public static JsonArray toJsonArray(Object object) {
		return getJsonArray(toJsonElement(object));
	}

	public static JsonArray toJsonArray(String json) {
		return getJsonArray(toObject(json, JsonArray.class));
	}

	public static Map<String, String> toStringMap(String json) {
		if (StringUtils.isNotEmpty(json)) {
			if (json.startsWith("[")) {
				return toStringMap(JsonUtil.toJsonArray(json));
			} else if (json.startsWith("{")) {
				return toStringMap(JsonUtil.toJsonObject(json));
			}
		}
		return new HashMap<>();
	}

	public static Map<String, String> toStringMap(Object object) {
		Map<String, String> stringMap = new HashMap<>();
		if (object != null) {
			toStringMap("root", JsonUtil.toJsonElement(object), stringMap);
		}
		return stringMap;
	}

	public static Map<String, String> toStringMap(JsonObject jsonObject) {
		Map<String, String> stringMap = new HashMap<>();
		if (jsonObject != null) {
			toStringMap("root", jsonObject, stringMap);
		}
		return stringMap;
	}

	public static Map<String, String> toStringMap(JsonArray jsonArray) {
		Map<String, String> stringMap = new HashMap<>();
		if (jsonArray != null) {
			toStringMap("root", jsonArray, stringMap);
		}
		return stringMap;
	}

	public static String findData(String json, String jsonPath) {
		if (StringUtils.isNotEmpty(json)) {
			if ("root".equals(jsonPath)) {
				return json;
			} else if (json.startsWith("[")) {
				return findData(JsonUtil.toJsonArray(json), jsonPath);
			} else if (json.startsWith("{")) {
				return findData(JsonUtil.toJsonObject(json), jsonPath);
			}
		}
		return null;
	}

	public static String findData(Object object, String jsonPath) {
		if (object != null) {
			if ("root".equals(jsonPath)) {
				return JsonUtil.toJson(object);
			} else {
				return findData("root", JsonUtil.toJsonElement(object), jsonPath);
			}
		}
		return null;
	}

	public static String findData(JsonObject jsonObject, String jsonPath) {
		if (jsonObject != null) {
			if ("root".equals(jsonPath)) {
				return jsonObject.toString();
			} else {
				return findData("root", jsonObject, jsonPath);
			}
		}
		return null;
	}

	public static String findData(JsonArray jsonArray, String jsonPath) {
		if (jsonArray != null) {
			if ("root".equals(jsonPath)) {
				return jsonArray.toString();
			} else {
				return findData("root", jsonArray, jsonPath);
			}
		}
		return null;
	}

	private static GsonBuilder newGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		return gsonBuilder;
	}

	private static Gson newGson() {
		return getGsonBuilder().create();
	}

	private static JsonElement toTree(final Gson gson, Object object) {
		return gson.toJsonTree(object);
	}

	private static <T> T toObject(final Gson gson, String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	private static <T> T toObject(final Gson gson, JsonElement jsonElement, Class<T> clazz) {
		return gson.fromJson(jsonElement, clazz);
	}

	private static String toJson(final Gson gson, Object object) {
		return gson.toJson(object);
	}

	private static JsonObject getJsonObject(JsonElement element) {
		if (element.isJsonObject()) {
			return element.getAsJsonObject();
		} else {
			return null;
		}
	}

	private static JsonArray getJsonArray(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		} else {
			return null;
		}
	}

	private static void toStringMap(String key, JsonObject jsonObject, Map<String, String> stringMap) {
		Iterator<Entry<String, JsonElement>> jsonIterator = jsonObject.entrySet().iterator();
		while (jsonIterator.hasNext()) {
			Entry<String, JsonElement> entry = jsonIterator.next();
			toStringMap(StringUtils.unite(key, ".", entry.getKey()), entry.getValue(), stringMap);
		}
		stringMap.put(key, jsonObject.toString());
	}

	private static void toStringMap(String key, JsonArray jsonArray, Map<String, String> stringMap) {
		for (int i = 0; i < jsonArray.size(); i++) {
			toStringMap(StringUtils.unite(key, ".", i), jsonArray.get(i), stringMap);
		}
		stringMap.put(key, jsonArray.toString());
	}

	private static void toStringMap(String key, JsonElement jsonElement, Map<String, String> stringMap) {
		if (jsonElement.isJsonObject()) {
			toStringMap(key, jsonElement.getAsJsonObject(), stringMap);
		} else if (jsonElement.isJsonArray()) {
			toStringMap(key, jsonElement.getAsJsonArray(), stringMap);
		} else if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jp = jsonElement.getAsJsonPrimitive();
			stringMap.put(key, jp.getAsString());
		}
	}

	private static String findData(String key, JsonObject jsonObject, String jsonPath) {
		Iterator<Entry<String, JsonElement>> jsonIterator = jsonObject.entrySet().iterator();
		while (jsonIterator.hasNext()) {
			Entry<String, JsonElement> entry = jsonIterator.next();
			String nextKey = StringUtils.unite(key, ".", entry.getKey());
			if (jsonPath.startsWith(nextKey)) {
				return findData(nextKey, entry.getValue(), jsonPath);
			}
		}
		return null;
	}

	private static String findData(String key, JsonArray jsonArray, String jsonPath) {
		for (int i = 0; i < jsonArray.size(); i++) {
			String nextKey = StringUtils.unite(key, ".", i);
			if (jsonPath.startsWith(nextKey)) {
				return findData(nextKey, jsonArray.get(i), jsonPath);
			}
		}
		return null;
	}

	private static String findData(String key, JsonElement jsonElement, String jsonPath) {
		if (jsonElement.isJsonObject()) {
			if (key.equals(jsonPath)) {
				return jsonElement.toString();
			} else {
				return findData(key, jsonElement.getAsJsonObject(), jsonPath);
			}
		} else if (jsonElement.isJsonArray()) {
			if (key.equals(jsonPath)) {
				return jsonElement.toString();
			} else {
				return findData(key, jsonElement.getAsJsonArray(), jsonPath);
			}
		} else if (jsonElement.isJsonPrimitive()) {
			if (key.equals(jsonPath)) {
				return jsonElement.getAsJsonPrimitive().getAsString();
			}
		}
		return null;
	}
}

class DateJsonHandler implements JsonSerializer<Date>, JsonDeserializer<Date> {
	private static final Logger log = LoggerFactory.getLogger(DateJsonHandler.class);
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Date date = null;
		JsonPrimitive jp = json.getAsJsonPrimitive();
		if (jp.isNumber()) {
			date = new Date(jp.getAsLong());
		} else {
			try {
				date = df.parse(jp.getAsString());
			} catch (ParseException e) {
				DateJsonHandlerException error = new DateJsonHandlerException(jp, e);
				log.error(error.getMessage(), error);
			}
		}
		return date;
	}

	@Override
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(df.format(src));
	}
}