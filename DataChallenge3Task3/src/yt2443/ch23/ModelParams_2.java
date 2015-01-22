package yt2443.ch23;

public class ModelParams_2 implements IModelParams {

	/*
	{100, 100, 200}
	[DBG] Number of links found = [93], correctLinks = [85], precision = [91.39784946236558%], F1 = [0.8854166666666665]
	[DBG] Number of links found = [89], correctLinks = [75], precision = [84.26966292134831%], F1 = [0.8064516129032259]
	[DBG] Number of links found = [93], correctLinks = [79], precision = [84.94623655913979%], F1 = [0.8186528497409327]
	
	1000, 1000, 2000
	[DBG] Number of links found = [739], correctLinks = [581], precision = [78.61975642760487%], F1 = [0.7128834355828222]
	
	small dataset
	[DBG] Number of links found = [87], correctLinks = [72], precision = [82.75862068965517%], F1 = [0.7868852459016394]
	//*/
	
	public static final float PARAM_WeightOfProb = 0.8f;
	public static final float PARAM_Smooth_NumOfLocations = 0.000025f;
	public static final long PARAM_Smooth_TimeDiff = 100000000000L;

	
	public float m_fWeightOfProb;
	
	public int m_nNumOfLocIds;
	public float m_fSmooth_NumOfLocations;
	
	public double m_dMaxDist;
	public long m_lSmooth_TimeDiff;
	
	public ModelParams_2(float fWeightOfProb, 
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
