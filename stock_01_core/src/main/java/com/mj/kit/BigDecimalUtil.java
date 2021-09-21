package com.mj.kit;

import static com.github.sd4324530.jtuple.Tuples.tuple;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;

import com.github.sd4324530.jtuple.Tuple3;

/**
 * 数值计算的工具类。
 * 
 * @version v1.0.0
 */
public class BigDecimalUtil {

	public static BigDecimal zero = new BigDecimal(0);
	public static DecimalFormat amountFormatStyle = new DecimalFormat("#.00");

	/**
	 * 返回两个值的最大值
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static BigDecimal max(BigDecimal num1, BigDecimal num2) {
		if (num1 == null && num2 == null)
			return zero;
		if (num1 == null)
			return num2;
		if (num2 == null)
			return num1;
		return num1.max(num2);
	}

	/**
	 * 返回两个值的最小值
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static BigDecimal min(BigDecimal num1, BigDecimal num2) {
		if (num1 == null || num2 == null)
			return zero;
		return num1.min(num2);
	}

	/**
	 * 比较两个值是否相等
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static boolean equals(BigDecimal num1, BigDecimal num2) {
		if (num1 == null && num2 == null)
			return true;
		if (num1 == null)
			return false;
		if (num2 == null)
			return false;
		if (num1.compareTo(num2) == 0)
			return true;
		return false;
	}

	/**
	 * 如果传入的值，为空，则返回0
	 * 
	 * @param num
	 * @return
	 */
	public static BigDecimal ifnull(BigDecimal num) {
		if (num == null) {
			return new BigDecimal(0);
		}
		return num;
	}

	/**
	 * 如果传入的值，为空，则返回true
	 * 
	 * @param num
	 * @return
	 */
	public static boolean ifBlank(BigDecimal num) {
		if (num != null) {
			return false;
		}
		return true;
	}

	/**
	 * 加
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static BigDecimal plus(BigDecimal num1, BigDecimal num2) {
		if (num1 == null && num2 == null)
			return zero;
		if (num1 == null)
			return num2;
		if (num2 == null)
			return num1;
		return num1.add(num2);
	}

	/**
	 * @param 可变参数
	 * @return
	 */
	public static BigDecimal plusVP(BigDecimal... nums) {
		if (nums == null)
			return zero;
		BigDecimal sum = null;
		for (BigDecimal num : nums)
			sum = plus(sum, num);
		return sum;
	}

	/**
	 * 减
	 * 
	 * @param minuend
	 *            被减数
	 * @param subtrahend
	 *            减数
	 * @return
	 */
	public static BigDecimal minus(BigDecimal minuend, BigDecimal subtrahend) {
		if (minuend == null && subtrahend == null)
			return zero;
		if (minuend == null)
			return subtrahend.multiply(new BigDecimal(-1));
		if (subtrahend == null)
			return minuend;
		return minuend.subtract(subtrahend);
	}

	/**
	 * 减
	 * 
	 * @param minuend
	 *            被减数
	 * @param subtrahend
	 *            减数
	 * @return
	 */
	public static BigDecimal minusVP(BigDecimal minuend, BigDecimal... subtrahend) {
		if (minuend == null && subtrahend == null)
			return zero;
		if (minuend == null) {
			BigDecimal result = null;
			for (BigDecimal _subtrahend : subtrahend) {
				result = plus(result, _subtrahend.multiply(new BigDecimal(-1)));
			}
			return result;
		}
		if (subtrahend == null)
			return minuend;
		for (BigDecimal _subtrahend : subtrahend) {
			minuend = minus(minuend, _subtrahend);
		}
		return minuend;
	}

	/**
	 * 数值 × 比例（需除以100） 四舍五入
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal multiplyProportion(BigDecimal num, BigDecimal proportion) {
		if (num == null)
			return zero;
		if (proportion == null)
			return zero;
		BigDecimal equalsValue = num.divide(new BigDecimal(100.0)).multiply(proportion);
		return equalsValue.setScale(2, BigDecimal.ROUND_HALF_UP); // 四舍五入;
	}

	/**
	 * 数值 × 比例（需除以100） 四舍五入 传入小数点保留位数，及 scaleStr 小数保留（0,1,2） roundModeStr 小数保留规则
	 * F-四舍五入，C-向上取整
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal multiplyProportion(BigDecimal num, BigDecimal proportion, String scaleStr,
			String roundModeStr) {
		if (!(StringUtils.equals(roundModeStr, "F") || StringUtils.equals(roundModeStr, "C"))) {
			roundModeStr = "F";
		}
		Integer scale = 2;
		try {
			scale = Integer.parseInt(scaleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (num == null)
			return zero;
		if (proportion == null)
			return zero;
		BigDecimal equalsValue = num.divide(new BigDecimal(100.0)).multiply(proportion);

		int roundMode = BigDecimal.ROUND_HALF_UP;
		if (StringUtils.equals(roundModeStr, "F")) {
			roundMode = BigDecimal.ROUND_HALF_UP;
		} else if (StringUtils.equals(roundModeStr, "C")) {
			roundMode = BigDecimal.ROUND_UP;
		}
		return equalsValue.setScale(scale, roundMode); // 四舍五入;
	}

	/**
	 * 数值 ×数值
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal multiply(BigDecimal num, Integer num2) {
		if (num == null)
			return zero;
		if (num2 == null)
			return zero;
		BigDecimal equalsValue = num.multiply(new BigDecimal(num2));
		return equalsValue; // 四舍五入;
	}

	/**
	 * 数值 ×数值
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal multiply(BigDecimal num, BigDecimal num2) {
		if (num == null)
			return zero;
		if (num2 == null)
			return zero;
		BigDecimal equalsValue = num.multiply(num2);
		return equalsValue; // 四舍五入;
	}

	/**
	 * 数值 ×数值
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal multiply(BigDecimal num, BigDecimal num2, int scale, int roundMode) {
		if (num == null)
			return zero;
		if (num2 == null)
			return zero;
		BigDecimal equalsValue = num.multiply(num2);
		return equalsValue.setScale(scale, roundMode); // 四舍五入;
	}

	/**
	 * 除数 / 被除数
	 * 
	 * @param divisor
	 *            除数
	 * @param dividend
	 *            被除数
	 * @return
	 */
	public static BigDecimal divide(BigDecimal divisor, BigDecimal dividend) {
		// 被除数 为 Null
		if (divisor == null || dividend == null) {
			return null;
		}
		return divisor.divide(dividend, 2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 除数 / 被除数
	 * 
	 * @param divisor
	 *            除数
	 * @param dividend
	 *            被除数
	 * @return
	 */
	public static BigDecimal divide(BigDecimal divisor, BigDecimal dividend, int scale) {
		// 被除数 为 Null
		if (divisor == null || dividend == null) {
			return null;
		}
		return divisor.divide(dividend, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 除数 / 被除数
	 * 
	 * @param divisor
	 *            除数
	 * @param dividend
	 *            被除数
	 * @return
	 */
	public static BigDecimal divideAsProportion(BigDecimal divisor, BigDecimal dividend) {
		// 被除数 为 Null
		if (divisor == null || dividend == null) {
			return null;
		}
		return divisor.multiply(new BigDecimal(100)).divide(dividend, 2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 两个 数字比较
	 * 
	 * @return BigDecimal
	 */
	public static int compare(BigDecimal num, BigDecimal num2) {
		if (num == null)
			return -1;
		if (num2 == null)
			return 1;
		return num.compareTo(num2);
	}

	/**
	 * 金额形式-字符串形式返回，小数点两位
	 */
	public static String printAmount(BigDecimal num) {
		if (num == null)
			return "0.00";
		String decimalStr = amountFormatStyle.format(num);
		if (decimalStr.startsWith(".")) {
			decimalStr = "0" + decimalStr;
		}
		return decimalStr;
	}

	public static String printAmount(BigDecimal num, String defaultValue) {
		if (num == null)
			return defaultValue;
		String decimalStr = amountFormatStyle.format(num);
		if (decimalStr.startsWith(".")) {
			decimalStr = "0" + decimalStr;
		}
		return decimalStr;
	}


	public static String printAmountAsSimplify(BigDecimal num, String defaultValue) {
		if (num == null)
			return defaultValue;
		
		DecimalFormat amountFormatStyle = new DecimalFormat("#.##");
		String decimalStr = amountFormatStyle.format(num);
		if (decimalStr.startsWith(".")) {
			decimalStr = "0" + decimalStr;
		}
		return decimalStr;
	}
	

	public static String printAmountAsThousandSeperator(BigDecimal num, String defaultValue) {
		if (num == null)
			return defaultValue;
		
		DecimalFormat amountFormatStyle = new DecimalFormat("#,###.00");
		String decimalStr = amountFormatStyle.format(num);
		if (decimalStr.startsWith(".")) {
			decimalStr = "0" + decimalStr;
		}
		return decimalStr;
	}
	
	/**
	 * 验证金额（字符串为正确）
	 * 
	 * @param amountStr
	 * @return
	 */
	public static boolean validate(String amountStr) {
		if (StringUtils.isEmpty(amountStr)) {
			return false;
		}
		boolean isCorrectAmount = true;
		try {
			new BigDecimal(amountStr);
		} catch (Exception e) {
			e.printStackTrace();
			isCorrectAmount = false;
		}
		return isCorrectAmount;
	}

	/**
	 * 数字字符串转 BigDecimal
	 * 
	 * @param amountStr
	 * @return
	 */
	public static BigDecimal parse(String amountStr) {
		if (StringUtils.isEmpty(amountStr)) {
			return null;
		}
		try {
			return new BigDecimal(amountStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * excel 的数值验证
	 * 
	 * @param cellData
	 * @param requireFlg
	 * @param excelRowNum
	 * @param cellDataDisp
	 * @return
	 */
	public static Tuple3<Boolean, BigDecimal, String> validateData4ImportFormExcel(Object cellData, boolean requireFlg,
			int excelRowNum, String cellDataDisp) {

		BigDecimal number = null;
		boolean isValid = true;
		String error = null;
		if (cellData != null) {
			String numberStr = cellData.toString();
			if (StringUtils.isNotEmpty(numberStr)) {
				try {
					number = new BigDecimal(numberStr);
				} catch (Exception e) {
					isValid = false;
					error = MessageFormat.format("第   {0} 行：{1}输入不正确；<br/>", excelRowNum, cellDataDisp);
					Tuple3<Boolean, BigDecimal, String> result = tuple(isValid, number, error);
					return result;
				}
			}
		}

		if (requireFlg && number == null) {
			isValid = false;
			error = MessageFormat.format("第   {0} 行：{1}不能为空；<br/>", excelRowNum, cellDataDisp);
		}

		Tuple3<Boolean, BigDecimal, String> result = tuple(isValid, number, error);
		return result;
	}

	public static BigDecimal getBigDecimal(Object value) {
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()
						+ " into a BigDecimal.");
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		System.out.println(BigDecimalUtil.divide(new BigDecimal(0.3), new BigDecimal(100), 4));
	}

}
