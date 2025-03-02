package com.softdev.system.config;


import com.softdev.system.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Object defaultExceptionHandler(HttpServletRequest req, Exception e) {
		e.printStackTrace();
		return ResponseUtil.fail(
				ResponseUtil.StatusCode.INTERNAL_ERROR,
				e.getMessage()
		);
	}
	
}
