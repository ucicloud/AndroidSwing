package com.kidsdynamic.data.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * Log工具类
 * 
 */
public class LogUtil2 {
	private final static boolean logFlag = true;

	public final static String tag = "SecureCloudService";
	private final static int logLevel = Log.VERBOSE;
	private String mClassName;

	private static LogUtil2 logger;

	private LogUtil2(String name) {
		mClassName = name;
		Logger.init(name);
	}


	public static LogUtil2 getUtils(String Tag) {
		if (logger == null) {
			logger = new LogUtil2(Tag);
		}
		return logger;
	}

	public static LogUtil2 getUtils() {
		if (logger == null) {
			logger = new LogUtil2(tag);
		}
		return logger;
	}

	/**
	 * 获取当前方法路径名
	 * 
	 * @since 2014年11月12日 下午5:13:27
	 * 
	 * @return 当前方法路径名
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return mClassName + "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":"
					+ st.getLineNumber() + " " + st.getMethodName() + " ]";
		}
		return null;
	}

	/**
	 * Info 级别日志
	 * 
	 * @since 2014年11月12日 下午5:14:50
	 * 
	 * @param str
	 */
	public void i(@NonNull Object str) {
		if (logFlag) {
			if (logLevel <= Log.INFO) {
				Logger.i(str.toString());
			}
		}

	}

	/**
	 * Debug 级别日志
	 * 
	 * @since 2014年11月12日 下午5:15:27
	 * 
	 * @param str
	 */
	public void d(@NonNull Object str) {
		if (logFlag) {
			if (logLevel <= Log.DEBUG) {
				Logger.d(str.toString());
			}
		}
	}

	/**
	 * Verbose 级别日志
	 * 
	 * @since 2014年11月12日 下午5:16:00
	 * 
	 * @param str
	 */
	public void v(@NonNull Object str) {
		if (logFlag) {
			if (logLevel <= Log.VERBOSE) {
				Logger.v(str.toString());
			}
		}
	}

	/**
	 * Warn 级别日志
	 * @since 2014年11月12日 下午5:16:07
	 * 
	 * @param str
	 */
	public void w(@NonNull Object str) {
		if (logFlag) {
			if (logLevel <= Log.WARN) {
				Logger.w(str.toString());
			}
		}
	}

	/**
	 * Error 级别日志
	 * @since 2014年11月12日 下午5:16:55
	 *  
	 * @param str
	 */
	public void e(@NonNull Object str) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				if(str != null){
					Logger.e(str.toString());
				}
			}
		}
	}

/**
 * Error 异常信息日志
 * @since 2014年11月12日 下午5:17:14
 *  
 * @param ex
 */
	public void e(Exception ex) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				Logger.e(ex,null);
			}
		}
	}

	/**
	 * Error 异常信息日志
	 * @since 2014年11月12日 下午5:17:14
	 *  
	 * @param log
     * @param tr
	 */
	public void e(String log, Throwable tr) {
		if (logFlag) {
			Logger.e(tr,log);
		}
	}

	public void json(String json){
		if (logFlag) {
			Logger.json(json);
		}
	}

	public void xml(String xml){
		if (logFlag) {
			Logger.xml(xml);
		}
	}



}
