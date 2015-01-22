package yt2443.aProject.entity;


public class Opinion {

	private static final int LENGTH_SentimentVector = 10;
	private static final int MAX_ViewpointDifference = 9;
	
	private float[] m_arrVector;

	public Opinion() {
		
		m_arrVector = new float[LENGTH_SentimentVector];
		
		for (int i = 0; i < LENGTH_SentimentVector; i++) {
			m_arrVector[i] = 0;
		}
    }
	
	public Opinion(TweetInfo tweetInfo) {
		
		m_arrVector = new float[LENGTH_SentimentVector];
		
		for (int i = 0; i < LENGTH_SentimentVector; i++) {
			m_arrVector[i] = 0;
		}
		
		m_arrVector[tweetInfo.m_nPositiveSentiment + 4] = 0.5f;
		m_arrVector[5 - tweetInfo.m_nNegativeSentiment] = 0.5f;
    }
	
	public float calcSimilarity(Opinion another) {
		
		float fViewpointValue = calcViewpointValue();
		float fViewpointValue_Another = another.calcViewpointValue();
		
		
		float fViewpointDiff = Math.abs(fViewpointValue - fViewpointValue_Another);
		return (MAX_ViewpointDifference - fViewpointDiff) / MAX_ViewpointDifference; 
	}
	
	private float calcViewpointValue() {
		
		float fViewpointValue = 0;
		
		for (int nIndex = 0; nIndex < LENGTH_SentimentVector; nIndex++) {
			
			fViewpointValue += ( (nIndex + 1) * m_arrVector[nIndex] );
		}
	
		return fViewpointValue;
	}

	public void release() {
		
		m_arrVector = null;
	}

	public void accumulate(Opinion another, float fTopicRelevance) {
		
		for (int nIndex = 0; nIndex < LENGTH_SentimentVector; nIndex++) {
			m_arrVector[nIndex] += ( fTopicRelevance * another.m_arrVector[nIndex] );
		}
	}

	public void normalizeBy(float fNormalizeFactor) {
		
		for (int nIndex = 0; nIndex < LENGTH_SentimentVector; nIndex++) {
			m_arrVector[nIndex] = m_arrVector[nIndex] / fNormalizeFactor;
		}
	}

}
