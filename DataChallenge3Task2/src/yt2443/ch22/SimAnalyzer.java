package yt2443.ch22;

import java.util.List;

public class SimAnalyzer {

	public static Similarity calcSimilarity(UserInfo userInfo_test,
			UserInfo userInfo_train, Params params) {
		
		float fWeightOfProb = params.m_fWeightOfProb;
		
		float fSimilarity_1 = calcProb(userInfo_test, userInfo_train, params);
		float fSimilarity_2 = 0;
		if (fWeightOfProb < 1.0) {
			fSimilarity_2 = ( 1 / (1 + calcDist(userInfo_test, userInfo_train, params)) );
		}
		/*
		if (fSimilarity_1 > 0.0001) {
		    Logger.showInfo("fSimilarity_1/2 = [" + fSimilarity_1 + ", " + fSimilarity_2 + "]");
		}
		//*/
		
		return new Similarity(fWeightOfProb * fSimilarity_1 + ((1 - fWeightOfProb)) * fSimilarity_2,
				fSimilarity_1,
				fSimilarity_2);
	}

	private static float calcProb(UserInfo userInfo_test,
			UserInfo userInfo_train, Params params) {
		
		List<CheckinItem> listCheckinItems = userInfo_test.getCheckinItems();
		if ( (listCheckinItems == null) || listCheckinItems.isEmpty() ) {
			return 0;
		}
		
		float fProb = 0;
		for (CheckinItem item : listCheckinItems) {
			fProb = fProb + calcProb_ForTestCheckinItem(item, userInfo_train, params);
		}
		
		return fProb / listCheckinItems.size();
	}

	private static float calcDist(UserInfo userInfo_test,
			UserInfo userInfo_train, Params params) {
		
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
		
		dDist = dDist_sum / params.m_dMaxDist; // normalization
		
		return (float) (dDist / listCheckinItems_test.size());
	}

	private static float calcProb_ForTestCheckinItem(CheckinItem item_test,
			UserInfo userInfo_train, Params params) {
		
		List<CheckinItem> listCheckinItems_train = userInfo_train.getCheckinItems();
		if ( (listCheckinItems_train == null) || listCheckinItems_train.isEmpty() ) {
			return 0;
		}
		
		float fProb = 0;
		
		int nLocIndex_test = item_test.m_nLocIndex;
			
		int cItems_train;
		int cCheckinTimes = 0;
		int nNumOfTimeUnits = params.m_nNumOfTimeUnits;
		
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
				
				if ( (item_test.m_sYMD == item_train.m_sYMD) &&
						(item_test.m_byTimeUnit == item_train.m_byTimeUnit) ) {
					
					cItems_train++;
					if (item_train.m_nLocIndex == nLocIndex_test) {
						cCheckinTimes++;
					}
			    }
			}
		}
		
		float fSmooth_NumOfLocations = params.m_fSmooth_NumOfLocations;
		fProb = (cCheckinTimes + fSmooth_NumOfLocations) / 
				(cItems_train + fSmooth_NumOfLocations * params.m_nNumOfLocIds);
		
		return fProb;
	}

	/**************************************************************************
	private static boolean isSameTimeUnit(long lTime_1, long lTime_2, int nNumOfTimeUnits) {
		
		Calendar calendar_1 = Calendar.getInstance();
		calendar_1.setTimeInMillis(lTime_1);
		
		Calendar calendar_2 = Calendar.getInstance();
		calendar_2.setTimeInMillis(lTime_2);
		
		if ( (calendar_1.get(Calendar.YEAR) != calendar_2.get(Calendar.YEAR)) ||
			 (calendar_1.get(Calendar.MONTH) != calendar_2.get(Calendar.MONTH)) ||
			 (calendar_1.get(Calendar.DATE) != calendar_2.get(Calendar.DATE)) ) {
			
			return false;
		}
		
		int nHour_1 = calendar_1.get(Calendar.HOUR_OF_DAY);
		int nTimeUnit_1 = calcTimeUnit(nHour_1, nNumOfTimeUnits);
		
		int nHour_2 = calendar_2.get(Calendar.HOUR_OF_DAY);
		int nTimeUnit_2 = calcTimeUnit(nHour_2, nNumOfTimeUnits);
		
		return (nTimeUnit_1 == nTimeUnit_2);
	}

	private static int calcTimeUnit(int nHour, int nNumOfTimeUnits) {
		
		int cHoursPerUnit = 24 / nNumOfTimeUnits;		
		return (int) (nHour / cHoursPerUnit);
	}
	//************************************************************************/

}
