package yt2443.ch23;

import java.util.List;

public class Model_2 implements IModel {

	/*
	public static final long ONE_YEAR_InMillis = 31536000000L;
	//*/
	
	private ModelParams_2 m_params;
	
	public Model_2(ModelParams_2 params) {
		
		m_params = params;
	}
	
	public IModelParams getParams() {
		
		return m_params;
	}

	public Similarity calcSimilarity(UserInfo userInfo_test, UserInfo userInfo_train) {
		
		float fWeightOfProb = m_params.m_fWeightOfProb;
		
		float fSimilarity_1 = calcProb(userInfo_test, userInfo_train);
		float fSimilarity_2 = 0;
		if (fWeightOfProb < 1.0) {
			fSimilarity_2 = ( 1 / (1 + calcDist(userInfo_test, userInfo_train)) );
		}
		
		return new Similarity(fWeightOfProb * fSimilarity_1 + ((1 - fWeightOfProb)) * fSimilarity_2,
				fSimilarity_1,
				fSimilarity_2);
	}

	private float calcProb(UserInfo userInfo_test, UserInfo userInfo_train) {
		
		List<CheckinItem> listCheckinItems = userInfo_test.getCheckinItems();
		if ( (listCheckinItems == null) || listCheckinItems.isEmpty() ) {
			return 0;
		}
		
		float fProb = 0;
		for (CheckinItem item : listCheckinItems) {
			fProb = fProb + calcProb_ForTestCheckinItem(item, userInfo_train);
		}
		
		return fProb / listCheckinItems.size();
	}

	private float calcDist(UserInfo userInfo_test, UserInfo userInfo_train) {
		
		List<CheckinItem> listCheckinItems_test = userInfo_test.getCheckinItems();
		if ( (listCheckinItems_test == null) || listCheckinItems_test.isEmpty() ) {
			return Float.MAX_VALUE;
		}
		
		List<CheckinItem> listCheckinItems_train = userInfo_train.getCheckinItems();
		if ( (listCheckinItems_train == null) || listCheckinItems_train.isEmpty() ) {
			return Float.MAX_VALUE;
		}
		
		double dDist_sum = 0;
		double dDist, dDist_min;
		long lTimeDiff;
		double dTimeDiff;
		for (CheckinItem item_test : listCheckinItems_test) {
			
			dDist_min = -1;
			for (CheckinItem item_train : listCheckinItems_train) {
				
				lTimeDiff = Math.abs(item_test.m_lTime - item_train.m_lTime);
				/*
				dTimeDiff = ((double) Math.abs(lTimeDiff - ONE_YEAR_InMillis)) / m_params.m_lSmooth_TimeDiff;
				//*/
				dTimeDiff = ((double) lTimeDiff) / m_params.m_lSmooth_TimeDiff;
				
				dDist = item_test.calcDistance(item_train) * Math.pow(Math.E, dTimeDiff);
				
				if ( (dDist_min < 0) || (dDist < dDist_min) ) {
					dDist_min = dDist;
				}
			}
			
			dDist_sum += dDist_min;
		}
		
		dDist = dDist_sum / m_params.m_dMaxDist; // normalization
		
		return (float) (dDist / listCheckinItems_test.size());
	}

	private float calcProb_ForTestCheckinItem(CheckinItem item_test,
			UserInfo userInfo_train) {
		
		List<CheckinItem> listCheckinItems_train = userInfo_train.getCheckinItems();
		if ( (listCheckinItems_train == null) || listCheckinItems_train.isEmpty() ) {
			return 0;
		}
		
		float fProb = 0;
		
		int nLocIndex_test = item_test.m_nLocIndex;
			
		int cCheckinTimes = 0;
		int cItems_train = listCheckinItems_train.size();
			
		for (CheckinItem item_train : listCheckinItems_train) {
			if (item_train.m_nLocIndex == nLocIndex_test) {
				cCheckinTimes++;
			}
		}
		
		float fSmooth_NumOfLocations = m_params.m_fSmooth_NumOfLocations;
		fProb = (cCheckinTimes + fSmooth_NumOfLocations) / 
				(cItems_train + fSmooth_NumOfLocations * m_params.m_nNumOfLocIds);
		
		return fProb;
	}

	public void release() {
		
		if (m_params != null) {
			m_params.release();
			m_params = null;
		}
	}

}
