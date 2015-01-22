package yt2443.ch23;

import java.util.List;

public class Model_1 implements IModel {

	private ModelParams_1 m_params;
	
	public Model_1(ModelParams_1 params) {
		
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
		for (CheckinItem item_test : listCheckinItems_test) {
			
			dDist_min = -1;
			for (CheckinItem item_train : listCheckinItems_train) {
				dDist = item_test.calcDistance(item_train);
				
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
			
		int cItems_train;
		int cCheckinTimes = 0;
		int nNumOfTimeUnits = m_params.m_nNumOfTimeUnits;
		
		if (nNumOfTimeUnits == 0) { // no time divide
			
			cItems_train = listCheckinItems_train.size();
			
			for (CheckinItem item_train : listCheckinItems_train) {
				if (item_train.m_nLocIndex == nLocIndex_test) {
					cCheckinTimes++;
				}
			}
		}
		
		else {
			
			cItems_train = 0;
			
			for (CheckinItem item_train : listCheckinItems_train) {
				
				if ( (item_test.m_byMonth == item_train.m_byMonth) &&
						(item_test.m_byDay == item_train.m_byDay) &&
						(item_test.m_byTimeUnit == item_train.m_byTimeUnit) ) {
					
					cItems_train++;
					if (item_train.m_nLocIndex == nLocIndex_test) {
						cCheckinTimes++;
					}
			    }
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
