package com.hfjy.framework.net.http.processor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hfjy.framework.checkable.CheckableManager;
import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.ConvertUtil;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.exception.CheckableDataException;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.HttpBaseController;
import com.hfjy.framework.net.http.annotation.Mark;
import com.hfjy.framework.net.http.entity.ControllerConfig;
import com.hfjy.framework.net.http.entity.ControllerMapping;
import com.hfjy.framework.net.http.entity.Model;
import com.hfjy.framework.net.http.entity.MultipartFile;
import com.hfjy.framework.net.http.entity.TypeProcessor;
import com.hfjy.framework.net.http.util.ServletUril;

public class ParameterProcessor {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ParameterProcessor.class);
	private final Map<Class<?>, TypeProcessor<?>> typeProcessorMap = new HashMap<>();
	private final Map<Class<?>, Map<String, Field>> parameterFieldMap = new HashMap<>();

	public ParameterProcessor(ControllerConfig config) {
		TypeProcessor<?>[] typeProcessors = config.getTypeProcessor();
		if (typeProcessors != null) {
			for (int i = 0; i < typeProcessors.length; i++) {
				Class<?> key = ClassUtil.getSingleGenerics(typeProcessors[i]);
				TypeProcessor<?> value = typeProcessors[i];
				typeProcessorMap.put(key, value);
			}
		}
	}

	public Object[] getParameters(HttpServletRequest httpRequest, ControllerMapping controllerMapping) throws IOException, ServletException, CheckableDataException {
		Class<?>[] parametersClass = controllerMapping.getMethodParameters(HttpBaseController.getControllerContext().getExecuteMethod());
		if (parametersClass == null) {
			return null;
		}
		Map<String, String[]> requestParameters = getRequestParameters(httpRequest);
		Annotation[][] annotations = HttpBaseController.getControllerContext().getExecuteMethod().getParameterAnnotations();
		Object[] useParameter = new Object[parametersClass.length];
		for (int i = 0; i < parametersClass.length; i++) {
			Annotation tmpAnnotation = ClassUtil.getAnnotation(annotations[i], Mark.class);
			TypeProcessor<?> typeProcessor = typeProcessorMap.get(parametersClass[i]);
			if (typeProcessor != null) {
				String mark = tmpAnnotation != null ? ((Mark) tmpAnnotation).value() : null;
				useParameter[i] = typeProcessor.handle(mark, httpRequest);
			} else {
				Mark mark = tmpAnnotation == null ? null : (Mark) tmpAnnotation;
				useParameter[i] = getParameter(parametersClass[i], requestParameters, mark == null ? null : new MarkValue(mark));
			}
		}
		CheckableManager.checkObjects(useParameter, parametersClass, annotations);
		return useParameter;
	}

	private Map<String, String[]> getRequestParameters(HttpServletRequest httpRequest) throws IOException, ServletException {
		if (ServletUril.isMultiPart(httpRequest)) {
			Map<String, String[]> requestParameters = new HashMap<>();
			Iterator<Part> parts = httpRequest.getParts().iterator();
			while (parts.hasNext()) {
				Part part = parts.next();
				String filename = ServletUril.getPartFileName(part);
				if (StringUtils.isNotEmpty(filename)) {
					HttpBaseController.getControllerContext().putMultipartFile(filename, new MultipartFile(part));
				} else {
					InputStream is = part.getInputStream();
					byte[] bf = new byte[is.available()];
					is.read(bf);
					if (requestParameters.get(part.getName()) == null) {
						requestParameters.put(part.getName(), new String[] { new String(bf) });
					} else {
						String[] tmp = requestParameters.get(part.getName());
						String[] newArray = Arrays.copyOf(tmp, tmp.length + 1);
						newArray[tmp.length] = new String(bf);
						requestParameters.put(part.getName(), newArray);
					}
				}
			}
			return requestParameters;
		} else {
			return httpRequest.getParameterMap();
		}
	}

	private Object getParameter(Class<?> classInfo, Map<String, String[]> requestParameters, MarkValue mark) {
		Object object = null;
		boolean isJavaType = ClassUtil.isJavaType(classInfo);
		try {
			if (mark != null) {
				if (mark.isJson) {
					String jsonPath = mark.value;
					String requestKey = mark.value;
					if (jsonPath.indexOf(".") > -1) {
						requestKey = jsonPath.substring(0, jsonPath.indexOf("."));
						jsonPath = jsonPath.replaceFirst(requestKey, "root");
					} else {
						jsonPath = "root";
					}
					String json = "";
					if (requestParameters.get(requestKey) == null) {
						Map<String, JsonElement> jsonElementMap = new HashMap<>();
						Iterator<String> iterator = requestParameters.keySet().iterator();
						while (iterator.hasNext()) {
							String key = iterator.next();
							String value = StringUtils.unite((Object[]) requestParameters.get(key));
							key = key.replaceAll("\\[", ".").replaceAll("\\]", ".").replaceAll("[.][.]", ".");
							if (key.endsWith(".")) {
								key = key.substring(0, key.length() - 1);
							}
							mapToJson(key, value, jsonElementMap);
						}
						json = JsonUtil.toJson(jsonElementMap.get(requestKey));
					} else {
						json = requestParameters.get(requestKey)[0];
					}
					object = JsonUtil.toObject(JsonUtil.findData(json, jsonPath), classInfo);
				} else if (isJavaType) {
					object = getJavaParameter(mark, classInfo, requestParameters);
				} else {
					object = getNotJavaParameter(mark, classInfo, requestParameters);
				}
			} else if (!isJavaType) {
				object = getNotJavaParameter(null, classInfo, requestParameters);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return object;
	}

	private Object getJavaParameter(MarkValue mark, Class<?> classInfo, Map<String, String[]> requestParameters) {
		Object object = null;
		if (classInfo.isArray()) {
			String[] dataArray = requestParameters.get(mark.value);
			if (dataArray == null) {
				dataArray = requestParameters.get(StringUtils.unite(mark.value, "[]"));
			}
			if (dataArray != null) {
				if (classInfo == String[].class) {
					object = dataArray;
				} else {
					object = ConvertUtil.toIts(dataArray, classInfo.getComponentType());
				}
			}
		} else {
			String[] values = requestParameters.get(mark.value);
			String tmpValue = values != null && values.length > 0 ? values[values.length - 1] : null;
			if (tmpValue != null) {
				object = ConvertUtil.toIt(tmpValue, classInfo);
			}
		}
		return object;
	}

	private Object getNotJavaParameter(MarkValue mark, Class<?> classInfo, Map<String, String[]> requestParameters) throws InstantiationException, IllegalAccessException {
		Object object = null;
		if (classInfo == HttpServletRequest.class) {
			object = HttpBaseController.getControllerContext().getRequest();
		} else if (classInfo == HttpServletResponse.class) {
			object = HttpBaseController.getControllerContext().getResponse();
		} else if (classInfo == HttpSession.class) {
			object = HttpBaseController.getControllerContext().getRequest().getSession();
		} else if (classInfo == Model.class) {
			object = HttpBaseController.getControllerContext().getModel();
		} else if (classInfo == MultipartFile.class) {
			object = HttpBaseController.getControllerContext().getMultipartFile(mark == null ? null : mark.value);
		} else if (classInfo == MultipartFile[].class) {
			object = HttpBaseController.getControllerContext().getMultipartFiles();
		} else {
			Map<String, Field> fields = null;
			if (parameterFieldMap.keySet().contains(classInfo)) {
				fields = parameterFieldMap.get(classInfo);
			} else {
				fields = ClassUtil.getClassAllFields(classInfo);
				parameterFieldMap.put(classInfo, fields);
			}
			if (fields != null) {
				Iterator<String> keys = fields.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					Field field = fields.get(key);
					if (ClassUtil.isJavaType(field.getType()) || field.getAnnotation(Mark.class) != null) {
						MarkValue tmpMark = new MarkValue(field.getAnnotation(Mark.class));
						if (tmpMark.value == null) {
							tmpMark.value = field.getName();
						}
						Object vaule = getParameter(field.getType(), requestParameters, tmpMark);
						if (vaule != null) {
							if (object == null) {
								object = classInfo.newInstance();
							}
							field.setAccessible(true);
							field.set(object, vaule);
							field.setAccessible(false);
						}
					}
				}
			}
		}
		return object;
	}

	private void mapToJson(String path, String value, Map<String, JsonElement> jsonElementMap) {
		StringBuilder sb = new StringBuilder();
		String[] paths = path.split("[.]");
		for (int i = 0; i < paths.length; i++) {
			JsonElement parentJsonElement;
			if (paths.length - 1 == 0) {
				jsonElementMap.put(paths[i], new JsonPrimitive(value));
				break;
			} else {
				parentJsonElement = jsonElementMap.get(sb.toString());
			}

			if (i == paths.length - 1) {
				if (parentJsonElement.isJsonArray()) {
					((JsonArray) parentJsonElement).add(new JsonPrimitive(value));
				} else {
					((JsonObject) parentJsonElement).addProperty(paths[i], value);
				}
			} else {
				sb.append(paths[i]);
				JsonElement jsonElement = jsonElementMap.get(sb.toString());
				if (jsonElement == null) {
					if (isJsonArray(paths[i + 1])) {
						jsonElement = new JsonArray();
					} else {
						jsonElement = new JsonObject();
					}
					jsonElementMap.put(sb.toString(), jsonElement);
					if (parentJsonElement != null) {
						if (parentJsonElement.isJsonArray()) {
							((JsonArray) parentJsonElement).add(jsonElement);
						} else {
							((JsonObject) parentJsonElement).add(paths[i], jsonElement);
						}
					}
				}
			}
		}
	}

	private boolean isJsonArray(String path) {
		try {
			Integer.valueOf(path);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	class MarkValue {
		private String value;
		private boolean isJson;

		public MarkValue(Mark mark) {
			if (mark != null) {
				this.value = mark.value();
				this.isJson = mark.isJson();
			}
		}
	}
}
