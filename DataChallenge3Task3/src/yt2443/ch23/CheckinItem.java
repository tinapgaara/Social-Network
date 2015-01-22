package yt2443.ch23;

public class CheckinItem {

	public long m_lTime;
	public byte m_byYear, m_byMonth, m_byDay;	// year - 2000
	public byte m_byTimeUnit;
	
	private float m_fLatitude, m_fLongitude;
	public int m_nLocIndex;
	
	public CheckinItem(TimePoint timePoint,
			float fLatitude, float fLongitude, int nLocIndex) {
		
		if (timePoint == null) {
			m_lTime = 0;
			m_byYear = 0; m_byMonth = 0; m_byDay = 0;
			m_byTimeUnit = 0;
		}
		else {
			m_lTime = timePoint.m_lTime;
			m_byYear = timePoint.m_byYear;
			m_byMonth = timePoint.m_byMonth; 
			m_byDay = timePoint.m_byDay;
			m_byTimeUnit = timePoint.m_byTimeUnit;
		}
		
		m_fLatitude = fLatitude;
		m_fLongitude = fLongitude;
		m_nLocIndex = nLocIndex;
	}

	public CheckinItem copy() {
		
		CheckinItem itemNew = new CheckinItem(null, 
				m_fLatitude, m_fLongitude, m_nLocIndex);
		
		itemNew.m_lTime = this.m_lTime;
		itemNew.m_byYear = this.m_byYear;
		itemNew.m_byMonth = this.m_byMonth;
		itemNew.m_byDay = this.m_byDay;
		itemNew.m_byTimeUnit = this.m_byTimeUnit;
		
		return itemNew;
	}
	
	public double calcDistance(CheckinItem another) {
		
		//**********************************************************************
		double dWork = Math.sin((m_fLatitude - another.m_fLatitude) / 2);
		dWork = dWork * dWork;
		
		double dWork2 = Math.sin((m_fLongitude - another.m_fLongitude) / 2);
		dWork2 = dWork2 * dWork2 * Math.cos(m_fLatitude) * Math.cos(another.m_fLatitude);
		
		dWork = Math.sqrt(dWork + dWork2);
		
		/*
		dWork = 2 * m_fEarthRadius * Math.asin(dWork);
		return dWork;
		//*/
		
		/*
	    Logger.showDbgMsg("Distance = [" + dWork + "]");
	    //*/
		
		return Math.asin(dWork);
		/**********************************************************************/
		
		/*
		float f1 = m_fLatitude - another.m_fLatitude;
		float f2 = m_fLongitude - another.m_fLongitude;
		
		return Math.sqrt( f1 * f1 + f2 * f2 );
		//*/
	}

	public long calcTimeDiff(CheckinItem another) {
		
		return Math.abs(m_lTime - another.m_lTime);
	}

}
