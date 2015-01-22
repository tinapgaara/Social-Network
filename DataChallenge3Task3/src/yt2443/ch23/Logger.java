package yt2443.ch23;

public class Logger {

	public static final String HELP_MSG_Usage = "Usage: java yt2443.ch23.Main --OR-- java yt2443.ch23.Main -test";
	public static final String RESULT_NoPathFound = "No path found.";
	
	public static boolean m_bDebugFlag;
	
	
	public static void showResult(String strMsg, String strParam) {
		
		showResult(strMsg + strParam);
	}

	public static void showResult(String strMsg, String strParam1, String strParam2) {
		
		System.out.println("[RESULT] " + strMsg + strParam1 + ", " + strParam2);
	}

	public static void showResult(String strMsg) {
		
		System.out.println("[RESULT] " + strMsg);
	}

	public static void showErrMsg(String strMsg) {
		
		System.out.println("[ERROR] " + strMsg);
	}

	public static void showErrMsg(String strMsg, String strParam) {
		
		showErrMsg(strMsg + strParam);
	}

	public static void showDbgMsg(String strMsg) {
		
		if (m_bDebugFlag) {
			System.out.println("[DBG] " + strMsg);
		}
	}

	public static void showDbgMsg(String strMsg, String strParam) {
		
		showDbgMsg(strMsg + strParam);
	}

	public static void showHelpMsg(String strMsg) {
		
		System.out.println("[HELP] " + strMsg);
	}

	public static void showInfo(String strMsg) {
		
		System.out.println("[INFO] " + strMsg);
	}

}
