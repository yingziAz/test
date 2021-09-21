package com.mj.kit;

import static com.github.sd4324530.jtuple.Tuples.tuple;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.sd4324530.jtuple.Tuple3;
import com.google.common.collect.ImmutableList;

public class DateUtil {
	private static Log  log = LogFactory.getLog(DateUtil.class);
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	private static final String DEFAULT_YEAE_PATTERN = "yyyy";
	private static final String DEFAULT_YEAE_MONTH_PATTERN = "yyyy-MM";;
	//    private static final String TIME_PATTERN = "HH:mm";


	private static final long ONE_MINUTE = 60;
	private static final long ONE_HOUR = 3600;
	private static final long ONE_DAY = 86400;
	private static final long ONE_MONTH = 2592000;
	private static final long ONE_YEAR = 31104000;

	public static String getDatePattern() {
		return DEFAULT_DATE_PATTERN;
	}

	public static String getYearMonthPattern() {
		return DEFAULT_YEAE_MONTH_PATTERN;
	}
	//获取当前日期
	//以 yyyy-mm-dd 形式
	public static Date currentDate(){
		return strToDate(dateToStr(new Date(),DEFAULT_DATE_PATTERN),DEFAULT_DATE_PATTERN);
	}

	//获取当前日期
	public static String currentDate2Str() {
		return dateToStr(new Date(),DEFAULT_DATE_PATTERN);
	}

	/**
	 * @Description:获取当月  yyyyMM
	 * @version 2019 年 03 月 20 日  09:27:21 
	 * @return
	 */
	public static String currentMonth(){
		return dateToStr(new Date(),DEFAULT_YEAE_MONTH_PATTERN);
	}
	//获取当年
	public static String currentYear(){
		return dateToStr(new Date(),DEFAULT_YEAE_PATTERN);
	}
	public static String getDateTimePattern() {
		return DateUtil.getDatePattern() + " HH:mm:ss.S";
	}

	//日期转字符串 ，使用的默认 pattern
	public static String dateToStr(Date date) {
		if(date == null){
			return StringUtils.EMPTY;
		}
		SimpleDateFormat df = new SimpleDateFormat(getDatePattern());
		return df.format(date);
	}
	//日期转字符串 ，可自定义 pattern
	public static String dateToStr(Date aDate,String pattern) {
		if(aDate == null){
			return StringUtils.EMPTY;
		}
		if(StringUtils.isEmpty(pattern)){
			pattern = DEFAULT_DATE_PATTERN;
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		return df.format(aDate);
	}

	/**
	 * datetime to string 
	 * @param aDate
	 * @return yyyy-MM-dd HH:mm 的格式
	 */
	public static String dateTimeToStr(Date aDate) {
		if(aDate == null){
			return StringUtils.EMPTY;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return df.format(aDate);
	}

	/**
	 * datetime to string
	 * @param aDate
	 * @return
	 */
	public static String dateTimeToStrWithSecond(Date aDate) {
		if(aDate == null){
			return StringUtils.EMPTY;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		return df.format(aDate);
	}


	//字符串转日期
	public static Date strToDate(String strDate,String pattern){
		if(StringUtils.isEmpty(strDate)){
			return null;
		}
		if(StringUtils.isEmpty(pattern)){
			pattern = DEFAULT_DATE_PATTERN;
		}
		try {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			return df.parse(strDate);
		} catch (ParseException pe) {
			log.error("字符串转日期出错："+ pe.getMessage());
			return null;
		}
	}
	//字符串转日期
	public static Date strToDate(String strDate){
		return strToDate(strDate,DEFAULT_DATE_PATTERN);
	}

	/**
	 * 时间【分钟】相加
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date plusMinute(Date date, int amount) {
		if(date == null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, amount);
		return calendar.getTime();
	}

	/**
	 * 日期相加天数
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date plusDate(Date date, int amount) {
		if(date == null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, amount);
		return calendar.getTime();
	}

	/**
	 * 日期相加月份
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date plusMonth(Date date, int amount) {
		if(date == null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, amount);
		return calendar.getTime();
	}

	/**
	 * 日期相加月份
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date plusYear(Date date, int amount) {
		if(date == null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, amount);
		return calendar.getTime();
	}
	
	/**
	 * 获取日期所在的 号 2013-01-13，返回 13
	 * @param date java.util.Date对象,不能为null
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	public static int getDaysOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取系统日期所在的 号 2013-01-13，返回 13
	 * @param date java.util.Date对象,不能为null
	 * @return
	 */
	public static int getDayOfMonth() {
		return getDayOfMonth(new Date());
	}

	/**
	 * 根据日期获取星期几
	 * @param date java.util.Date对象,不能为null
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * 统计两个时间差，返回的是天数(即24小时算一天，少于24小时就为0，用这个的时候最好把小时、分钟等去掉)
	 * @param beginDate 开始时间  不能为空
	 * @param endDate 结束时间 不能为空
	 * @return
	 */
	public static int getDays(String beginDate, String endDate) {
		return getDays(beginDate, endDate, DEFAULT_DATE_PATTERN);
	}
	/**
	 * 统计两个时间差，返回的是天数(即24小时算一天，少于24小时就为0，用这个的时候最好把小时、分钟等去掉)
	 * @param begin 开始时间
	 * @param end
	 * @return
	 */
	public static int getDays(String beginDate, String endDate,String pattern) {
		return getDays(strToDate(beginDate, pattern),strToDate(endDate, pattern));
	}


	/**
	 * 统计两个时间差，返回的是天数(即24小时算一天，少于24小时就为0，用这个的时候最好把小时、分钟等去掉)
	 * @param beginDate 开始时间  不能为空 Date 类型
	 * @param endDate 结束时间 不能为空 Date 类型
	 * @return
	 */
	public static int getDays(Date beginDate, Date endDate) {
		long times = endDate.getTime() - beginDate.getTime();
		return (int) (times / 60 / 60 / 1000 / 24);
	}
	/**
	 * 统计两个时间差，返回的是分钟少于1分钟算1分钟
	 * @param beginDate 开始时间  不能为空 Date 类型
	 * @param endDate 结束时间 不能为空 Date 类型
	 * @return
	 */
	public static int getMinutes(Date beginDate, Date endDate) {
		long times = endDate.getTime() - beginDate.getTime();
		int suf = (int)times % (60*1000) > 0? 1:0;
		return (int) times/(60*1000) + suf;
	}
	
	/**
	 * 获得两个月份间隔的天数
	 * 如2015-02 ， 2015-02 返回 2015.2月的天数
	 * 如2015-02 ， 2015-03 返回 2015.2月的天数 + 2015.03月的天数
	 * @param startMonth
	 * @param endMonth 
	 * @return
	 */
	public static int getDaysBetweenYM(String startMonth,String endMonth){
		return getDays(getFirstDayFromYM(startMonth),getFirstDayFromYM(getNextMonthFromYM(endMonth)));
	}

	/**
	 * 根据月份获得该月份的1号日期
	 * @param year_month 不能为空
	 */
	public static Date getFirstDayFromYM(String year_month){
		Date start_date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
			start_date =  sdf.parse(year_month+"01");
		} catch (ParseException pe) {
			log.error("exception in convert string to date!");
		}
		return start_date;
	}
	/**
	 * 根据月份获得该月份的月末日期
	 */
	public static Date getLastDayFromYM(String year_month){
		Date end_date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
			Date start_date = sdf.parse(year_month+"01");
			Calendar c1 = Calendar.getInstance();  
			c1.setTime(start_date);
			c1.add(Calendar.MONTH, 1);
			c1.add(Calendar.DAY_OF_MONTH, -1);
			end_date = c1.getTime();
		} catch (ParseException pe) {
			log.error("exception in convert string to date!");
		}
		return end_date;
	}

	/**
	 * 根据月份获得上月月份
	 */
	public static String getPreMonthFromYM(String year_month){
		Date start_date = getFirstDayFromYM(year_month);
		SimpleDateFormat ymFormat = new SimpleDateFormat(DEFAULT_YEAE_MONTH_PATTERN);
		Calendar c2 = Calendar.getInstance();  
		c2.setTime(start_date);
		c2.add(Calendar.MONTH, -1);
		return ymFormat.format(c2.getTime());
	}

	/**
	 * 根据月份获得下月月份
	 */
	public static String getNextMonthFromYM(String year_month){
		Date start_date = getFirstDayFromYM(year_month);
		SimpleDateFormat ymFormat = new SimpleDateFormat(DEFAULT_YEAE_MONTH_PATTERN);
		Calendar cal = Calendar.getInstance();  
		cal.setTime(start_date);
		cal.add(Calendar.MONTH, 1);
		return ymFormat.format(cal.getTime());
	}

	/**
	 * 比较yyyy-MM 相差的月份
	 * @param startDay
	 * @param endDay
	 * @return
	 * startMonth = endMonth --> =0
	 * startMonth < endMonth --> >0
	 * startMonth > endMonth --> <0
    public static int compareYearMonth(String startMonth,String endMonth){
        DateTime begin = new DateTime(startMonth);
        DateTime end = new DateTime(endMonth);
        //计算区间天数
        Period p = new Period(begin, end, PeriodType.months());
        return p.getMonths();
    }
	 */

	/**
	 * 获取两个月份间隔的月数
	 * @param begin_month is not null
	 * @param end_month is not null
	 * @return 两个月份间隔的月数
	 * 2013-01, 2013-02 = 1
	 * 2013-01, 2013-01 = 0
	 * 
	 */
	public static int getMonthInterval(String begin_month,String end_month){
		//String[] sDateArr = begin_month.split("-");
		//String[] eDateArr = end_month.split("-");
		String[] sDateArr = new String[2];
		sDateArr[0] = StringUtils.left(begin_month, 4);
		sDateArr[1] = StringUtils.right(begin_month, 2);
		String[] eDateArr = new String[2];
		eDateArr[0] = StringUtils.left(end_month, 4);
		eDateArr[1] = StringUtils.right(end_month, 2);

		return (Integer.parseInt(eDateArr[0])-Integer.parseInt(sDateArr[0]))*12+(Integer.parseInt(eDateArr[1])-Integer.parseInt(sDateArr[1]));
	}

	public static int getMonth(Date startDate, Date endDate) {
		if (startDate.after(endDate)) {
			Date t = startDate;
			startDate = endDate;
			endDate = t;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		Calendar temp = Calendar.getInstance();
		temp.setTime(endDate);
		temp.add(Calendar.DATE, 1);

		int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

		if ((startCalendar.get(Calendar.DATE) == 1)&& (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month + 1;
		} else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month;
		} else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
			return year * 12 + month;
		} else {
			return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
		}
	}
	/**
	 * 
	 * @param yearMonth
	 * @param add 增加
	 * @return
	 */
	public static String plusYearMonth(String yearMonth,int amount){
		if(StringUtils.isEmpty(yearMonth)){
			return StringUtils.EMPTY;
		}
		Date date = getFirstDayFromYM(yearMonth);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, amount);
		Date _date = calendar.getTime();
		return dateToStr(_date,DEFAULT_YEAE_MONTH_PATTERN);

	}


	/**
	 * 返回两个日期的最小年月
	 * @param yearMonth
	 * @param add 增加
	 * @return
	 */
	public static String minYearMonth(String yearMonthA,String yearMonthB){
		if(StringUtils.isEmpty(yearMonthA) && StringUtils.isEmpty(yearMonthB)){
			return StringUtils.EMPTY;
		}
		if(StringUtils.isEmpty(yearMonthA)){
			return yearMonthB;
		}
		if(StringUtils.isEmpty(yearMonthB)){
			return yearMonthA;
		}
		if(StringUtils.equals(yearMonthB, yearMonthA)){
			return yearMonthA;
		}
		/**
        if(compareYearMonth(yearMonthA,yearMonthA) > 0){
            return yearMonthA;
        }
		 */
		return yearMonthB;
	}

	/**
	 * 根据月份获得年份
	 */
	public static String getYearFromYM(String year_month){
		if(!validate4YearMonth(year_month)){
			return StringUtils.EMPTY;
		}
		return StringUtils.substring(year_month, 0, 4);
	}


	/**
	 * 根据月份获得月
	 */
	public static int getMonthFromYM(String year_month){
		if(!validate4YearMonth(year_month)){
			return 0;
		}
		return Integer.parseInt(StringUtils.right(year_month, 2));
	}

	/**
	 * 验证日期：是否为 yyyy-mm-dd 的格式
	 * @param str_date
	 * @return
	 */
	public static boolean isValidDate(String str_date){
		String eL = "^((19|20)\\d\\d)(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])$";
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(str_date);
		return m.matches();
	}

	/**
	 * 验证日期：是否为 yyyy-mm-dd 的格式
	 * @param str_date
	 * @return
	 */
	public static Date validDate(String strDate){
		List<String> dateFmtRules = ImmutableList.of("yyyy-MM-dd","yyyyMMdd","yyyy-MM-dd");
		Date date = null;
		for(String fmt : dateFmtRules) {
			date = DateUtil.strToDate(strDate,fmt);
			if(date  != null) {
				return date;
			}
		}
		return date;
	}

	/**
	 * 验证日期：是否为 yyyy-MM 的格式
	 * @param str_date
	 * @return
	 */
	public static boolean validate4YearMonth(String str_date){
		String eL = "^((19|20)\\d\\d)(0?[1-9]|1[012])$";
		Pattern p = Pattern.compile(eL);         
		Matcher m = p.matcher(str_date);
		return m.matches();
	}

	public static int getAgeByBirthday(Date birthday) {
		if(birthday == null){
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthday)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		int age = yearNow - yearBirth;
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth 
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth 
				age--;
			}
		}
		return age;
	}

	/**
	 * unix timestamp to date unixtimestamp 转日期
	 * @param unixtimestamp
	 * @return
	 */
	public static Date unixTimestampToDate(Integer unixtimestamp){
		long timestamp = unixtimestamp*1000l;
		Date date = new Date(timestamp); 
		return date;
	}


	/**
	 * unix timestamp to date unixtimestamp 转日期
	 * @param unixtimestamp
	 * @return
	 */
	public static Date unixTimestampToDate(String unixtimestamp){
		long timestamp = Integer.parseInt(unixtimestamp)*1000l;
		Date date = new Date(timestamp); 
		return date;
	}

	/**
	 * date to unitimestamp 日期转 unixtimestamp
	 * @param date
	 * @return
	 */
	public static Integer dateToUnixTimestamp(Date date){
		if(date == null){
			return null;
		}
		return Integer.parseInt(String.valueOf(date.getTime() / 1000l));
	}

	/** 
	 * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前 
	 *  
	 * @param timeStamp 
	 * @return 
	 */ 
	public static String convertTimeToFormat(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		long time = date.getTime() / 1000;
		long now = new Date().getTime() / 1000;
		long ago = now - time;
		if(ago <= ONE_MINUTE){
			return "刚刚";
		}else if (ago <= ONE_HOUR)
			return (ago / ONE_MINUTE) + "分钟前";
		else if (ago <= ONE_DAY)
			return ago / ONE_HOUR + "小时前";
		else if (ago <= ONE_DAY * 2)
			return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点";
		else if (ago <= ONE_DAY * 3)
			return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点";
		else if (ago <= ONE_MONTH) {
			long day = ago / ONE_DAY;
			return day + "天前";
		} else if (ago <= ONE_YEAR) {
			long month = ago / ONE_MONTH;
			//long day = ago % ONE_MONTH / ONE_DAY;
			return month + "个月前";
		} else {
			long year = ago / ONE_YEAR;
			//int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is 0 so month+1
			return year + "年前";
		}

	}


	/**
	 * 验证从 excel上导入的日期，并执行解析
	 * @param cellData excel的单元格值
	 * @param requireFlg 是否为必须项
	 * @param excelRowNum excel上的第几行
	 * @param excelHead  文案
	 * @return
	 */
	public static Tuple3<Boolean, Date, String> validateDate4ImportFormExcel(Object cellData,boolean requireFlg,int excelRowNum,String cellDataDisp){
		Date _date = null;
		boolean isValid = false;
		String error = null;

		if(cellData != null){
			if (cellData instanceof Date) {
				_date = (Date) cellData;
				isValid = true;
			} else if (cellData instanceof String) {
				String cellDataStr = (String) cellData;
				if (StringUtils.isNotBlank(cellDataStr)) {
					_date = validDate(cellDataStr);
					if (_date == null) {
						error = MessageFormat .format("第   {0} 行：{1}『{2}』格式不正确，格式为：yyyy-MM-dd 或 yyyyMMdd,如：{3}；<br/>",
								excelRowNum,cellDataDisp, cellDataStr,DateUtil.currentDate2Str());
					} else {
						isValid = true;
					}
				}
			} else if (cellData instanceof Integer || cellData instanceof Long) {
				String cellDataStr = cellData.toString();
				if (StringUtils.isNotBlank(cellDataStr)) {
					if(StringUtils.length(cellDataStr)  == 8) {
						if (!DateUtil.isValidDate(cellDataStr)) {
							error = MessageFormat .format("第   {0} 行：{1}『{2}』格式不正确，格式为：yyyyMMdd,如：{3}；<br/>",
									excelRowNum,cellDataDisp, cellDataStr,DateUtil.currentDate2Str());

						} else {
							_date = DateUtil.strToDate(cellDataStr);
							isValid = true;
						}
					}else {
						//						Calendar c = new GregorianCalendar(1900,0,-1);  
						//						try {
						//							_date = DateUtils.addDays(c.getTime(), (Integer)cellData);  //42605是距离1900年1月1日的天数
						//							isValid = true;
						//						}catch(Exception e) {
						error = MessageFormat .format("第   {0} 行：{1}格式不正确，格式为：yyyyMMdd,如：{2}；<br/>",
								excelRowNum,cellDataDisp, DateUtil.currentDate2Str());
						//						}
					}
				}
			} 
			else{
				error = MessageFormat .format("第   {0} 行：{1}格式不正确，格式为：yyyyMMdd,如：{2}；<br/>",
						excelRowNum,cellDataDisp, DateUtil.currentDate2Str());
			}
		}

		if(requireFlg && _date == null){
			if(StringUtils.isEmpty(error)) {
				error = MessageFormat .format("第   {0} 行：{1}不能为空；<br/>",excelRowNum,cellDataDisp);
			}
		}

		if(_date != null && _date.after(DateUtil.strToDate("21000101"))) {
			isValid = false;
			error = MessageFormat .format("第   {0} 行：{1}填入日期不正确，解析到的日期值为：{2}；<br/>",
					excelRowNum,cellDataDisp, DateUtil.dateToStr(_date, "yyyy-MM-dd"));
		}
		
		Tuple3<Boolean, Date, String> result = tuple(isValid, _date, error);
		return result;
	}

	/**
	 * 验证从 excel上导入的日期，并执行解析
	 * @param cellData excel的单元格值
	 * @param requireFlg 是否为必须项
	 * @param excelRowNum excel上的第几行
	 * @param excelHead  文案
	 * @return
	 */
	public static Tuple3<Boolean, String, String> validateYearMonth4ImportFormExcel(Object cellData,boolean requireFlg,int excelRowNum,String cellDataDisp){
		String _month = null;
		boolean isValid = false;
		String error = null;
		if(cellData != null){
			if (cellData instanceof String) {
				String cellDataStr = (String) cellData;
				if (StringUtils.isNotBlank(cellDataStr)) {
					if (!DateUtil.validate4YearMonth(cellDataStr)){
						List<String> yearMonthFmtRules = ImmutableList.of("yyyy年MM月","yyyy年M月","yyyy-MM","yyyy-MM");
						for(String fmt : yearMonthFmtRules) {
							Date date = DateUtil.strToDate(cellDataStr,fmt);
							if(date  != null) {
								_month = DateUtil.dateToStr(date,"yyyyMM");
								isValid = true;
								break;
							}
						}
						if(_month == null) {
							error = MessageFormat.format("第   {0} 行：{1}『{2}』,输入不正确，正确的输入格式为：yyyyMM,如：{3}； <br>",excelRowNum,
									cellDataDisp,cellDataStr,DateUtil.currentMonth());
						}
					} else {
						_month = cellDataStr;
						isValid = true;
					}
				}
			}else if (cellData instanceof Integer || cellData instanceof Long ) {
				String cellDataStr = cellData.toString();
				if (StringUtils.isNotBlank(cellDataStr)) {
					if(StringUtils.length(cellDataStr)  == 6) {
						if (!DateUtil.validate4YearMonth(cellDataStr)) {
							error = MessageFormat.format("第   {0} 行：{1}『{2}』,输入不正确，正确的输入格式为：yyyyMM,如：{3}； <br>",excelRowNum,
									cellDataDisp,cellDataStr,DateUtil.currentMonth());

						} else {
							_month = cellDataStr;
							isValid = true;
						}
					}
				}
			}else if (cellData instanceof Double) {
				String cellDataStr= String.valueOf(((Double) cellData).intValue());
				if (StringUtils.isNotBlank(cellDataStr)) {
					if(StringUtils.length(cellDataStr)  == 6) {
						if (!DateUtil.validate4YearMonth(cellDataStr)) {
							error = MessageFormat.format("第   {0} 行：{1}『{2}』,输入不正确，正确的输入格式为：yyyyMM,如：{3}； <br>",excelRowNum,
									cellDataDisp,cellDataStr,DateUtil.currentMonth());

						} else {
							_month = cellDataStr;
							isValid = true;
						}
					}
				}
			} else{
				error = MessageFormat.format("第   {0} 行：{1}『{2}』,输入不正确，正确的输入格式为：yyyyMM,如：{3}； <br>",excelRowNum,
						cellDataDisp,cellData.toString(),DateUtil.currentMonth());
			}
		}

		if(requireFlg && _month == null){
			if(StringUtils.isEmpty(error)) {
				error = MessageFormat .format("第   {0} 行：{1}不能为空；<br/>",excelRowNum,cellDataDisp);
			}
		}else if(!requireFlg && _month == null){
			if(StringUtils.isEmpty(error)) {
				isValid = true;
			}
		}
		Tuple3<Boolean, String, String> result = tuple(isValid, _month, error);
		return result;
	}



	public static void  main(String a[]){
		//如果 start Month <= end Month return >=0
		//System.out.println("-------------->"+DateUtil.getMonthInterval("201504","201601"));

		String value = "2018-9";
		List<String> yearMonthFmtRules = ImmutableList.of("yyyy年MM月","yyyy年M月","yyyy-MM","yyyy-MM");

		for(String fmt : yearMonthFmtRules) {
			Date date = DateUtil.strToDate(value,fmt);
			if(date  != null) {
				System.out.println(DateUtil.dateToStr(date,"yyyyMM"));
			}
		}

	}

}
