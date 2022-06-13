package com.ginfon.core.advice;

import com.ginfon.core.exception.LogisticsGpException;
import com.ginfon.core.model.Result;
import com.ginfon.core.utils.ResultUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 捕获异常统一处理
 *
 * @author James
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 全局异常捕捉处理
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ModelAndView exceptionHandler(Exception ex) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("code", "-1");
		mv.addObject("msg", ex.getMessage());
		ex.printStackTrace();
		mv.setViewName("error");

		logger.error("异常信息：" + ex.getClass() + " - " + ex.getMessage());

		return mv;
	}

	/**
	 * 自定义异常处理
	 *
	 * @param ex
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(value = LogisticsGpException.class)
	public Result<?> logisticsGpExceptionHandler(Exception ex) {
		LogisticsGpException gpException = (LogisticsGpException) ex;
		return ResultUtil.error(gpException.getCode(), gpException.getMessage());
	}
}
