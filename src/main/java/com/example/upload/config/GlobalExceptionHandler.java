package com.example.upload.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author wujingjing
 * @date 2020/8/4 14:32
 * @description 全局异常处理
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 其他错误
	 *
	 * @param ex 异常类
	 * @return 统一返回JSON格式
     *
	 */
	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> exception(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}


}
