package yt2443.aProject.entity;

import java.util.HashSet;
import java.util.Set;

public class UserSet {

	/*
	private Map<Long, UserInfo> m_mapUserInfos;
	//*/
	private Set<Long> m_setUsers;
	
	public UserSet() {
		
		m_setUsers = null;
		/*
		m_mapUserInfos = null;
		//*/
	}
	
	/*
	public Map<Long, UserInfo> getUserInfos() {
		
		return m_mapUserInfos;
	}
	//*/

	public Set<Long> getUserIds() {
		
		return m_setUsers;
	}
	
	public int getNumOfUsers() {
		
		if (m_setUsers == null) {
		    return 0;
		}
		
		return m_setUsers.size();
	}

	public void addUserIdIfNew(Long longUserId) {
		
		if (m_setUsers == null) {
			m_setUsers = new HashSet<Long>();
		}
		m_setUsers.add(longUserId);
		
		/*
		long lFollowerId = longFollowerId.longValue();
		
		if (m_mapUserInfos == null) {
			m_mapUserInfos = new HashMap<Long, UserInfo>();
			m_mapUserInfos.put(longFollowerId, new UserInfo(lFollowerId));
		}
		
		else {
		    UserInfo userInfo = m_mapUserInfos.get(longFollowerId);
		    if (userInfo == null) {
				m_mapUserInfos.put(longFollowerId, new UserInfo(lFollowerId));
		    }
		}
		//*/
	}

	public void addUserIdsIfNew(Set<Long> setUserIds) {
		
		if (m_setUsers == null) {
			m_setUsers = new HashSet<Long>();
		}
		
		m_setUsers.addAll(setUserIds);
	}
	/*
	public void addUser(Long longUserId, String strUserName,
			int nFollowersCount, int nFriendsCount, int nListedCount) {
		
		long lUserId = longUserId.longValue();
		
		if (m_mapUserInfos == null) {
			m_mapUserInfos = new HashMap<Long, UserInfo>();
			m_mapUserInfos.put(longUserId, new UserInfo(lUserId, strUserName,
					nFollowersCount, nFriendsCount, nListedCount));
		}
		
		else {
		    UserInfo userInfo = m_mapUserInfos.get(longUserId);
		    if (userInfo == null) {
				m_mapUserInfos.put(longUserId, new UserInfo(lUserId, strUserName,
						nFollowersCount, nFriendsCount, nListedCount));
		    }
		    else {
		    	userInfo.m_strUserName = strUserName;
		    	userInfo.m_nFollowersCount = nFollowersCount;
		    	userInfo.m_nFriendsCount = nFriendsCount;
		    	userInfo.m_nListedCount = nListedCount;
		    }
		}
	}
	//*/

	public boolean containAnyOf(Set<Long> setUserIds) {
		
		if ( (m_setUsers == null) || (setUserIds == null) ) {
		    return false;
		}
		
		boolean bContain = false;
		
		for (Long longUserId : setUserIds) {
			if (m_setUsers.contains(longUserId)) {
				bContain = true;
				break;
			}
		}
		
		return bContain;
	}

	public boolean contains(Long longUserId) {
		
		if ( (m_setUsers == null) || (longUserId == null) ) {
		    return false;
		}
		
		return m_setUsers.contains(longUserId);
	}

	public void removeUserIds(UserSet another) {
		
		if ( (m_setUsers == null) || (another == null) ) {
		    return;
		}
		
		m_setUsers.removeAll(another.m_setUsers);
	}

	public void release() {
		
		if (m_setUsers != null) {
			
			m_setUsers.clear();
			m_setUsers = null;
		}
		
		/*
		if (m_mapUserInfos != null) {
			
			for (UserInfo userInfo : m_mapUserInfos.values()) {	
				userInfo.release();
			}
			
			m_mapUserInfos.clear();
			m_mapUserInfos = null;
		}
		//*/
	}

}
