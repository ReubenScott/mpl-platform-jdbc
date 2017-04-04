package com.soak.framework.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * 数值转换
 * @author hyPortal
 * @version 1.0 2012-01-01
 */
public class NumberUtils {
	
	public static boolean isNumericExt(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str.replaceAll("-", "")).matches();
	}

	/** 对字符数字进行整数截取* */
	public static String floatFormat(Object str) {
		DecimalFormat format = new DecimalFormat("0");
		if (str != null) {
			BigDecimal val = new BigDecimal(str.toString());
			String result = format.format(val);
			return result;
		}
		return "0";
	}

	/** 对字符数字进行小数点后两位截取* */
	public static String floatFormatHB(Object str) {
		String result = "0.00";
		DecimalFormat format = new DecimalFormat("0.00");
		if (str != null) {
			if (str.getClass().getSimpleName().equals("Double")
					|| str.getClass().getSimpleName().equals("Integer")
					|| str.getClass().getSimpleName().equals("BigDecimal")
					|| str.getClass().getSimpleName().equals("BigInteger")) {
				if (str.toString().equals("Infinity")
						|| str.toString().equals("NaN")) {
					result = "100";
				} else {
					BigDecimal val = new BigDecimal(str.toString());
					result = format.format(val);
				}
			}
		}
		return result;
	}

	/** 对字符数字进行四舍五入截取* */
	public static String roundFormat(String str) {
		float f = Float.valueOf(str);
		f = Math.round(f * 1000) / 1000f;
		return String.valueOf(f);
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// 求总数
	public static int sum(int[] value) {
		int result = 0;
		for (int i = 0; i < value.length; i++) {
			result += value[i];
		}
		return result;
	}

	/**
	 * 判断一个数值是否是整数
	 * 
	 * @param d
	 *            数值
	 * @return true-整数，false-浮点数
	 */
	public static boolean isLong(Double d) {
		String str = String.valueOf(d);
		int index = str.indexOf('.');
		if (index == -1) {
			return true;
		}
		String decimal = str.substring(index + 1);
		for (int i = 0, len = decimal.length(); i < len; i++) {
			if (decimal.charAt(i) != '0') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字符串转换为Integer
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值，转换失败后返回的默认值
	 * @return Integer
	 */
	public static Integer toInteger(String str, Integer defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return new Integer(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 字符串转换为Integer
	 * 
	 * @param str
	 *            字符串
	 * @return Integer，或者null，如果转换失败
	 */
	public static Integer toInteger(String str) {
		return toInteger(str, null);
	}

	/**
	 * 字符串转换为Long
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值，转换失败后返回的默认值
	 * @return Long
	 */
	public static Long toLong(String str, Long defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return new Long(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 字符串转换为Long
	 * 
	 * @param str
	 *            字符串
	 * @return Long，或者null，如果转换失败
	 */
	public static Long toLong(String str) {
		return toLong(str, null);
	}

	/**
	 * 字符串转换为Float
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值，转换失败后返回的默认值
	 * @return Float
	 */
	public static Float toFloat(String str, Float defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return new Float(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 字符串转换为Float
	 * 
	 * @param str
	 *            字符串
	 * @return Float，或者null，如果转换失败
	 */
	public static Float toFloat(String str) {
		return toFloat(str, null);
	}

	/**
	 * 字符串转换为Double
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值，转换失败后返回的默认值
	 * @return Double
	 */
	public static Double toDouble(String str, Double defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return new Double(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 字符串转换为Double
	 * 
	 * @param str
	 *            字符串
	 * @return Double，或者null，如果转换失败
	 */
	public static Double toDouble(String str) {
		return toDouble(str, null);
	}

	/**
	 * 字符串转换为Short
	 * 
	 * @param str
	 *            字符串
	 * @param defaultValue
	 *            默认值，转换失败后返回的默认值
	 * @return Short
	 */
	public static Short toShort(String str, Short defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return new Short(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 字符串转换为Short
	 * 
	 * @param str
	 *            字符串
	 * @return Short，或者null，如果转换失败
	 */
	public static Short toShort(String str) {
		return toShort(str, null);
	}

	/**
	 * 将数字格式为以","分割的字符串小数位为0。若数值为空则返回"0"，若有截断为四舍五入。
	 * 
	 * @param decimal
	 *            数值
	 * @return 格式化后的数字字符串
	 * @author hz
	 */
	public static String formatInteger(Number decimal) {
		return formatNumber(decimal, 0);
	}

	/**
	 * 格式金额。小数位为2。
	 * 
	 * @param menoy
	 *            金额
	 * @return 格式化的金额
	 * @author hz
	 */
	public static String formatMoney(Number menoy) {
		return formatNumber(menoy, 2);
	}

	/**
	 * 将数字格式为以","分割的字符串。若数值为空则返回"0"，若有截断为四舍五入
	 * 
	 * @param decimal
	 *            数值
	 * @param fraction
	 *            小数位
	 * @return 格式化后的数字字符串
	 * @author hz
	 */
	public static String formatNumber(Number decimal, int fraction) {
		return formatNumber(decimal, fraction, fraction);
	}

	/**
	 * 将数字格式为以","分割的字符串。若有截断为四舍五入。
	 * 
	 * @param decimal
	 *            数值
	 * @param fraction
	 *            小数位
	 * @param nullIf
	 *            若为null，则返回该默认值
	 * @return 格式化后的数字字符串，若数值为空则返回nullIf。
	 * @author hz
	 */
	public static String formatNumber(Number decimal, int fraction,
			String nullIf) {
		return formatNumber(decimal, fraction, fraction, nullIf);
	}

	/**
	 * 将数字格式为以","分割的字符串，若有截断为四舍五入
	 * 
	 * @param number
	 *            要格式的数字类型实例，可以是Integer,Long,Float,Double,BigDecimal,BigInteger
	 * @param maximumFractionDigits
	 *            最多小数位数
	 * @param minimumFractionDigits
	 *            最少小数位数
	 * @return 格式化后的数字字符串，若数值为空则返回"0"。
	 * @author hz
	 */
	public static String formatNumber(Number number, int maximumFractionDigits,
			int minimumFractionDigits) {
		return formatNumber(number, maximumFractionDigits,
				minimumFractionDigits, "0");
	}

	/**
	 * 将数字格式为以","分割的字符串，若有截断为四舍五入
	 * 
	 * @param number
	 *            要格式的数字类型实例，可以是Integer,Long,Float,Double,BigDecimal,BigInteger
	 * @param maximumFractionDigits
	 *            最多小数位数
	 * @param minimumFractionDigits
	 *            最少小数位数
	 * @param nullIf
	 *            若为null，则返回该默认值
	 * @return 格式化后的数字字符串
	 * @author hz
	 */
	public static String formatNumber(Number number, int maximumFractionDigits,
			int minimumFractionDigits, String nullIf) {
		if (number == null) {
			return nullIf;
		}
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(maximumFractionDigits);
		nf.setMinimumFractionDigits(minimumFractionDigits);
		return nf.format(number);
	}

	/**
	 * 求最大值
	 * @param num1 值，可能为null
	 * @param num2 值，可能为null
	 * @return 最大值
	 */
	public static Number max(Number num1, Number num2) {
		if (num1 == null) {
			return num2;
		}
		if (num2 == null) {
			return num1;
		}
		return num1.doubleValue() > num2.doubleValue() ? num1 : num2;
	}
	
	/**
	 * 求最小值
	 * @param num1 值，可能为null
	 * @param num2 值，可能为null
	 * @return 最小值
	 */
	public static Number min(Number num1, Number num2) {
		if (num1 == null) {
			return num2;
		}
		if (num2 == null) {
			return num1;
		}
		return num1.doubleValue() > num2.doubleValue() ? num2 : num1;
	}
	
}
