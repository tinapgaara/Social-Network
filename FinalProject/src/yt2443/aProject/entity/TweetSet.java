package yt2443.aProject.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import yt2443.aProject.SnsException;

public class TweetSet {

	private Map<Long, TweetInfo> m_mapTweetInfos;
	
	/*
	private int m_cIgnoredTweets;
	private int m_cRetweetsDiscovered;
	private int m_cTweetsWithRTprefix;
	//*/
	
	public TweetSet() {
		
		m_mapTweetInfos = null;
		
		/*
		m_cIgnoredTweets = 0;
		m_cRetweetsDiscovered = 0;
		m_cTweetsWithRTprefix = 0;
		//*/
	}
	
	public Map<Long, TweetInfo> getTweetInfos() {
		
		return m_mapTweetInfos;
	}

	public int getNumOfTweets() {
		
		if (m_mapTweetInfos == null) {
		    return 0;
		}
		
		return m_mapTweetInfos.size();
	}
	
	public Object[] getAllTweetIds_InArray() {
		
		if (m_mapTweetInfos == null) {
		    return null;
		}
		
		return m_mapTweetInfos.keySet().toArray();
	}

	public Set<Long> getAllTweetIds_InSet() {
		
		if (m_mapTweetInfos == null) {
		    return null;
		}
		
		return m_mapTweetInfos.keySet();
	}

	public TweetInfo getTweetInfo(Long longTweetId) {
		
		if (m_mapTweetInfos == null) {
		    return null;
		}
		
		return m_mapTweetInfos.get(longTweetId);
	}

	public TweetInfo fetchOutTweet(Long longTweetId) {
		
		if (m_mapTweetInfos == null) {
		    return null;
		}
		
		return m_mapTweetInfos.remove(longTweetId);
	}

	/*
	public int getNumOfIgnoredTweets() {
		
		return m_cIgnoredTweets;
	}
	
	public int getNumOfRetweetsDiscovered() {
		
		return m_cRetweetsDiscovered;
	}
	
	public int getNumOfTweetsWithRTprefix() {
		
		return m_cTweetsWithRTprefix;
	}
	
	public float getPercentOfIgnoredTweets() {
		
		float fPercent = 0;
		
		int cTotalTweets = m_cIgnoredTweets;
		if (m_mapTweetInfos != null) {
			cTotalTweets += m_mapTweetInfos.size();
		}
		
		if (cTotalTweets > 0) {
			fPercent = (100.0f * m_cIgnoredTweets) / cTotalTweets;
		}
		
		return fPercent;
	}
	//*/

	public boolean contains(Long longTweetId) {
		
		if (m_mapTweetInfos != null) {
			
		    return (m_mapTweetInfos.get(longTweetId) != null);
		}
		
	    return false;
	}

	public void addTweet(Long longTweetId, String strTweetTime, String strText)
			throws SnsException {
		
		if (strText != null) {
			strText = strText.replace('"', ' ');
			strText = strText.replace('\r', ' ');
			strText = strText.replace('\n', ' ');
		}
		
		if (m_mapTweetInfos == null) {
			m_mapTweetInfos = new HashMap<Long, TweetInfo>();
			m_mapTweetInfos.put(longTweetId, 
					new TweetInfo(longTweetId.longValue(), strTweetTime, strText));
		}
		
		else {
		    TweetInfo tweetInfo = m_mapTweetInfos.get(longTweetId);
		    if (tweetInfo == null) {
				m_mapTweetInfos.put(longTweetId,
						new TweetInfo(longTweetId.longValue(), strTweetTime, strText));
		    }
		    else {
		    	tweetInfo.m_lTweetId = longTweetId.longValue();
		    	tweetInfo.m_time = TweetTime.parseOriginalTime(strTweetTime);
		    	tweetInfo.m_strText = strText;
		    }
		}
	}

	public void addTweet(Long longTweetId, String strMediumTime, String strText,
			long lRetweetedId)
			throws SnsException {
		
		if (m_mapTweetInfos == null) {
			m_mapTweetInfos = new HashMap<Long, TweetInfo>();
			m_mapTweetInfos.put(longTweetId, 
					new TweetInfo(longTweetId.longValue(),
					    strMediumTime, strText, null, lRetweetedId)); // strText_Clear = null
		}
		
		else {
		    TweetInfo tweetInfo = m_mapTweetInfos.get(longTweetId);
		    if (tweetInfo == null) {
				m_mapTweetInfos.put(longTweetId,
						new TweetInfo(longTweetId.longValue(),
							    strMediumTime, strText, null, lRetweetedId)); // strText_Clear = null
		    }
		    else {
		    	tweetInfo.m_lTweetId = longTweetId.longValue();
		    	tweetInfo.m_time = TweetTime.parseMediumTime(strMediumTime);
		    	tweetInfo.m_strText = strText;
		    	tweetInfo.m_lRetweetedId = lRetweetedId;
		    }
		}
	}

	public void addTweet(TweetInfo tweetInfo) {
		
		Long longTweetId = new Long(tweetInfo.m_lTweetId);
		
		if (m_mapTweetInfos == null) {
			m_mapTweetInfos = new HashMap<Long, TweetInfo>();
			m_mapTweetInfos.put(longTweetId, tweetInfo.copy());
		}
		
		else {
		    TweetInfo tweetInfo_2 = m_mapTweetInfos.get(longTweetId);
		    if (tweetInfo_2 == null) {
				m_mapTweetInfos.put(longTweetId, tweetInfo.copy());
		    }
		    else {
		    	tweetInfo_2.m_lTweetId = tweetInfo.m_lTweetId;
		    	tweetInfo_2.m_time = tweetInfo.m_time;
		    	tweetInfo_2.m_strText = tweetInfo.m_strText;
		    	tweetInfo_2.m_lRetweetedId = tweetInfo.m_lRetweetedId;
		    }
		}
	}

	public void addTweet_WithoutContent(Long longTweetId) {
		
		if (longTweetId == null) {
			return;
		}
		
		if (m_mapTweetInfos == null) {
			m_mapTweetInfos = new HashMap<Long, TweetInfo>();
		}
		
		if (m_mapTweetInfos.get(longTweetId) == null) {
			TweetInfo tweetInfo = new TweetInfo(longTweetId.longValue());
		    m_mapTweetInfos.put(longTweetId, tweetInfo);
		}
	}

	public void addTweets_WithoutContent(Set<Long> setTweetIds) {
		
		if (setTweetIds == null) {
			return;
		}
		
		if (m_mapTweetInfos == null) {
			m_mapTweetInfos = new HashMap<Long, TweetInfo>();
		}
		
		TweetInfo tweetInfo;
		for (Long longTweetId : setTweetIds) {
			
			if (m_mapTweetInfos.get(longTweetId) == null) {
			    tweetInfo = new TweetInfo(longTweetId.longValue());
			    m_mapTweetInfos.put(longTweetId, tweetInfo);
			}
		}
	}

	public void setRetweetRelation(Long longTweetId, Long longTweetId_retweeted) {
		
		if (m_mapTweetInfos == null) {
			return;
		}
		
	    TweetInfo tweetInfo = m_mapTweetInfos.get(longTweetId);
	    if (tweetInfo == null) {
			return;
		}
		
	    tweetInfo.setRetweetedId(longTweetId_retweeted.longValue());
	}

	public void removeTweets(List<Long> listTweetIds) {
		
		if ( (listTweetIds == null) || (m_mapTweetInfos == null) ) {
			return;
		}
		
		TweetInfo tweetInfo;
		for (Long longTweetId : listTweetIds) {
			tweetInfo = m_mapTweetInfos.remove(longTweetId);
			if (tweetInfo != null) {
				tweetInfo.release();
				tweetInfo = null;
			}
		}
	}

	/*
	public void clearTextOfRetweets(TweetRelationSet ttRelationSet) {
	
		if (m_mapTweetInfos == null) {
			return;
		}
		
		TweetInfo tweetInfo;
		/*
		Long longRetweetedId;
		////
		for (Long longTweetId : m_mapTweetInfos.keySet()) {
			
			tweetInfo = m_mapTweetInfos.get(longTweetId);
			if (tweetInfo.m_lRetweetedId != 0) {
				continue;
			}
			
			if ( (tweetInfo.m_strText != null) &&
					tweetInfo.m_strText.startsWith("RT") ) {
				System.out.println("tweet=[" + tweetInfo.m_strText + "]");
				m_cTweetsWithRTprefix++;
			}
			/*
			longRetweetedId = ttRelationSet.getRetweetedId(longTweetId);
			if (longRetweetedId == null) {
				if ( (tweetInfo.m_strText != null) &&
						tweetInfo.m_strText.startsWith("RT") ) {
					m_cTweetsWithRTprefix++;
				}
			}
			else {
				m_cRetweetsDiscovered++;
			    tweetInfo.setRetweetedId(longRetweetedId.longValue());
			}
			////
		}
	}
    //*/

	public void release() {
		
		if (m_mapTweetInfos != null) {
			
			for (TweetInfo TweetInfo : m_mapTweetInfos.values()) {	
				TweetInfo.release();
			}
			
			m_mapTweetInfos.clear();
			m_mapTweetInfos = null;
		}
	}

}
