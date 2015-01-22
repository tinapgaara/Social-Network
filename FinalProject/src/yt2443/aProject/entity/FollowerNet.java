package yt2443.aProject.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FollowerNet {

	private Map<Long, Set<Long>> m_mapFollowedUserIds;
	private Map<Long, Set<Long>> m_mapFollowerIds;
	
	public FollowerNet() {
		
		m_mapFollowedUserIds = null;
		m_mapFollowerIds = null;
	}
	
	public Map<Long, Set<Long>> getFollowedUserIds() {
		
		return m_mapFollowedUserIds;
	}

	public Map<Long, Set<Long>> getFollowerIds() {
		
		return m_mapFollowerIds;
	}

	public void addFollower(Long longFollowerId, Long longFollowedUserId) {
		
		if (m_mapFollowedUserIds == null) {
			m_mapFollowedUserIds = new HashMap<Long, Set<Long>>();
			
			Set<Long> set = new HashSet<Long>();
			set.add(longFollowedUserId);
			m_mapFollowedUserIds.put(longFollowerId, set);
		}
		
		else {
			Set<Long> set = m_mapFollowedUserIds.get(longFollowerId);
		    if (set == null) {
				set = new HashSet<Long>();
				m_mapFollowedUserIds.put(longFollowerId, set);
		    }
			set.add(longFollowedUserId);
		}

		
		if (m_mapFollowerIds == null) {
			m_mapFollowerIds = new HashMap<Long, Set<Long>>();
			
			Set<Long> set = new HashSet<Long>();
			set.add(longFollowerId);
			m_mapFollowerIds.put(longFollowedUserId, set);
		}
		
		else {
			Set<Long> set = m_mapFollowerIds.get(longFollowedUserId);
		    if (set == null) {
				set = new HashSet<Long>();
				m_mapFollowerIds.put(longFollowedUserId, set);
		    }
			set.add(longFollowerId);
		}		
	}

	public void addFollowers(Long longFollowedUserId, Set<Long> setFollowerIds) {
		
		if ( (longFollowedUserId == null) || (setFollowerIds == null) ) {
			return;
		}
		
		for (Long longFollowerId : setFollowerIds) {
			addFollower(longFollowerId, longFollowedUserId);
		}
	}

	public void removeFollowRelations(Long longFollowedUserId,
			Set<Long> setFollowers_ToRemove) {
		
		if ( (setFollowers_ToRemove == null) || setFollowers_ToRemove.isEmpty() ) {
			return;
		}
		
		for (Long longFollowerId : setFollowers_ToRemove) {
			removeFollower(longFollowerId, longFollowedUserId);
		}
	}

	private void removeFollower(Long longFollowerId, Long longFollowedUserId) {
		
		if (m_mapFollowedUserIds != null) {
			Set<Long> set = m_mapFollowedUserIds.get(longFollowerId);
		    if (set != null) {
		    	set.remove(longFollowedUserId);
		    	if (set.isEmpty()) {
					m_mapFollowedUserIds.put(longFollowerId, null);
		    	}
		    }
		}

		
		if (m_mapFollowerIds != null) {
			Set<Long> set = m_mapFollowerIds.get(longFollowedUserId);
		    if (set != null) {
		    	set.remove(longFollowerId);
		    	if (set.isEmpty()) {
		    		m_mapFollowerIds.put(longFollowedUserId, null);
		    	}
		    }
		}
	}

	public UserSet getFollowersByFollowedUserIds(UserSet userSet_FollowedUserIds) {
		
		if ( (userSet_FollowedUserIds == null) || (m_mapFollowerIds == null) ) {
		    return null;
		}
		
		UserSet userSet_Followers = new UserSet();
		Set<Long> setFollowerIds;
		for (Long longFollowedUserId : m_mapFollowerIds.keySet()) {
			
			setFollowerIds = m_mapFollowerIds.get(longFollowedUserId);
			userSet_Followers.addUserIdsIfNew(setFollowerIds);
		}
		
		/*
		Set<Long> setFollowedUserIds;
		for (Long longFollowerId : m_mapFollowedUserIds.keySet()) {
			
			setFollowedUserIds = m_mapFollowedUserIds.get(longFollowerId);
			
			if (userSet_FollowedUserIds.containAnyOf(setFollowedUserIds)) {
				userSet_Followers.addUserIdIfNew(longFollowerId);
			}
		}
		//*/
		
		return userSet_Followers;
	}

	public Set<Long> getFollowersByFollowedUserId(Long longFollowedUserId) {
		
		if ( (longFollowedUserId == null) || (m_mapFollowerIds == null) ) {
		    return null;
		}
		
		return m_mapFollowerIds.get(longFollowedUserId);
	}

	public int getNumOfFollowers(Long longFollowedUserId) {
		
		if ( (longFollowedUserId == null) || (m_mapFollowerIds == null) ) {
		    return 0;
		}
		
		Set<Long> setFollowerIds = m_mapFollowerIds.get(longFollowedUserId);
		if (setFollowerIds == null) {
			return 0;
		}
		
		return setFollowerIds.size();
	}

	public int getMaxNumOfFollowedUserIds(Set<Long> setFollowerIds) {
		
		if ( (setFollowerIds == null) || setFollowerIds.isEmpty() ||
				(m_mapFollowedUserIds == null) || m_mapFollowedUserIds.isEmpty() ) {
			
			return 0;
		}
		
		int nMaxNumOfFollowedUserIds = 0;
		
		Set<Long> setFollowedUserIds;
		int cFollowedUserIds;
		for (Long longFollowerId : setFollowerIds) {
			
			setFollowedUserIds = m_mapFollowedUserIds.get(longFollowerId);
			if ( (setFollowedUserIds == null) || setFollowedUserIds.isEmpty() ) {
				continue;
			}
			
			cFollowedUserIds = setFollowedUserIds.size();
			if (cFollowedUserIds > nMaxNumOfFollowedUserIds) {
				nMaxNumOfFollowedUserIds = cFollowedUserIds;
			}
		}
		
		return nMaxNumOfFollowedUserIds;
	}

	public Set<Long> getFollowedUserIdsByFollowerIds(Long longFollowerId) {
		
		if ( (m_mapFollowedUserIds == null) || m_mapFollowedUserIds.isEmpty() ) {
			return null;
		}
		
		return m_mapFollowedUserIds.get(longFollowerId);
	}

	public void combine(FollowerNet another) {
		
		if ( (another == null) || (another.m_mapFollowerIds == null) ||
				another.m_mapFollowerIds.isEmpty() ) {
			return;
		}
		
		for (Long longFollowedUserId : another.m_mapFollowerIds.keySet()) {
			
			addFollowers(longFollowedUserId,
					another.getFollowersByFollowedUserId(longFollowedUserId));
		}
	}

	public Set<Long> getAllUserIds() {
		
		if ( (m_mapFollowerIds == null) || m_mapFollowerIds.isEmpty() ) {
			return null;
		}
		
		Set<Long> setUserIds = new HashSet<Long>();
		
		Set<Long> set;
		for (Long longFollowedUserId : m_mapFollowerIds.keySet()) {
			
			setUserIds.add(longFollowedUserId);
			
			set = m_mapFollowerIds.get(longFollowedUserId);
			if (set != null) {
				setUserIds.addAll(set);
			}
		}
		
		return setUserIds;
	}

	public void release() {
		
		if (m_mapFollowedUserIds != null) {
			
			for (Set<Long> set : m_mapFollowedUserIds.values()) {
				if (set != null) {
				    set.clear();
				}
			}
			
			m_mapFollowedUserIds.clear();
			m_mapFollowedUserIds = null;
		}

		if (m_mapFollowerIds != null) {
			
			for (Set<Long> set : m_mapFollowerIds.values()) {
				if (set != null) {
				    set.clear();
				}
			}
			
			m_mapFollowerIds.clear();
			m_mapFollowerIds = null;
		}
	}

}
