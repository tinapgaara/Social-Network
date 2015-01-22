package yt2443.ch23;

public class TimePoint {

	public long m_lTime;
	public byte m_byYear, m_byMonth, m_byDay;	// year - 2000
	public byte m_byTimeUnit;
	
	public TimePoint() {
		
		this( (long) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}
	
	public TimePoint(long lTime, byte byYear, byte byMonth, byte byDay, byte byTimeUnit) {
		
		m_lTime = lTime;
		m_byYear = byYear;
		m_byMonth = byMonth;
		m_byDay = byDay;
		m_byTimeUnit = byTimeUnit;
	}

	public static byte calcTimeUnit(int nHour, int cHoursPerUnit) {
		
		return (byte) (nHour / cHoursPerUnit);
	}

}
