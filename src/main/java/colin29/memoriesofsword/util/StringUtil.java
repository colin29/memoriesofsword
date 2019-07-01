package colin29.memoriesofsword.util;

/**
 * String manipulation methods that we don't expect will ever need to differ, or that we want one-point of control with
 * 
 * @author Colin Ta
 *
 */
public class StringUtil {

	/**
	 * Converts the first character of a string to uppercase
	 */
	public static String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Converts the first character of a string to lowercase
	 */
	public static String decapitalize(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
}
