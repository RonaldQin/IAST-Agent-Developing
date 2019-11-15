/**
 * 规则：插桩获取Tomcat服务器接受的HTTP请求头部信息 （后续这条规则可以作为初始化规则加载）
 */

//import {insert_before, insert_after, Obj2JSON} from "./Utils.js";
//load("/home/lace/Documents/workspace-sts-3.9.10.RELEASE/IAST-v8-parser/rules/base.js");

addRule({
		"isRule": true,
		"id": "EAFED52D-481B-4603-AAC9-7B7FB028AA19",
		"className": "com.engine.rule.RecordTomcatHttpRequestRule",
		"transform" : {
			"org.apache.catalina.connector.CoyoteAdapter" : {
				"postParseRequest" : {
					"insert_InitTomcatHttpRequestInfo" : insert_after
				},
				"service" : {
					"insert_DumpHttpRequestInfo" : insert_after
				}
			},
			"org.apache.catalina.connector.Request" : {
				"parseParameters" : {
					"insert_GetMultipartHttpPostParameters" : insert_after
				},
//				"getParameter" : {
//					"insert_LableSource" : insert_after
//				}
			},
		},
		"check" : function() {
			
		}
})
