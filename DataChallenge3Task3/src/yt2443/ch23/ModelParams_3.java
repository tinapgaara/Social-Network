package yt2443.ch23;

public class ModelParams_3 implements IModelParams {

	/*
	{100, 100, 200}
	[DBG] Number of links found = [92], correctLinks = [78], precision = [84.78260869565217%], F1 = [0.8125]
	[DBG] Number of links found = [89], correctLinks = [59], precision = [66.29213483146067%], F1 = [0.6243386243386243]
	[DBG] Number of links found = [91], correctLinks = [78], precision = [85.71428571428571%], F1 = [0.8210526315789474]
	//*/
	
	public static final float PARAM_WeightOfProb = 0.8f;
	public static final float PARAM_Smooth_NumOfLocations = 0.000025f;
	public static final long PARAM_Smooth_TimeDiff = 100000000000L;

	
	public float m_fWeightOfProb;
	
	public int m_nNumOfLocIds;
	public float m_fSmooth_NumOfLocations;
	
	public double m_dMaxDist;
	public long m_lSmooth_TimeDiff;
	
	public ModelParams_3(float fWeightOfProb, 
			int nNumOfLocIds, float fSmooth_NumOfLocations,
			double dMaxDist,
			long lSmooth_TimeDiff) {
		
		m_fWeightOfProb = fWeightOfProb;
		
		m_nNumOfLocIds = nNumOfLocIds;
		m_fSmooth_NumOfLocations = fSmooth_NumOfLocations;
		
		m_dMaxDist = dMaxDist;
		m_lSmooth_TimeDiff = lSmooth_TimeDiff;
	}
	
	public int getNumOfTimeUnits() {
		
		return 0;
	}
	public void setNumOfLocIds(int nNumOfLocIds) {
		
		m_nNumOfLocIds = nNumOfLocIds;
	}

	public void release() {
		// nothing to do here
	}

	
}
