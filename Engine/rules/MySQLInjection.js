/**
 * 规则：MySQL 注入
 */

//import {insert_before, insert_after, Obj2JSON} from "./Utils.js";
//load("/home/lace/Documents/workspace-sts-3.9.10.RELEASE/IAST-v8-parser/rules/base.js");

addRule({
		"isRule": true,
		"id": "26B733E7-4139-46A5-9126-222D11810D75",
		"className": "com.engine.rule.MySQLInjectionRule",
		"transform" : {
			"com.mysql.jdbc.StatementImpl" : {
				"executeQuery" : {
					"insert_GetExecutedSQL" : insert_before,
					"insert_addChecker" : insert_after /* 添加 Checker 检测逻辑 */
				}
			},
//			"com.mariadb.jdbc.MariaDbStatement" : {
//				"executeQuery" : {
//					"insert_GetExecutedSQL" : insert_before
//				}
//			},
//			"com.mysql.jdbc.jdbc2.optional.StatementWrapper" : {
//				"executeQuery" : {
//					"insert_GetExecutedSQL" : insert_before
//				}
//			},
//			"org.apache.tomcat.dbcp.dbcp.DelegatingStatement" : {
//				"executeQuery" : {
//					"insert_GetExecutedSQL" : insert_before
//				}
//			},
//			"org.mariadb.jdbc.MariaDbStatement" : {
//				"executeQuery" : {
//					"insert_GetExecutedSQL" : insert_before
//				}
//			}
		},
		"checker" : function(sql) {
			/*
			 * 测试数据：
			 * SELECT * FROM vuln WHERE id = 1 or 1=1 --
			 * SELECT * FROM vuln WHERE id = 1
			 * 
			 * "\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)"
			 */
			print("--> exec sql: " + sql); // 执行的sql
			var rule = "/(\%27)|(’)|(--)|(\%23)|(#)/ix";
			if (sql.search(rule) != -1) {
				return "存在！";
			}
			return "不存在。";
		}
})
