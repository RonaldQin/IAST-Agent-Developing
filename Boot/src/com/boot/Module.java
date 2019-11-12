package com.boot;

import java.lang.instrument.Instrumentation;

/**
 * 每个子模块入口都需要继承的模块 模块入口类 配置在子模块 jar 包的 MANIFEST 配置中
 * 
 * @author lace
 *
 */
public interface Module {

	void start(Instrumentation inst) throws Throwable;

}
