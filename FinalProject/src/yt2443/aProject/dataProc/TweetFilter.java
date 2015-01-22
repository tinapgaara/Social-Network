package yt2443.aProject.dataProc;

import yt2443.aProject.entity.TweetTime;

public class TweetFilter {

	public TweetTime m_timeFrom, m_timeTo;
	public int m_nMinLength;
	public int m_cMinWords;
	public boolean m_bWipeOffNoisyWords;
	
	public int m_cMinRetweets;
	
	public TweetFilter(TweetTime timeFrom, TweetTime timeTo,
			int nMinLength,
			int cMinWords,
			boolean bWipeOffNoisyWords,
			int cMinRetweets) {
		
		m_timeFrom = timeFrom;
		m_timeTo = timeTo;
		
		m_nMinLength = nMinLength;
		m_cMinWords = cMinWords;
		
		m_bWipeOffNoisyWords = bWipeOffNoisyWords;
		
		m_cMinRetweets = cMinRetweets;
	}
	
}
