package yt2443.aProject.entity;

import java.util.StringTokenizer;

import yt2443.aProject.SnsException;

public class TweetTime {

	// sample: "Thu Sep 13 11:35:04 +0000 2012"
	private static final int LENGTH_TweetTimeString = 30;
	private static final String MONTH_Jan = "Jan";
	private static final String MONTH_Feb = "Feb";
	private static final String MONTH_Mar = "Mar";
	private static final String MONTH_Apr = "Apr";
	private static final String MONTH_May = "May";
	private static final String MONTH_Jun = "Jun";
	private static final String MONTH_Jul = "Jul";
	private static final String MONTH_Aug = "Aug";
	private static final String MONTH_Sep = "Sep";
	private static final String MONTH_Oct = "Oct";
	private static final String MONTH_Nov = "Nov";
	private static final String MONTH_Dec = "Dec";
	
	private static final String STRING_FieldDelimiterInMediumTime = ",";
	
	
	public short m_sYear;
	public byte m_byMonth, m_byDay, m_byHour, m_byMinute, m_bySecond;
	
	public TweetTime(short sYear, byte byMonth, byte byDay) {
		
		this( sYear, byMonth, byDay,
				(byte) 0, (byte) 0, (byte) 0);
	}
	
	public TweetTime(short sYear, byte byMonth, byte byDay,
			byte byHour, byte byMinute, byte bySecond) {
		
		m_sYear = sYear;
		m_byMonth = byMonth;
		m_byDay = byDay;
		
		m_byHour = byHour;
		m_byMinute = byMinute;
		m_bySecond = bySecond;
	}
	
	private TweetTime() {
		
		this( (short) 0, (byte) 0, (byte) 0,
				(byte) 0, (byte) 0, (byte) 0);
	}
	
	public static TweetTime parseOriginalTime(String strOriginalTime) throws SnsException {

		if ( (strOriginalTime == null) || (strOriginalTime.length() != LENGTH_TweetTimeString) ) {
			throw new SnsException(SnsException.ERR_DESP_IllegalDateTimeFormat);
		}
		
		// sample: "Thu Sep 13 11:35:04 +0000 2012"
		String strYear = strOriginalTime.substring(26, 30);
		String strMonth = strOriginalTime.substring(4, 7);
		String strDay = strOriginalTime.substring(8, 10);
		String strHour = strOriginalTime.substring(11, 13);
		String strMinute = strOriginalTime.substring(14, 16);
		String strSecond = strOriginalTime.substring(17, 19);;

		TweetTime tweetTime = new TweetTime();
		
		try {		
			tweetTime.m_sYear = Short.parseShort(strYear);
			tweetTime.m_byDay = Byte.parseByte(strDay);
			tweetTime.m_byHour = Byte.parseByte(strHour);
			tweetTime.m_byMinute = Byte.parseByte(strMinute);
			tweetTime.m_bySecond = Byte.parseByte(strSecond);
		
			if (strMonth.equals(MONTH_Jan)) {
				tweetTime.m_byMonth = 1;
			}
			else if (strMonth.equals(MONTH_Feb)) {
				tweetTime.m_byMonth = 2;
			}
			else if (strMonth.equals(MONTH_Mar)) {
				tweetTime.m_byMonth = 3;
			}
			else if (strMonth.equals(MONTH_Apr)) {
				tweetTime.m_byMonth = 4;
			}
			else if (strMonth.equals(MONTH_May)) {
				tweetTime.m_byMonth = 5;
			}
			else if (strMonth.equals(MONTH_Jun)) {
				tweetTime.m_byMonth = 6;
			}
			else if (strMonth.equals(MONTH_Jul)) {
				tweetTime.m_byMonth = 7;
			}
			else if (strMonth.equals(MONTH_Aug)) {
				tweetTime.m_byMonth = 8;
			}
			else if (strMonth.equals(MONTH_Sep)) {
				tweetTime.m_byMonth = 9;
			}
			else if (strMonth.equals(MONTH_Oct)) {
				tweetTime.m_byMonth = 10;
			}
			else if (strMonth.equals(MONTH_Nov)) {
				tweetTime.m_byMonth = 11;
			}
			else if (strMonth.equals(MONTH_Dec)) {
				tweetTime.m_byMonth = 12;
			}
			else {
			    throw new SnsException(SnsException.ERR_DESP_IllegalDateTimeFormat, strMonth);
			}
		}
		
		catch (NumberFormatException ex) {
        	ex.printStackTrace();
			throw new SnsException(SnsException.ERR_DESP_IllegalDateTimeFormat);
		}
		
		return tweetTime;
	}
	
	public static TweetTime parseMediumTime(String strMediumTime) throws SnsException {

		String strYear = null, strMonth = null, strDay = null;
		String strHour = null, strMinute = null, strSecond = null;
		
		// sample: "2012,9,13,3,40,7"
		StringTokenizer tokenizer = new StringTokenizer(strMediumTime, STRING_FieldDelimiterInMediumTime);
		int nFieldOrderNo = 1;
		String strField;
        while (tokenizer.hasMoreTokens()) {
        	
        	strField = tokenizer.nextToken();
        	switch (nFieldOrderNo) {
        	case 1:
        		strYear = strField;
        		break;
        		
        	case 2:
        		strMonth = strField;
        		break;
        		
        	case 3:
        		strDay = strField;
        		break;
        		
        	case 4:
        		strHour = strField;
        		break;
        		
        	case 5:
        		strMinute = strField;
        		break;
        		
        	case 6:
        		strSecond = strField;
        		break;
        	}

        	nFieldOrderNo++;
        }

		TweetTime tweetTime = new TweetTime();
		
		try {		
			tweetTime.m_sYear = Short.parseShort(strYear);
			tweetTime.m_byMonth = Byte.parseByte(strMonth);
			tweetTime.m_byDay = Byte.parseByte(strDay);
			tweetTime.m_byHour = Byte.parseByte(strHour);
			tweetTime.m_byMinute = Byte.parseByte(strMinute);
			tweetTime.m_bySecond = Byte.parseByte(strSecond);
		}
		
		catch (NumberFormatException ex) {
        	ex.printStackTrace();
			throw new SnsException(SnsException.ERR_DESP_IllegalDateTimeFormat);
		}
		
		return tweetTime;
	}
	
	public String toString() {
		
		return "" + m_sYear + "," +
		    m_byMonth + "," +
		    m_byDay + "," +
		    m_byHour + "," +
		    m_byMinute + "," +
		    m_bySecond;
	}

	public boolean isBetween_IgnoreTimeOfDay(TweetTime timeFrom, TweetTime timeTo) {
		
		if (timeFrom != null) {
			if ( ! isEqualOrGreaterThan_IgnoreTimeOfDay(timeFrom) ) {
				return false;
			}
		}
		
		if (timeTo != null) {
			if ( ! timeTo.isEqualOrGreaterThan_IgnoreTimeOfDay(this) ) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isEqualOrGreaterThan_IgnoreTimeOfDay(TweetTime time) {
			
		if (m_sYear > time.m_sYear) {
			return true;
		}
		else if (m_sYear < time.m_sYear) {
			return false;
		}
		
		if (m_byMonth > time.m_byMonth) {
			return true;
		}
		else if (m_byMonth < time.m_byMonth) {
			return false;
		}
		
		if (m_byDay >= time.m_byDay) {
			return true;
		}
		
		return false;
	}

	public int compare(TweetTime time) {
		
		if (m_sYear > time.m_sYear) {
			return -1;
		}
		else if (m_sYear < time.m_sYear) {
			return +1;
		}
		
		if (m_byMonth > time.m_byMonth) {
			return -1;
		}
		else if (m_byMonth < time.m_byMonth) {
			return +1;
		}
		
		if (m_byDay > time.m_byDay) {
			return -1;
		}
		else if (m_byDay < time.m_byDay) {
			return +1;
		}
		
		if (m_byHour > time.m_byHour) {
			return -1;
		}
		else if (m_byHour < time.m_byHour) {
			return +1;
		}
		
		if (m_byMinute > time.m_byMinute) {
			return -1;
		}
		else if (m_byMinute < time.m_byMinute) {
			return +1;
		}
		
		if (m_bySecond > time.m_bySecond) {
			return -1;
		}
		else if (m_bySecond < time.m_bySecond) {
			return +1;
		}
		
		return 0;
	}
}
