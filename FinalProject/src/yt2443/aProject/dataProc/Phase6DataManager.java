package yt2443.aProject.dataProc;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;

import yt2443.aProject.Logger;
import yt2443.aProject.SnsException;
import yt2443.aProject.entity.TestResult_ForTweet;
import yt2443.aProject.entity.UserIdWithScore;

public class Phase6DataManager extends GenericDataManager {

	
	private static Phase6DataManager m_theInstance = null;
	
	private String m_strDataDir;
	
	public static Phase6DataManager getInstance(String strDataDir) {
		
		if (m_theInstance == null) {
			m_theInstance = new Phase6DataManager(strDataDir);
		}
		
		return m_theInstance;
	}

	private Phase6DataManager(String strDataDir) {
		
		m_strDataDir = strDataDir;
	}

	public void release() {
		
		m_strDataDir = null;
	}

	public void write_testResult(List<TestResult_ForTweet> listTestResults,
			String strOutputFileName,
			float fCorrectScope, boolean bOnlySN)
			throws SnsException {
		
		if ( (listTestResults == null) || listTestResults.isEmpty() ) {
			Logger.showErrMsg("No test result !!!");
			return;
		}
		
		SnsException exception = null;
		
		OutputStream outputStream = null;
		BufferedWriter writer = null;
		int nLineNo = 1;
		
		try {
			outputStream = new FileOutputStream(m_strDataDir + strOutputFileName);
			
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			int cCorrectResults = 0;
			boolean bCorrectResultFound;
			
			Set<Long> setCorrectUserIds;
			List<UserIdWithScore> listUserIdWithScores;
			for (TestResult_ForTweet testResult : listTestResults) {
				
				setCorrectUserIds = testResult.getCorrectUserIds();
				if ( (setCorrectUserIds == null) || setCorrectUserIds.isEmpty() ) {
				}
				
				else {
					bCorrectResultFound = false;
					int nOrderNo;
					for (Long longCorrectUserId : setCorrectUserIds) {
						
						nOrderNo = testResult.getOrderNoOf(longCorrectUserId);
						if (nOrderNo > 0) {
							bCorrectResultFound = true;
						}
					}
					
					if (bCorrectResultFound) {
						cCorrectResults++;
					}
				}
			}
			
			int cTotalTestTweets = listTestResults.size();
			writer.write("Summary: test-mode = [" + ( bOnlySN ? "ONLY-SN" : "OpinionAnalysis + SN" ) 
					+ "], \r\n\tnum-of-test-tweets = [" + cTotalTestTweets
					+ "], \r\n\tnum-of-correct-results = [" + cCorrectResults
					+ "], \r\n\tcorrect-percentage = [" + ( (100.0f * cCorrectResults) / cTotalTestTweets )
					+ "], \r\n\tscope-param = [" + (100 * fCorrectScope)
					+ "%]\r\n\r\n");

			
			
			
			
			
			for (TestResult_ForTweet testResult : listTestResults) {
				
				writer.write("tweetId = [" + testResult.getTweetId_Target() + "]\r\n");
				
				setCorrectUserIds = testResult.getCorrectUserIds();
				if ( (setCorrectUserIds == null) || setCorrectUserIds.isEmpty() ) {
					writer.write("\tcorrectUserIds = [NULL]\r\n");
				}
				
				else {
					int nOrderNo;
					for (Long longCorrectUserId : setCorrectUserIds) {
						
						nOrderNo = testResult.getOrderNoOf(longCorrectUserId);
						if (nOrderNo > 0) {
							writer.write("\tcorrectUserId = [" + longCorrectUserId +
									"], orderNo = [" + nOrderNo + "]\r\n");
						}
					}
				}
				
				listUserIdWithScores = testResult.getUserIdWithScores();
				if ( (listUserIdWithScores == null) || listUserIdWithScores.isEmpty() ) {
					writer.write("\tuserIds = [NULL]\r\n");
				}
				else {
					
					for (UserIdWithScore uiws : listUserIdWithScores) {
						if (uiws.m_fScore > 0) {
							writer.write("\tuserId = [" + uiws.m_longUserId +
									"], score = [" + uiws.m_fScore + "]\r\n");
						}
					}
				}
				
				writer.write("\r\n");
				
				nLineNo++;
			}
		}
		
		catch (IOException ex) {
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_FailedWriteFile,
					strOutputFileName, "" + nLineNo);
		}
		
		finally {
			SnsException ex1 = closeWriter(writer);
			SnsException ex2 = closeOutputStream(outputStream);
			
		    if (exception == null) {
		    	if (ex1 != null) {
		    		exception = ex1;
		    	}
		    	else if (ex2 != null) {
		    		exception = ex2;
		    	}
		    }
		}
		
		if (exception != null) {
			throw exception;
		}
	}

}
