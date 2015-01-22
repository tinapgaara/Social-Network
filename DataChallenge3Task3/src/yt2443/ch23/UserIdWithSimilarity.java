package yt2443.ch23;

public class UserIdWithSimilarity {

	public int m_nUserId;
	public float m_fSimilarity;
	//*
	public float m_fSimilarity_1, m_fSimilarity_2;
	//*/
	
	private boolean m_bDeprivedFlag;

	
	public UserIdWithSimilarity(int nUserId, float fSimilarity,
			float fSimilarity_1, float fSimilarity_2) {
		
		m_nUserId = nUserId;
		m_fSimilarity = fSimilarity;
		
		//*
		m_fSimilarity_1 = fSimilarity_1;
		m_fSimilarity_2 = fSimilarity_2;
		//*/
		
		m_bDeprivedFlag = false;
	}

	public void setDeprivedFlag(boolean bDeprivedFlag) {
		
		m_bDeprivedFlag = bDeprivedFlag;
	}
	
	public boolean isDeprived() {
		
		return m_bDeprivedFlag;
	}
	
}
