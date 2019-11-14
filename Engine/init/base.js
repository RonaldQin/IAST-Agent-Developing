/**
 * 定义Utils模块，提供公共函数。
 */

var Rules = new Array(0); // 存储所有的检测规则
var insert_before = "insert_before"; // 标记在方法体前开始插桩
var insert_after = "insert_after"; // 标记在方法体后开始插桩
/**
 * 添加规则
 */
var addRule = function(rule) {
	if (rule.isRule == true) {
		Rules.push(rule);
	}
	if (rule.hasOwnProperty("checker")) {
		rule.checker = `${rule.checker}`;// .replace(/\r\n/g, "\\n").replace(/\n/g, "");//.replace("/\"/g", "\\""); // 去掉回车和换行
	}
	return JSON.stringify(Rules);
}

/**
 * 返回所有加载规则总数量
 */
var getRulesCount = function() {
	return Rules.length;
}