package yt2443.aProject;

import yt2443.aProject.entity.StopWordDict;


public class Main {

	public static final String DATA_FILE_DIR_StopWords = "./../../../data/";
	public static final String FILE_NAME_StopWords = "stopWords.txt";
	
	public static final int MIN_NumOfTweets_ForEachActiveUser = 200;

	
	public static void main(String[] args) {
		
		Logger.m_bDebugFlag = true;
		
		Test.test();
	}
	
	public static StopWordDict loadStopWordDict() throws SnsException {
		
		StopWordDict stopWordDict = StopWordDict.getInstance();
		
		stopWordDict.loadStopWords(DATA_FILE_DIR_StopWords, FILE_NAME_StopWords);
		
		return stopWordDict;
	}

}
