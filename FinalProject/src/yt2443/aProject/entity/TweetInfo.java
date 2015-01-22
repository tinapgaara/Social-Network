package yt2443.aProject.entity;

import yt2443.aProject.SnsException;

public class TweetInfo {

	public long m_lTweetId;
	public TweetTime m_time;
	public String m_strText, m_strText_Clear;
	public long m_lRetweetedId;
	
	public int m_nPositiveSentiment, m_nNegativeSentiment;
	
	public TweetInfo(long lTweetId) {
		
		this(lTweetId, (TweetTime) null, null, null, 0);
	}
	
	public TweetInfo(long lTweetId, String strOriginalTime, String strText)
			throws SnsException {
		
		this(lTweetId, TweetTime.parseOriginalTime(strOriginalTime), strText, null, 0);
	}
	
	public TweetInfo(long lTweetId, String strMediumTime, String strText, String strText_Clear, long lRetweetedId)
			throws SnsException {
		
		this(lTweetId, TweetTime.parseMediumTime(strMediumTime), strText, strText_Clear, lRetweetedId);
	}
	
	public TweetInfo(long lTweetId, TweetTime time, String strText, String strText_Clear, long lRetweetedId) {
		
		m_lTweetId = lTweetId;
		m_time = time;
		m_strText = strText;
		
		m_strText_Clear = strText_Clear;
		
		m_lRetweetedId = lRetweetedId;
		
		m_nPositiveSentiment = 0;
		m_nNegativeSentiment = 0;
	}
	
	public void setRetweetedId(long lRetweetedId) {
		
		m_lRetweetedId = lRetweetedId;
		
		if (m_lRetweetedId != 0) {
		    m_strText = null;
		}
	}
	
	public TweetInfo copy() {
		
		return new TweetInfo(m_lTweetId, m_time, m_strText, m_strText_Clear, m_lRetweetedId);
	}

	public void release() {
		
		m_time = null;
		m_strText = null;
		m_strText_Clear = null;
	}

}
