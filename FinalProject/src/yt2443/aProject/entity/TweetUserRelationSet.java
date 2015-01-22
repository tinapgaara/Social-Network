package yt2443.aProject.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TweetUserRelationSet {

	private Map<Long, Set<Long>> m_mapTweet2Users;
	
	public TweetUserRelationSet() {
		
		m_mapTweet2Users = null;
	}
	
	public Map<Long, Set<Long>> getTweet2Users() {
		
		return m_mapTweet2Users;
	}

	public Set<Long> getUserIdsByTweetId(Long longTweetId) {
		
		if (m_mapTweet2Users == null) {
		    return null;
		}
		
		return m_mapTweet2Users.get(longTweetId);
	}

	public void addRelation(Long longTweetId, Long longUserId) {
		
		Set<Long> set;
		
		if (m_mapTweet2Users == null) {
			m_mapTweet2Users = new HashMap<Long, Set<Long>>();
			
			set = new HashSet<Long>();
			set.add(longUserId);
			m_mapTweet2Users.put(longTweetId, set);
		}
		else {
			set = m_mapTweet2Users.get(longUserId);
			if (set == null) {
				set = new HashSet<Long>();
				set.add(longUserId);
				m_mapTweet2Users.put(longTweetId, set);
			}
			else {
				set.add(longUserId);
			}
		}
	}

	public void release() {
		
		if (m_mapTweet2Users != null) {
			
			for (Set<Long> set : m_mapTweet2Users.values()) {		
				set.clear();
			}
			
			m_mapTweet2Users.clear();
			m_mapTweet2Users = null;
		}
	}

}
