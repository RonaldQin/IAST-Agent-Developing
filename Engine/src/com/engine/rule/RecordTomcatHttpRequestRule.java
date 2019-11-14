package com.engine.rule;

import com.engine.bean.HttpRequestHelperStack;
import com.engine.bean.HttpRequestInfo;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

public class RecordTomcatHttpRequestRule extends AbstractRule {

	private RecordTomcatHttpRequestRule(ClassLoader loader, byte[] classfileBuffer, String className) {
		super(loader, classfileBuffer, className);
	}

	public static RecordTomcatHttpRequestRule instance = null;

	public static RecordTomcatHttpRequestRule getInstance(ClassLoader loader, byte[] classfileBuffer,
			String className) {
		if (instance == null) {
			synchronized (RecordTomcatHttpRequestRule.class) {
				if (instance == null) {
					instance = new RecordTomcatHttpRequestRule(loader, classfileBuffer, className);
				}
			}
		}
		return instance;
	}

	public static RecordTomcatHttpRequestRule getInstance() {
		if (instance == null) {
			return null;
		}
		return instance;
	}

	public String insert_InitTomcatHttpRequestInfo() {
		pool.importPackage("java.util.UUID");
		pool.importPackage("java.util.Enumeration");
		pool.importPackage("java.util.Map");
		pool.importPackage("java.util.HashMap");
		pool.importPackage(HttpRequestInfo.class.getCanonicalName());
		pool.importPackage(HttpRequestHelperStack.class.getCanonicalName());
		pool.importPackage("java.io.ByteArrayOutputStream");
		pool.importPackage("java.io.ByteArrayInputStream");
		pool.importPackage("org.apache.tomcat.util.http.fileupload.IOUtils");

		StringBuffer code_buffer = new StringBuffer("");
		try {
			dealMethod.addLocalVariable("httpRequestInfo", pool.get(HttpRequestInfo.class.getCanonicalName()));
			code_buffer.append("httpRequestInfo = new HttpRequestInfo();");
			code_buffer.append("httpRequestInfo.setUuid(UUID.randomUUID().toString().replaceAll(\"-\", \"\"));");
			code_buffer.append("httpRequestInfo.setMethod($2.getMethod());");
			code_buffer.append("httpRequestInfo.setUrl($2.getRequestURL().toString());");
			code_buffer.append("httpRequestInfo.setRemoteAddr($2.getRemoteAddr());");
			code_buffer.append("httpRequestInfo.setReferer($2.getHeader(\"referer\"));");

			dealMethod.addLocalVariable("headerMap", pool.get("java.util.Map"));
			dealMethod.addLocalVariable("next", pool.get("java.lang.String"));
			dealMethod.addLocalVariable("header_names", pool.get("java.util.Enumeration"));
			code_buffer.append("headerMap = new HashMap();");
			code_buffer.append("header_names = $2.getHeaderNames();");

			code_buffer.append("while (header_names.hasMoreElements()) {");
			code_buffer.append("	next = (String) header_names.nextElement();");
			code_buffer.append("	headerMap.put(next, $2.getHeader(next));");
			code_buffer.append("}");

			dealMethod.addLocalVariable("content_type", pool.get("java.lang.String"));
			code_buffer.append("content_type = $2.getContentType();");

			dealMethod.addLocalVariable("outputStream", pool.get("java.io.ByteArrayOutputStream"));
			dealMethod.addLocalVariable("byteArray", pool.get("byte[]"));
			dealMethod.addLocalVariable("inputStreamClone", pool.get("java.io.ByteArrayInputStream"));
			dealMethod.addLocalVariable("buffer", pool.get("byte[]"));
			dealMethod.addLocalVariable("len", CtClass.intType);
			dealMethod.addLocalVariable("body", pool.get("java.lang.StringBuffer"));

			code_buffer.append(
					"if (content_type != null && content_type.startsWith(\"multipart/form-data\") && httpRequestInfo.getMethod().equalsIgnoreCase(\"post\")) {");

			code_buffer.append("try {");
			code_buffer.append("	outputStream = new ByteArrayOutputStream();");
			code_buffer.append("	IOUtils.copy($2.getInputStream(), outputStream);");
			code_buffer.append("	byteArray = outputStream.toByteArray();");
			code_buffer.append("	inputStreamClone = new ByteArrayInputStream(byteArray);");
			code_buffer.append(" buffer = new byte[1024];");
			code_buffer.append("	body = new StringBuffer(\"\");");
			code_buffer.append("	while ((len = inputStreamClone.read(buffer)) != -1) {");
			code_buffer.append("		body.append(new String(byteArray));");
			code_buffer.append("	}");
			code_buffer.append("	httpRequestInfo.setBody(body.toString());");
			code_buffer.append("} catch (Exception e) { e.printStackTrace(); }");
			code_buffer.append("}");

			code_buffer.append("httpRequestInfo.setHeader(headerMap);");
			code_buffer.append("HttpRequestHelperStack.push(httpRequestInfo);"); // 将记录的请求信息压栈

		} catch (Exception e) {
			e.printStackTrace();
		}
		return code_buffer.toString();
	}

	public String insert_DumpHttpRequestInfo() {
		pool.importPackage("java.util.UUID");
		pool.importPackage("java.util.Enumeration");
		pool.importPackage("java.util.Map");
		pool.importPackage("java.util.HashMap");
		pool.importPackage(HttpRequestInfo.class.getCanonicalName());
		pool.importPackage(HttpRequestHelperStack.class.getCanonicalName());
		pool.importPackage("java.io.ByteArrayOutputStream");
		pool.importPackage("java.io.ByteArrayInputStream");
		pool.importPackage("org.apache.tomcat.util.http.fileupload.IOUtils");

		StringBuffer code_buffer = new StringBuffer("");
		try {
			dealMethod.addLocalVariable("httpRequestInfo", pool.get(HttpRequestInfo.class.getCanonicalName()));
			code_buffer.append("httpRequestInfo = null;");
			code_buffer.append("httpRequestInfo = HttpRequestHelperStack.pop();");
			code_buffer.append("if (httpRequestInfo != null) {");
			code_buffer.append(
					"	System.out.println(\"插桩获取的Tomcat HTTP 请求头部信息： \" + httpRequestInfo.toString());");
			code_buffer.append("}");
		} catch (CannotCompileException | NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code_buffer.toString();
	}

	public String insert_GetMultipartHttpPostParameters() { // 读取 HTTP POST 提交的二进制数据
		pool.importPackage("java.util.Enumeration");
		pool.importPackage("java.util.Map");
		pool.importPackage("java.util.HashMap");
		pool.importPackage(HttpRequestInfo.class.getCanonicalName());
		pool.importPackage(HttpRequestHelperStack.class.getCanonicalName());

		StringBuffer code_buffer = new StringBuffer("");
		try {
			dealMethod.addLocalVariable("httpRequestInfo", pool.get(HttpRequestInfo.class.getCanonicalName()));
			dealMethod.addLocalVariable("parameterNames", pool.get("java.util.Enumeration"));
			code_buffer.append("httpRequestInfo = HttpRequestHelperStack.peek();");
			code_buffer.append("if (!(httpRequestInfo != null "
					+ "&& httpRequestInfo.getHeader().get(\"content-type\") != null "
					+ "&& ((String) httpRequestInfo.getHeader().get(\"content-type\")).startsWith(\"multipart/form-data\"))) {");
			dealMethod.addLocalVariable("params", pool.get("java.util.Map"));
			code_buffer.append("	params = new HashMap();");
			code_buffer.append("	parameterNames = $0.getCoyoteRequest().getParameters().getParameterNames();");
			code_buffer.append("	while (parameterNames.hasMoreElements()) {");
			dealMethod.addLocalVariable("next", pool.get("java.lang.String"));
			code_buffer.append("		next = (String) parameterNames.nextElement();");
			code_buffer.append("		params.put(next, $0.getCoyoteRequest().getParameters().getParameter(next));");
			code_buffer.append("	}");
			code_buffer.append("	httpRequestInfo.setParameters(params);");
			code_buffer.append("}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code_buffer.toString();
	}

	@Override
	public String insert_addChecker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transmit_check_params() {
		// TODO Auto-generated method stub
		return null;
	}

}
