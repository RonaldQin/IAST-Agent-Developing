/**
 * 
 */
addRule({
	"isRule" : true,
	"id" : "82553BFB-36C2-4158-8213-5A51A02D02A2",
	"className" : "com.engine.rule.GetHttpRequestParameterSourceRule",
	"transform" : {
		"org.apache.catalina.connector.RequestFacade" : {
			"getParameter" : {
				"insert_LabelSource" : insert_after,
			}
		}
	},
});

