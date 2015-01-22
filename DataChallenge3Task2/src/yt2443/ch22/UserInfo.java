package yt2443.ch22;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

	private List<CheckinItem> m_listCheckinItems;

	private int m_nMaxNumOfCandidateLinks;
	private int m_cCandidateLinks;
	private UserIdWithSimilarity[] m_arrCandidateLinks;
	
	public UserInfo() {
		
		this(null);
	}
	
	public UserInfo(List<CheckinItem> listCheckinItems) {
		
		m_listCheckinItems = listCheckinItems;
		
		m_nMaxNumOfCandidateLinks = 1; // default to 1
		m_cCandidateLinks = 0;
		m_arrCandidateLinks = null;
	}
	
	public boolean isEmpty() {
		
		boolean bEmpty = (m_listCheckinItems == null) ||
				( m_listCheckinItems.isEmpty() );
		
		return bEmpty;
	}

	public void addCheckinItem(CheckinItem item) {
		
		if (m_listCheckinItems == null) {
			m_listCheckinItems = new ArrayList<CheckinItem>();
		}
		m_listCheckinItems.add(item);
	}
	
	public List<CheckinItem> getCheckinItems() {
		
		return m_listCheckinItems;
	}
	
	public int getNumOfCheckinItems() {
		
		if (m_listCheckinItems == null) {
			return 0;
		}
		
		return m_listCheckinItems.size();
	}
	
	public void setMaxNumOfCandidateLinks(int nMaxNumOfCandidateLinks) {
		
		m_nMaxNumOfCandidateLinks = nMaxNumOfCandidateLinks;
	}

	public int getNumOfCandidateLink() {
		
		return m_cCandidateLinks;
	}
	
	public UserIdWithSimilarity getCandidateLinkByOrder(int nOrderIndex) {
		
		return m_arrCandidateLinks[nOrderIndex];
	}
	
	public UserIdWithSimilarity getCandidateLinkByUserId(int nUserId) {
		
		UserIdWithSimilarity uiws = null;
		
		for (int i = 0; i < m_cCandidateLinks; i++) {
			uiws = m_arrCandidateLinks[i];
			if (uiws.m_nUserId == nUserId) {
				break;
			}
		}
		
		return uiws;
	}

	/**************************************************************************
	public void addCandidateLinkWithSimilarity(int nUserId_train, float fSimilarity) {
		
		if (m_arrCandidateLinks == null) {
			
			m_arrCandidateLinks = new UserIdWithSimilarity[m_nMaxNumOfCandidateLinks];
			m_arrCandidateLinks[0] = new UserIdWithSimilarity(nUserId_train, fSimilarity);
			m_cCandidateLinks = 1;
			
			return;
		}
		
		int nRightMostPos;
		boolean bCandidateWithMinSimilarity = true;
		for (int i = 0; i < m_cCandidateLinks; i++) {
			
			if (fSimilarity > m_arrCandidateLinks[i].m_fSimilarity) {
				
				if (m_cCandidateLinks < m_nMaxNumOfCandidateLinks) {
					nRightMostPos = m_cCandidateLinks;
					m_cCandidateLinks++;
				}
				else {
					nRightMostPos = m_nMaxNumOfCandidateLinks - 1;
				}				
				for (int j = nRightMostPos; j > i; j--) {
					m_arrCandidateLinks[j] = m_arrCandidateLinks[j - 1];
				}	
				m_arrCandidateLinks[i] = new UserIdWithSimilarity(nUserId_train, fSimilarity);
				bCandidateWithMinSimilarity = false;
				break;
			}
		}
		
		if ( bCandidateWithMinSimilarity && (m_cCandidateLinks < m_nMaxNumOfCandidateLinks) ) {
			m_arrCandidateLinks[m_cCandidateLinks] = new UserIdWithSimilarity(
					nUserId_train, fSimilarity);
			m_cCandidateLinks++;
		}
	}
	**************************************************************************/

	public void addCandidateLinkWithSimilarity(int nUserId_train, 
			Similarity similarity) {
		
		if (m_arrCandidateLinks == null) {
			
			m_arrCandidateLinks = new UserIdWithSimilarity[m_nMaxNumOfCandidateLinks];
			m_arrCandidateLinks[0] = new UserIdWithSimilarity(nUserId_train,
					similarity.m_fSimilarity, similarity.m_fSimilarity_1, similarity.m_fSimilarity_2);
			m_cCandidateLinks = 1;
			
			return;
		}
		
		int nRightMostPos;
		boolean bCandidateWithMinSimilarity = true;
		for (int i = 0; i < m_cCandidateLinks; i++) {
			
			if (similarity.m_fSimilarity > m_arrCandidateLinks[i].m_fSimilarity) {
				
				if (m_cCandidateLinks < m_nMaxNumOfCandidateLinks) {
					nRightMostPos = m_cCandidateLinks;
					m_cCandidateLinks++;
				}
				else {
					nRightMostPos = m_nMaxNumOfCandidateLinks - 1;
				}				
				for (int j = nRightMostPos; j > i; j--) {
					m_arrCandidateLinks[j] = m_arrCandidateLinks[j - 1];
				}	
				m_arrCandidateLinks[i] = new UserIdWithSimilarity(nUserId_train,
						similarity.m_fSimilarity, similarity.m_fSimilarity_1, similarity.m_fSimilarity_2);
						
				bCandidateWithMinSimilarity = false;
				break;
			}
		}
		
		if ( bCandidateWithMinSimilarity && (m_cCandidateLinks < m_nMaxNumOfCandidateLinks) ) {
			
			m_arrCandidateLinks[m_cCandidateLinks] = new UserIdWithSimilarity(nUserId_train,
					similarity.m_fSimilarity, similarity.m_fSimilarity_1, similarity.m_fSimilarity_2);
			
			m_cCandidateLinks++;
		}
	}
	
	public Similarity getCandidateSimilarityByUserId(int nUserId) {
		
		if (m_arrCandidateLinks == null) {
		    return null;
		}
		
		Similarity similarity = null;
		
		for (int i = 0; i < m_cCandidateLinks; i++) {
			if (m_arrCandidateLinks[i].m_nUserId == nUserId) {
			    similarity = new Similarity(m_arrCandidateLinks[i].m_fSimilarity,
			    		m_arrCandidateLinks[i].m_fSimilarity_1,
			    		m_arrCandidateLinks[i].m_fSimilarity_2);
			    break;
			}
		}
		
		return similarity;
	}

	public void clearCandidateLinks() {
		
		for (int i = 0; i < m_cCandidateLinks; i++) {
			
			m_arrCandidateLinks[i] = null;
		}
		
		m_cCandidateLinks = 0;
	}

	public void resetMaxNumOfCandidateLinks(int nMaxNumOfCandidateLinks_new) {
		
		if (m_arrCandidateLinks != null) {
			for (int i = 0; i < m_cCandidateLinks; i++) {
				m_arrCandidateLinks[i] = null;
			}
		}
		
		m_cCandidateLinks = 0;
		
		if (m_nMaxNumOfCandidateLinks != nMaxNumOfCandidateLinks_new) {
			m_nMaxNumOfCandidateLinks = nMaxNumOfCandidateLinks_new;
			m_arrCandidateLinks = new UserIdWithSimilarity[m_nMaxNumOfCandidateLinks];
		}
	}

	public void release() {
		
		if (m_listCheckinItems != null) {
			
			/*
			for (CheckinItem item : m_listCheckinItems) {
				item.release();
			}
			//*/
			
			m_listCheckinItems.clear();
			m_listCheckinItems = null;
		}
		
		m_cCandidateLinks = 0;
		m_arrCandidateLinks = null;
	}

	public double calcMaxDist(UserInfo another) {
		
		double dMaxDist = 0;
		
		double dDist;
		
		for (CheckinItem item : m_listCheckinItems) {
			
			for (CheckinItem item_another : another.m_listCheckinItems) {
				dDist = item.calcDistance(item_another);
				if (dMaxDist < dDist) {
					dMaxDist = dDist;
				}
			}	
		}
		
		return dMaxDist;
	}

}
