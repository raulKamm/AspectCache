package com.rk.aspectCache.test.aop;

public aspect DeclarePrecedence {

	declare precedence: (@TestAspect *), *;
}
