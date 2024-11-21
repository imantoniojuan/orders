package com.anthony.orders.controllers;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    protected void prepare(Object response) {
		try {
			Class<?> responseClass = response.getClass();
			Method setStatus = responseClass.getMethod("setStatusCode", Integer.class);
			setStatus.invoke(response, 400);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	protected void conclude(Object response) {
		try {
			Class<?> responseClass = response.getClass();
			Method setStatus = responseClass.getMethod("setStatusCode", Integer.class);
			setStatus.invoke(response, 200);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}
}
