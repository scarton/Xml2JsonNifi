package net.c4analytics.xml2json.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Static utilities for Semaphore Classifier processor
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * @date Nov 15, 2018
 */
public class Util {

	private Util() {}
	/**
	 * Turn a stack trace into a string - useful for logging
	 * @param e
	 * @return
	 */
	public static String stackTrace(Throwable e) {
		if (e == null)
			return "No StackTrace available for exception";
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
