package yt2443.ch21;


public class Main {

	public static final String FILE_NAME_G1 = "G1.txt";
	public static final String FILE_NAME_G2 = "G2.txt";
	public static final String FILE_NAME_L = "L.txt";
	public static final String FILE_NAME_T1 = "T1.txt";
	public static final String FILE_NAME_Output = "output.txt";
	
	public static final int MAX_NodeNum = 50001;
	public static final int MARK_THRESHOLD = 1;
	
	
	public static void main(String[] args) {
		boolean bNeedHelp = false;
		
		String strRunMode;
		boolean bTestFlag = false;
		boolean bRandomFlag_1 = false;
		boolean bRandomFlag_2 = false;
		if ( (args == null) || (args.length == 0) ) {	
			bTestFlag = false;
		}
		else if (args.length == 1) {
			strRunMode = args[0];
			if (strRunMode.equalsIgnoreCase("-test")) {
				bTestFlag = true;
			}
			else if (strRunMode.equalsIgnoreCase("-random1")) {
				bRandomFlag_1 = true;
			}
			else if (strRunMode.equalsIgnoreCase("-random2")) {
				bRandomFlag_2 = true;
			}
			else {
				bNeedHelp = true;
			}
		}
		else if (args.length == 2) {
			strRunMode = args[0];
			if (strRunMode.equalsIgnoreCase("-test")) {
				bTestFlag = true;
			}
			else if (strRunMode.equalsIgnoreCase("-random1")) {
				bRandomFlag_1 = true;
			}
			else if (strRunMode.equalsIgnoreCase("-random2")) {
				bRandomFlag_2 = true;
			}
			else {
				bNeedHelp = true;
			}
			
			strRunMode = args[1];
			if (strRunMode.equalsIgnoreCase("-test")) {
				bTestFlag = true;
			}
			else if (strRunMode.equalsIgnoreCase("-random1")) {
				bRandomFlag_1 = true;
			}
			else if (strRunMode.equalsIgnoreCase("-random2")) {
				bRandomFlag_2 = true;
			}
			else {
				bNeedHelp = true;
			}
		}
		else {
			bNeedHelp = true;
		}
		
		if (bNeedHelp) {
			Logger.showHelpMsg(Logger.HELP_MSG_Usage);
	        System.exit(-1);
		}
		
		boolean bDebugFlag = false;
		if (bTestFlag) {
			bDebugFlag = true;
		}
		
		run(bRandomFlag_1, bRandomFlag_2, bTestFlag, bDebugFlag);
	}
	
	private static void run(boolean bRandomFlag_1, boolean bRandomFlag_2,
			boolean bTestFlag, boolean bDebugFlag) {
		
		Logger.m_bDebugFlag = bDebugFlag;	// set the flag to show debug messages
		
		Graph graph1 = null;
		Graph graph2 = null;
		LinkSet linkSet = null, linkSet_Answer = null;
		Matcher matcher = null;
		long lTime = 0, lStartTime;
		/*
		int cLinks_Given;
		//*/
		try {
			//*
			graph1 = new Graph(MAX_NodeNum);
			lStartTime = System.currentTimeMillis();
			graph1.buildFromFile(FILE_NAME_G1);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Load file [" + FILE_NAME_G1 + "] completed, time = [" + lTime + "]");
			
			graph2 = new Graph(MAX_NodeNum);
			lStartTime = System.currentTimeMillis();
			graph2.buildFromFile(FILE_NAME_G2);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Load file [" + FILE_NAME_G2 + "] completed, time = [" + lTime + "]");
			
			linkSet = new LinkSet();
			lStartTime = System.currentTimeMillis();
			linkSet.buildFromFile(FILE_NAME_L);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Load file [" + FILE_NAME_L + "] completed, time = [" + lTime + "]");
			
			/*
			cLinks_Given = linkSet.getLinkCount();
			//*/
			
			matcher = new Matcher(graph1, graph2);
			lStartTime = System.currentTimeMillis();
			matcher.doMatch(linkSet, MARK_THRESHOLD, bRandomFlag_1, bRandomFlag_2);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Match completed, time = [" + lTime + "]");
			
			lStartTime = System.currentTimeMillis();
			linkSet.saveIntoFile(FILE_NAME_Output);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Save file [" + FILE_NAME_Output + "] completed, time = [" + lTime + "]");
			//*/
			
			if (bTestFlag) {
				/*
				linkSet = new LinkSet();
				linkSet.buildFromFile(FILE_NAME_Output);
				//*/
				
				linkSet_Answer = new LinkSet();
				linkSet_Answer.buildFromFile(FILE_NAME_T1);
				int cLinks_Answer = linkSet_Answer.getLinkCount();
				
				int cLinks = linkSet.getLinkCount();
				int cCorrectLinks = linkSet.compareWith(linkSet_Answer);
				double dPrecision = (double) cCorrectLinks / cLinks;
				double dRecall = (double) cCorrectLinks / cLinks_Answer;
				double dF1 = 2 * (dPrecision * dRecall) / (dPrecision + dRecall);
				Logger.showInfo("Number of links found = [" + cLinks +
						"], correctLinks = [" + cCorrectLinks + 
						"], precision = [" + (100 * dPrecision) + "%], F1 = [" + dF1 + "]");					
			}
		}
		
		catch (SnsException ex) {
        	ex.printStackTrace();
			Logger.showErrMsg(ex.getExDesp());
		}
		
		finally {
			if (linkSet_Answer != null) {
				linkSet_Answer.release();
				linkSet_Answer = null;
			}
			if (matcher != null) {
				matcher.release();
				matcher = null;
			}
			if (graph1 != null) {
				graph1.release();
				graph1 = null;
			}			
			if (graph2 != null) {
				graph2.release();
				graph2 = null;
			}
			if (linkSet != null) {
				linkSet.release();
				linkSet = null;
			}
		}
		
	}
	
}
