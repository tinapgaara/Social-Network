package yt2443.aProject.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TestResult_ForTweet {

	private Long m_longTweetId_Target;
	
	private Set<Long> m_setCorrectUserIds;
	
	private List<UserIdWithScore> m_listUserIdWithScores;
	
	public TestResult_ForTweet(Long longTweetId_Target) {
		
		m_longTweetId_Target = longTweetId_Target;
		
		m_setCorrectUserIds = null;
		m_listUserIdWithScores = null;
	}
	
	public Long getTweetId_Target() {
		
		return m_longTweetId_Target;
	}

	public Set<Long> getCorrectUserIds() {
		
		return m_setCorrectUserIds;
	}

	public void addCorrectUserIds(Set<Long> setUserIds) {
		
		if (m_setCorrectUserIds == null) {
			m_setCorrectUserIds = new HashSet<Long>();
		}
		
		m_setCorrectUserIds.addAll(setUserIds);
	}
	
	public List<UserIdWithScore> getUserIdWithScores() {
		
		return m_listUserIdWithScores;
	}
	
	public void addUserScore(Long longUserId, float fScore) {
		
		if (m_listUserIdWithScores == null) {
			m_listUserIdWithScores = new ArrayList<UserIdWithScore>();
		}
		
		m_listUserIdWithScores.add( new UserIdWithScore(longUserId, fScore) );
	}

	public void sortByScores() {
		
		Comparator<UserIdWithScore> comparator = new TestResultComparator_ByScore();
		
		Collections.sort(m_listUserIdWithScores, comparator);
	}
	
	public void cutTail(int cTotalUsers, float fResultScope) {
		
		int cUserIds_Keep = (int) Math.ceil(cTotalUsers * fResultScope);
		
		if ( (m_listUserIdWithScores == null) ||
				(m_listUserIdWithScores.size() <= cUserIds_Keep) ) {
			
			return;
		}
		
		Set<UserIdWithScore> setUsers_InTail = new HashSet<UserIdWithScore>();
		int nOrderNo;
		for (UserIdWithScore uiws : m_listUserIdWithScores) {
			
			nOrderNo = getOrderNoOf(uiws.m_longUserId);
			if (nOrderNo > cUserIds_Keep) {
				setUsers_InTail.add(uiws);
			}
		}
		
		if ( ! setUsers_InTail.isEmpty() ) {
		    m_listUserIdWithScores.removeAll(setUsers_InTail);
		}
	}

	public int getOrderNoOf(Long longUserId) {
		
		if (m_listUserIdWithScores == null) {
			return 0;
		}
		
		int nOrderNo = 0;
		
		int nPos = 0;
		for (UserIdWithScore uiws : m_listUserIdWithScores) {
			
			nPos++;
			
			if (uiws.m_longUserId.longValue() == longUserId.longValue()) {
				nOrderNo = nPos;
				break;
			}
		}
		
		return nOrderNo;
	}

	public void release() {
		
		m_longTweetId_Target = null;
		
		if (m_setCorrectUserIds != null) {
			m_setCorrectUserIds.clear();
			m_setCorrectUserIds = null;
		}

		if (m_listUserIdWithScores == null) {
			m_listUserIdWithScores.clear();
			m_listUserIdWithScores = null;
		}
	}

}
