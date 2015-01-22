package yt2443.aProject.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TweetRelationSet {

	private Map<Long, Set<Long>> m_mapRetweetIds;
	private Map<Long, Long> m_mapRetweetRelations;
	
	public TweetRelationSet() {
		
		m_mapRetweetIds = null;
		m_mapRetweetRelations = null;
	}
	
	public Map<Long, Set<Long>> getRetweetIds() {
		
		return m_mapRetweetIds;
	}

	public Set<Long> getRetweetIds(Long longRetweetedId) {
		
		if (m_mapRetweetIds == null) {
			return null;
		}
		
		return m_mapRetweetIds.get(longRetweetedId);
	}

	public Long getRetweetedId(Long longTweetId) {
		
		if (m_mapRetweetIds == null) {
			return null;
		}
		
		Long longRetweetedId_Found = null;
		
		Set<Long> setRetweetIds;
		for (Long longRetweetedId : m_mapRetweetIds.keySet()) {
			
			setRetweetIds = m_mapRetweetIds.get(longRetweetedId);
			if (setRetweetIds.contains(longTweetId)) {
				longRetweetedId_Found = longRetweetedId;
				break;
			}
		}
		
		return longRetweetedId_Found;
	}

	public Set<Long> getAllRetweetIds() {
		
		if (m_mapRetweetIds == null) {
			return null;
		}
		
		Set<Long> setAllRetweetIds = new HashSet<Long>();
		
		for (Set<Long> set : m_mapRetweetIds.values()) {
			if (set != null) {
				setAllRetweetIds.addAll(set);
			}
		}
		
		return setAllRetweetIds;
	}

	public void addRelation(Long longTweetId, Long longTweetId_retweeted) {
		
		Set<Long> setRetweetIds = null;
		if (m_mapRetweetIds == null) {
			m_mapRetweetIds = new HashMap<Long, Set<Long>>();
		}
		else {
			setRetweetIds = m_mapRetweetIds.get(longTweetId_retweeted);
		}
		
		if (setRetweetIds == null) {
			setRetweetIds = new HashSet<Long>();
			setRetweetIds.add(longTweetId);
			m_mapRetweetIds.put(longTweetId_retweeted, setRetweetIds);
		}
		else {
			setRetweetIds.add(longTweetId);
		}
		
		if (m_mapRetweetRelations == null) {
			m_mapRetweetRelations = new HashMap<Long, Long>();
		}
		m_mapRetweetRelations.put(longTweetId, longTweetId_retweeted);
	}

	public TweetRelationSet calcRetweetClosureOf(TweetSet tweetSet) {
		
		if (tweetSet == null) {
			return null;
		}
		
		Map<Long, TweetInfo> mapTweetInfos = tweetSet.getTweetInfos();
		if ( (mapTweetInfos == null) || mapTweetInfos.isEmpty() ) {
			return null;
		}
		
		return calcRetweetClosureOf(mapTweetInfos.keySet());
		
	}

	public TweetRelationSet calcRetweetClosureOf(Set<Long> setTweetIds) {
		
		TweetRelationSet ttClosure = new TweetRelationSet();
		ttClosure.m_mapRetweetIds = new HashMap<Long, Set<Long>>();
		
		Set<Long> setRetweetIds, setRetweetIds_New = null, set;
		int cRetweetIds, cRetweetIds_New;
		for (Long longTweetId : setTweetIds) {
			
			setRetweetIds = new HashSet<Long>();
			setRetweetIds.add(longTweetId);
			
			cRetweetIds = 1;
			while (true) {
				
				for (Long longRetweetId : setRetweetIds) {
					
					set = getRetweetIds(longRetweetId);
					if ( (set == null) || set.isEmpty() ) {
						continue;
					}
					
					if (setRetweetIds_New == null) {
						setRetweetIds_New = new HashSet<Long>();
					}
					setRetweetIds_New.addAll(set);
				}
				
				if (setRetweetIds_New != null) {
					setRetweetIds.addAll(setRetweetIds_New);
					setRetweetIds_New.clear();
				}
				
				cRetweetIds_New = setRetweetIds.size();
				if (cRetweetIds_New == cRetweetIds) {
					break;
				}
				
				cRetweetIds = cRetweetIds_New;
			}
			
			ttClosure.m_mapRetweetIds.put(longTweetId, setRetweetIds);
		}
		
		setRetweetIds_New.clear();
		setRetweetIds_New = null;
		
		return ttClosure;
	}

	public Set<Long> calcOriginClosureOf(Set<Long> setTweetIds) {
		
		if ( (setTweetIds == null) || setTweetIds.isEmpty() || 
				(m_mapRetweetIds == null) ) {
			
			return null;
		}
		
		Set<Long> setOriginTweetIds = new HashSet<Long>();
		for (Long longTweetId : setTweetIds) {
			
			setOriginTweetIds.add(calcOriginTweetOf(longTweetId));
		}
		
		return setOriginTweetIds;
		
		/*
		int cTweetIds = setTweetIds.size();
		int cTweetIds_New;
		
		Set<Long> setRetweetIds;
		while (true) {
			
			for (Long longRetweetedId : m_mapRetweetIds.keySet()) {
				
				setRetweetIds = m_mapRetweetIds.get(longRetweetedId);
				if (setRetweetIds == null) {
					continue;
				}
				
				if (isIntersect(setRetweetIds, setTweetIds)) {
					setTweetIds.add(longRetweetedId);
				}
			}
			
			cTweetIds_New = setTweetIds.size();
			if (cTweetIds_New == cTweetIds) {
				break;
			}
			
			cTweetIds = cTweetIds_New;
		}
		//*/
	}

	public Long calcOriginTweetOf(Long longTweetId) {
		
		if (m_mapRetweetRelations == null) {
			return longTweetId;
		}
		
		Long longOriginTweetId = longTweetId;
		
		Long longWork;
		while (true) {
			
			longWork = m_mapRetweetRelations.get(longOriginTweetId);
			if (longWork == null) {
				break;
			}
			
			longOriginTweetId = longWork;
		}
		
		return longOriginTweetId;
	}

	/*
	private boolean isIntersect(Set<Long> set_1, Set<Long> set_2) {
		
		if ( (set_1 == null) || (set_2 == null) ) {
			return false;
		}
		
		boolean bIntersect = false;
		
		for (Long longObj : set_2) {
			
			if (set_1.contains(longObj)) {
				bIntersect = true;
				break;
			}
		}
		
		return bIntersect;
	}
	//*/
	
	public void release() {
		
		if (m_mapRetweetIds != null) {
			
			m_mapRetweetIds.clear();
			m_mapRetweetIds = null;
		}
	}

}
