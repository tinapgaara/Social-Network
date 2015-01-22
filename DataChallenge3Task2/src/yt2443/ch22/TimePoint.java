package yt2443.ch22;

public class TimePoint {

	public long m_lTime;
	public short m_sYMD;	// year (1 byte) + month (4 bits) + day (4 bits)
	public byte m_byTimeUnit;
	
	public TimePoint() {
		
		this( (long) 0, (short) 0, (byte) 0);
	}
	
	public TimePoint(long lTime, short sYMD, byte byTimeUnit) {
		
		m_lTime = lTime;
		m_sYMD = sYMD;
		m_byTimeUnit = byTimeUnit;
	}

	public static short combineYMD(int nYear, int nMonth, int nDay) {
		
		return (short) ( ( (nYear - 2000) << 11 ) | (nMonth << 7) | nDay );
	}

	public static byte calcTimeUnit(int nHour, int cHoursPerUnit) {
		
		return (byte) (nHour / cHoursPerUnit);
	}

}
