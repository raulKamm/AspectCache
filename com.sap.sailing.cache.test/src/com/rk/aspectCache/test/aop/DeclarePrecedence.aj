package com.sap.sailing.cache.test.aop;

public aspect DeclarePrecedence {

	declare precedence: (@TestAspect *), *;
}
