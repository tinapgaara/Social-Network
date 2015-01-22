package yt2443.aProject.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserTweetRelationSet {

	private Map<Long, Set<Long>> m_mapUser2Tweets;
	/*
	private Map<Long, Set<Long>> m_mapTweet2Users;
	//*/
	
	public UserTweetRelationSet() {
		
		m_mapUser2Tweets = null;
		/*
		m_mapTweet2Users = null;
		//*/
	}
	
	public Map<Long, Set<Long>> getUser2Tweets() {
		
		return m_mapUser2Tweets;
	}

	public Set<Long> getTweetIdsByUserId(Long longUserId) {
		
		if (m_mapUser2Tweets == null) {
		    return null;
		}
		
		return m_mapUser2Tweets.get(longUserId);
	}

	public Set<Long> getUserIdsByTweetId(Long longTweetId) {
		
		if (m_mapUser2Tweets == null) {
			return null;
		}
		
		Set<Long> setUserIds = new HashSet<Long>();
		
		Set<Long> setTweetIds;
		for (Long longUserId : m_mapUser2Tweets.keySet()) {
			
			setTweetIds = m_mapUser2Tweets.get(longUserId);
			if ( (setTweetIds != null) && setTweetIds.contains(longTweetId) ) {
				
				setUserIds.add(longUserId);
			}
		}
		
		return setUserIds;
	}

	public void addRelation(Long longUserId, Long longTweetId) {
		
		Set<Long> set;
		
		if (m_mapUser2Tweets == null) {
			m_mapUser2Tweets = new HashMap<Long, Set<Long>>();
			
			set = new HashSet<Long>();
			set.add(longTweetId);
			m_mapUser2Tweets.put(longUserId, set);
		}
		else {
			set = m_mapUser2Tweets.get(longUserId);
			if (set == null) {
				set = new HashSet<Long>();
				set.add(longTweetId);
				m_mapUser2Tweets.put(longUserId, set);
			}
			else {
				set.add(longTweetId);
			}
		}

		/*
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
		//*/
	}

	public void addOriginTweetIds(TweetRelationSet tweetRelationSet) {
		
		if ( (m_mapUser2Tweets == null) || (tweetRelationSet == null) ) {
			return;
		}
		
		Set<Long> setTweetIds;
		Long longOriginTweetId;
		
		Set<Long> setOriginTweetIds = new HashSet<Long>();
		
		for (Long longUserId :m_mapUser2Tweets.keySet()) {
			
			setTweetIds = m_mapUser2Tweets.get(longUserId);
			if (setTweetIds == null) {
				continue;
			}
			
			for (Long longTweetId : setTweetIds) {
				
				longOriginTweetId = tweetRelationSet.calcOriginTweetOf(longTweetId);
				if (longOriginTweetId.longValue() != longTweetId.longValue()) {
					setOriginTweetIds.add(longOriginTweetId);
				}
			}
			
			if ( ! setOriginTweetIds.isEmpty() ) {
				
				setTweetIds.addAll(setOriginTweetIds);
				
				setOriginTweetIds.clear();
			}
		}
		
		setOriginTweetIds.clear();
		setOriginTweetIds = null;
	}

	public void release() {
		
		if (m_mapUser2Tweets != null) {
			
			for (Set<Long> set : m_mapUser2Tweets.values()) {		
				set.clear();
			}
			
			m_mapUser2Tweets.clear();
			m_mapUser2Tweets = null;
		}
		
		/*
		if (m_mapTweet2Users != null) {
			
			for (Set<Long> set : m_mapTweet2Users.values()) {		
				set.clear();
			}
			
			m_mapTweet2Users.clear();
			m_mapTweet2Users = null;
		}
		//*/
	}

}
