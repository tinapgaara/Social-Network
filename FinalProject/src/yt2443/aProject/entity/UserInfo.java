package yt2443.aProject.entity;

public class UserInfo {

	public long m_lUserId;
	public String m_strUserName;
	
	public int m_nFollowersCount, m_nFriendsCount, m_nListedCount;
	
	public UserInfo(long lUserId) {
		
		this(lUserId, null, 0, 0, 0);
	}
	
	public UserInfo(long lUserId, String strUserName, 
			int nFollowersCount, int nFriendsCount, int nListedCount) {
		
		m_lUserId = lUserId;
		m_strUserName = strUserName;
		
		m_nFollowersCount = nFollowersCount;
		m_nFriendsCount = nFriendsCount;
		m_nListedCount = nListedCount;
	}
	
	public void release() {
		
		m_strUserName = null;
	}

}
