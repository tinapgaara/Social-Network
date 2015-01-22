package yt2443.ch23;

public class ModelParams_1 implements IModelParams {

	/*
	[DBG] Number of links found = [94], correctLinks = [81], precision = [86.17021276595744%], F1 = [0.8350515463917526]
	[DBG] Number of links found = [48], correctLinks = [40], precision = [83.33333333333334%], F1 = [0.816326530612245]
	//*/
	
	public static final float PARAM_WeightOfProb = 0.8f;
	public static final int PARAM_NumOfTimeUnits = 0;
	public static final float PARAM_Smooth_NumOfLocations = 0.000025f;

	
	public float m_fWeightOfProb;
	
	public int m_nNumOfTimeUnits;
	
	public int m_nNumOfLocIds;
	public float m_fSmooth_NumOfLocations;
	
	public double m_dMaxDist;
	
	public ModelParams_1(float fWeightOfProb, 
			int nNumOfTimeUnits,
			int nNumOfLocIds, float fSmooth_NumOfLocations,
			double dMaxDist) {
		
		m_fWeightOfProb = fWeightOfProb;
		
		m_nNumOfTimeUnits = nNumOfTimeUnits;
		
		m_nNumOfLocIds = nNumOfLocIds;
		m_fSmooth_NumOfLocations = fSmooth_NumOfLocations;
		
		m_dMaxDist = dMaxDist;
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
