package yt2443.ch23;

public class Similarity {

	public float m_fSimilarity;
	public float m_fSimilarity_1, m_fSimilarity_2;
	
	public Similarity(float fSimilarity, float fSimilarity_1, float fSimilarity_2) {
		
		m_fSimilarity = fSimilarity;
		m_fSimilarity_1 = fSimilarity_1;
		m_fSimilarity_2 = fSimilarity_2;
	}
	
	public String toString() {
		
		return "(" + m_fSimilarity + ", " +
				m_fSimilarity_1 + ", " + m_fSimilarity_2 + ")";
	}
}
