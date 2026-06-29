package com.examen.gamestore.support;

public final class ApiTestJson {

	private ApiTestJson() {
	}

	public static String field(String json, String name) {
		String marker = "\"" + name + "\":\"";
		int start = json.indexOf(marker);
		if (start < 0) {
			throw new IllegalStateException("Field not found: " + name);
		}
		start += marker.length();
		int end = json.indexOf('"', start);
		return json.substring(start, end);
	}

	public static String firstCartItemId(String json) {
		String marker = "\"items\":[{\"id\":\"";
		int start = json.indexOf(marker);
		if (start < 0) {
			throw new IllegalStateException("Cart item id not found");
		}
		start += marker.length();
		int end = json.indexOf('"', start);
		return json.substring(start, end);
	}
}
