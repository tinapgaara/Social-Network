package yt2443.ch22;

public class Params {

	/*
	[INFO] Number of links found = [15845], correctLinks = [12922], 81.55%, F1 = [0.3884504967609085]
	public static final float PARAM_WeightOfProb = 0.8f;
	public static final float PARAM_Smooth_NumOfLocations = 0.25f;
	public static final float PARAM_Smooth_Social = 0.1f;
	public static final int PARAM_NumOfTimeUnits = 0;
	public static final float PARAM_MaxDist = 375.2149800447738f;
    //*/
	
	/*
	[INFO] Number of links found = [878], correctLinks = [806], precision = [0.9179954441913439], F1 = [0.8837719298245614]
    [INFO] Number of links found = [875], correctLinks = [806], precision = [0.9211428571428572], F1 = [0.8789531079607416]
    [INFO] Number of links found = [477], correctLinks = [449], precision = [0.9412997903563941], F1 = [0.9267285861713106]
    [INFO] Number of links found = [478], correctLinks = [422], precision = [0.8828451882845189], F1 = [0.8701030927835051]
    [INFO] Number of links found = [474], correctLinks = [428], precision = [0.9029535864978903], F1 = [0.8861283643892339]
    [INFO] Number of links found = [910], correctLinks = [809], precision = [0.889010989010989], F1 = [0.8670953912111469]
    [INFO] Number of links found = [1730], correctLinks = [1500], precision = [0.8670520231213873], F1 = [0.8441193021947102]
    [INFO] Number of links found = [905], correctLinks = [804], precision = [0.8883977900552487], F1 = [0.8682505399568036]
	public static final float PARAM_WeightOfProb = 0.5f;
	public static final float PARAM_Smooth_NumOfLocations = 0.000025f;
	public static final float PARAM_Smooth_Social = 0.1f;
	public static final int PARAM_NumOfTimeUnits = 0;
	public static final float PARAM_MaxDist = 1;
	//*/
	
	/*
	public static final float PARAM_WeightOfProb = 0.8f;
	[INFO] Number of links found = [474], correctLinks = [439], precision = [0.9261603375527426], F1 = [0.9164926931106473]
	[INFO] Number of links found = [99], correctLinks = [98], precision = [0.98989898989899], F1 = [0.9849246231155778]
    [INFO] Number of links found = [100], correctLinks = [98], precision = [0.98], F1 = [0.98]
    [INFO] Number of links found = [95], correctLinks = [92], precision = [0.968421052631579], F1 = [0.9484536082474226]
    [INFO] Number of links found = [195], correctLinks = [187], precision = [0.958974358974359], F1 = [0.9516539440203563]
    [INFO] Number of links found = [194], correctLinks = [190], precision = [0.979381443298969], F1 = [0.9743589743589743]
    [INFO] Number of links found = [482], correctLinks = [455], precision = [0.9439834024896265], F1 = [0.9391124871001032]
    [INFO] Number of links found = [932], correctLinks = [859], precision = [0.9216738197424893], F1 = [0.908994708994709]
    [INFO] Number of links found = [947], correctLinks = [874], precision = [0.9229144667370645], F1 = [0.9123173277661796]
	
	public static final float PARAM_WeightOfProb = 0.9f;
	[INFO] Number of links found = [484], correctLinks = [450], precision = [0.9297520661157025], F1 = [0.9240246406570842]
	
	public static final float PARAM_WeightOfProb = 1.0f;
	[INFO] Number of links found = [458], correctLinks = [434], precision = [0.9475982532751092], F1 = [0.9136842105263158]
    [INFO] Number of links found = [189], correctLinks = [187], precision = [0.9894179894179894], F1 = [0.9714285714285714]
    [INFO] Number of links found = [181], correctLinks = [177], precision = [0.9779005524861878], F1 = [0.9414893617021277]
    [INFO] Number of links found = [94], correctLinks = [92], precision = [0.9787234042553191], F1 = [0.9484536082474226]
    [INFO] Number of links found = [95], correctLinks = [93], precision = [0.9789473684210527], F1 = [0.9538461538461539]

	public static final float PARAM_WeightOfProb = 0.5f;
    //*/

	public static final float PARAM_WeightOfProb = 0.8f;
	public static final float PARAM_Smooth_NumOfLocations = 0.000025f;
	/*
	public static final float PARAM_Smooth_Social = 0.1f;
	//*/
	public static final int PARAM_NumOfTimeUnits = 0;
	/*
	public static final float PARAM_MaxDist = 375.2149800447738f;
	//*/
	public static final float PARAM_MaxDist = 1;
	
	public float m_fWeightOfProb;
	public float m_fSmooth_NumOfLocations;
	/*
	public float m_fSmooth_Social;
	//*/
	public int m_nNumOfTimeUnits;
	public int m_nNumOfLocIds;
	
	public double m_dMaxDist;
	
	public Params() {
		
		this(PARAM_WeightOfProb,
				PARAM_Smooth_NumOfLocations,
				PARAM_NumOfTimeUnits,
				0);
	}
	
	public Params(float fWeightOfProb,
			float fSmooth_NumOfLocations,
			int nNumOfTimeUnits,
			int nNumOfLocIds) {
		
		m_fWeightOfProb = fWeightOfProb;
		
		m_fSmooth_NumOfLocations = fSmooth_NumOfLocations;
		/*
		m_fSmooth_Social = fSmooth_Social;
		//*/
		
		m_nNumOfTimeUnits = nNumOfTimeUnits;
		
		m_nNumOfLocIds = nNumOfLocIds;
		
		m_dMaxDist = PARAM_MaxDist;
	}
	
	public float getWeightOfProb() {
		
		return m_fWeightOfProb;
	}

	public float getWeightOfDist() {
		
		return 1 - m_fWeightOfProb;
	}
	
	public float getSmooth_NumOfLocations() {
		
		return m_fSmooth_NumOfLocations;
	}

	/*
	public float getSmooth_Social() {
		
		return m_fSmooth_Social;
	}
	//*/

	public int getNumOfTimeUnits() {
		
		return m_nNumOfTimeUnits;
	}
}
