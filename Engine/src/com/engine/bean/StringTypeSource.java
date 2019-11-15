package com.engine.bean;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StringTypeSource {

	public static Hashtable<String, StringTypeSource> table = new Hashtable<String, StringTypeSource>();

	public static String getMatchedSource(String value) {
		if (table != null && !table.isEmpty()) {
			for (Map.Entry<String, StringTypeSource> entry : table.entrySet()) {
				StringTypeSource source = entry.getValue();
				if (source.getValue().contains(value) || source.mayBeSource(value)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	private String uuid;
	private String value;
	private List<String> transmited_values;

	public StringTypeSource() {
	}

	public StringTypeSource(String value) {
		this.value = value;
		this.uuid = UUID.randomUUID().toString().toUpperCase();
		table.put(uuid, this);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getTransmited_values() {
		return transmited_values;
	}

	public void setTransmited_values(List<String> transmited_values) {
		this.transmited_values = transmited_values;
	}

	public List<String> add_transmited_value(String value) { // 在字符串传递过程中在传递的方法最后调用
		this.transmited_values.add(value);
		return this.transmited_values;
	}

	public static boolean mayBeSource(String value) {
		for (Map.Entry<String, StringTypeSource> entry : table.entrySet()) {
			List<String> transmited_values = entry.getValue().getTransmited_values();
			for (String v : transmited_values) {
				if (v.contains(value)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Source [uuid=" + uuid + ", value=" + value + ", transmited_values=" + transmited_values + "]";
	}

}
