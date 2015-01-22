package yt2443.ch23;

public class ModelParams_4 implements IModelParams {

	/*
	[DBG] Number of links found = [94], correctLinks = [81], precision = [86.17021276595744%], F1 = [0.839378238341969]
	[DBG] Number of links found = [91], correctLinks = [78], precision = [85.71428571428571%], F1 = [0.8167539267015708]
	//*/
	
	public static final float PARAM_WeightOfProb = 0.8f;
	public static final float PARAM_Smooth_NumOfLocations = 0.000025f;
	public static final long PARAM_Smooth_TimeDiff = 100000000000L;
	
	public static final int PARAM_NumOfTimeUnits = 4;

	
	public float m_fWeightOfProb;
	
	public int m_nNumOfLocIds;
	public float m_fSmooth_NumOfLocations;
	
	public double m_dMaxDist;
	public long m_lSmooth_TimeDiff;
	
	public int m_nNumOfTimeUnits;
	
	
	public ModelParams_4(float fWeightOfProb, 
			int nNumOfLocIds, float fSmooth_NumOfLocations,
			double dMaxDist,
			long lSmooth_TimeDiff,
			int nNumOfTimeUnits) {
		
		m_fWeightOfProb = fWeightOfProb;
		
		m_nNumOfLocIds = nNumOfLocIds;
		m_fSmooth_NumOfLocations = fSmooth_NumOfLocations;
		
		m_dMaxDist = dMaxDist;
		m_lSmooth_TimeDiff = lSmooth_TimeDiff;
		
		m_nNumOfTimeUnits = nNumOfTimeUnits;
	}
	
	public int getNumOfTimeUnits() {
		
		return m_nNumOfTimeUnits;
	}
	
	public void setNumOfLocIds(int nNumOfLocIds) {
		
		m_nNumOfLocIds = nNumOfLocIds;
	}

	public void release() {
		// nothing to do here
	}

	
}
