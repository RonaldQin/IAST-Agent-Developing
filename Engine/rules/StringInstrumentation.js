/**
 * TODO: 修改插桩时定位方法的方法签名
 */
addRule({
	"isRule" : true,
	"id" : "E16454B1-E3C1-4FAB-9173-6FB0A7E646C6",
	"className" : "com.engine.rule.StringInstrumentationRule",
	"transform" : {
		"java.lang.String" : {
			"valueOf" : {
//				"insert_transmitAllTaint" : insert_after
			},
//			"String" : {}  // 构造方法的插桩方式有不同！！！
		}
	}
});