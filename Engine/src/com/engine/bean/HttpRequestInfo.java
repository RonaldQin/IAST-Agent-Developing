package com.engine.bean;

import java.util.Map;

/**
 * 保存 Tomcat HTTP 请求信息类。
 * 
 * @author lace
 *
 */
public class HttpRequestInfo {

	String uuid;
	String method;
	String url;
	String remoteAddr;
	String referer;
	Map<String, String> header;
	String body; // POST 二进制数据
	Map<String, String> parameters; // GET

	public HttpRequestInfo() {
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "HttpRequestInfo [uuid=" + uuid + ", method=" + method + ", url=" + url + ", remoteAddr=" + remoteAddr
				+ ", referer=" + referer + ", header=" + header + ", body=" + body + ", parameters=" + parameters + "]";
	}

}
