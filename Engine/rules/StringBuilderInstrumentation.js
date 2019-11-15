/**
 * TODO: 修改插桩时定位方法的方法签名
 */
addRule({
	"isRule" : true,
	"id" : "161EDAFF-6D5E-4985-80F0-6ED40BFBF1D0",
	"className" : "com.engine.rule.StringBuilderInstrumentationRule",
	"transform" : {
		"java.lang.StringBuilder" : {
//			"StringBuilder" : {}, // 构造方法的返回值插桩检测？？？
			"append" : {
//				"insert_transmitAllTaint" : insert_after // 所有参数只要有一个是污点则方法的返回值就会被污染
			},
			"toString" : {
//				"insert_transmitThisTaint" : insert_after // 如果this是被污染的然么方法的返回值也会被污染
			}
		}
	}
});