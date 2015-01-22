package yt2443.aProject.dataProc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import yt2443.aProject.SnsException;
import yt2443.aProject.entity.TweetInfo;
import yt2443.aProject.entity.TweetRelationSet;
import yt2443.aProject.entity.TweetSet;
import yt2443.aProject.entity.TweetUserRelationSet;
import yt2443.aProject.entity.UserTweetRelationSet;

public class Phase5DataManager extends GenericDataManager {

	private static final String JSON_NAME_id = "id";
	private static final String JSON_NAME_time = "time";
	private static final String JSON_NAME_text = "text";
	private static final String JSON_NAME_text_clear = "clearText";
	private static final String JSON_NAME_tweets = "tweets";
	private static final String JSON_NAME_retweetIds = "retweetIds";
	private static final String STRING_DelimiterInSentmentFile = "\t";
	
	
	private static Phase5DataManager m_theInstance = null;
	
	private String m_strDataDir;
	
	public static Phase5DataManager getInstance(String strDataDir) {
		
		if (m_theInstance == null) {
			m_theInstance = new Phase5DataManager(strDataDir);
		}
		
		return m_theInstance;
	}

	private Phase5DataManager(String strDataDir) {
		
		m_strDataDir = strDataDir;
	}

	public void release() {
		
		m_strDataDir = null;
	}

	public void write_tweets_ForTest(TweetSet tweetSet,
			String strOutputFileName,
			boolean bForSentimentAnalysis)
		    throws SnsException {
		
		if (tweetSet == null) {
			return;
		}
		
		Map<Long, TweetInfo> mapTweetInfos = tweetSet.getTweetInfos();
		if ( (mapTweetInfos == null) || mapTweetInfos.isEmpty() ) {
			return;
		}
		
		SnsException exception = null;
		
		OutputStream outputStream = null;
		BufferedWriter writer = null;
		StringBuffer sbLine;
		int nLineNo = 1;
		
		try {
			outputStream = new FileOutputStream(m_strDataDir + strOutputFileName);
			
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			TweetInfo tweetInfo;
			for (Long longTweetId : mapTweetInfos.keySet()) {
				
				tweetInfo = mapTweetInfos.get(longTweetId);
				if (tweetInfo.m_strText_Clear == null) {
					continue;
				}
				
				sbLine = new StringBuffer();
				
				if (bForSentimentAnalysis) {
					if (tweetInfo.m_strText != null) {
						
					    sbLine.append("" + longTweetId + STRING_DelimiterInSentmentFile);
					    sbLine.append(tweetInfo.m_strText);
				        sbLine.append("\r\n");
					}
				}
				
				else {
				    sbLine.append("{\"" + JSON_NAME_id + "\":" + longTweetId.longValue() + ",");
				
				    sbLine.append("\"" + JSON_NAME_time + "\":\"" + ( (tweetInfo.m_time == null) ? "" : tweetInfo.m_time.toString() ) + "\",");
				    sbLine.append("\"" + JSON_NAME_text + "\":\"" + tweetInfo.m_strText + "\",");
				    sbLine.append("\"" + JSON_NAME_text_clear + "\":\"" + tweetInfo.m_strText_Clear + "\"");
				    
				    sbLine.append("}\r\n");
				}
				
				writer.write(sbLine.toString());
				sbLine = null;
				
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

	public void write_userTweetRelationSet_ForTest(UserTweetRelationSet utRelationSet,
			String strOutputFileName)
			throws SnsException {
		
		if (utRelationSet == null) {
			return;
		}
		
		Map<Long, Set<Long>> mapUser2Tweets = utRelationSet.getUser2Tweets();
		if ( (mapUser2Tweets == null) || mapUser2Tweets.isEmpty() ) {
			return;
		}
		
		SnsException exception = null;
		
		OutputStream outputStream = null;
		BufferedWriter writer = null;
		StringBuffer sbLine;
		int nLineNo = 1;
		
		try {
			outputStream = new FileOutputStream(m_strDataDir + strOutputFileName);
			
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			Set<Long> setTweetIds;
			boolean bInitFlag;
			for (Long longUserId : mapUser2Tweets.keySet()) {
				
				setTweetIds = mapUser2Tweets.get(longUserId);
				if ( (setTweetIds == null) || setTweetIds.isEmpty() ) {
					continue;
				}
				
				sbLine = new StringBuffer();
			    sbLine.append("{\"" + JSON_NAME_id + "\":" + longUserId.longValue() + ",");
			
			    sbLine.append("\"" + JSON_NAME_tweets + "\":[");
			    
			    bInitFlag = true;
				for (Long longTweetId : setTweetIds) {
					
					if (bInitFlag) {
						sbLine.append(longTweetId.toString());
						bInitFlag = false;
					}
					else {
						sbLine.append("," + longTweetId);
					}
				}
				
			    sbLine.append("]}\r\n");
			    
				writer.write(sbLine.toString());
				sbLine = null;
				
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

	public void write_tweetRelationSet_ForTest(TweetRelationSet ttRelationSet,
			String strOutputFileName)
			throws SnsException {
		
		if (ttRelationSet == null) {
			return;
		}
		
		Map<Long, Set<Long>> mapRetweetIds = ttRelationSet.getRetweetIds();
		if ( (mapRetweetIds == null) || mapRetweetIds.isEmpty() ) {
			return;
		}
		
		SnsException exception = null;
		
		OutputStream outputStream = null;
		BufferedWriter writer = null;
		StringBuffer sbLine;
		int nLineNo = 1;
		
		try {
			outputStream = new FileOutputStream(m_strDataDir + strOutputFileName);
			
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			Set<Long> setRetweetIds;
			boolean bFirstFlag;
			for (Long longRetweetedId : mapRetweetIds.keySet()) {
				
				setRetweetIds = mapRetweetIds.get(longRetweetedId);
				if ( (setRetweetIds == null) || setRetweetIds.isEmpty() ) {
					continue;
				}
				
				sbLine = new StringBuffer();
			    sbLine.append("{\"" + JSON_NAME_id + "\":" + longRetweetedId.longValue() + ",");
			
			    sbLine.append("\"" + JSON_NAME_retweetIds + "\":[");
			    
			    bFirstFlag = true;
			    for (Long longRetweetId : setRetweetIds) {
			    	
			    	if (bFirstFlag) {
				        sbLine.append(longRetweetId.toString());
				        bFirstFlag = false;
			    	}
			    	else {
				        sbLine.append("," + longRetweetId.longValue());
			    	}
			    }
			    sbLine.append("]}\r\n");
			    
				writer.write(sbLine.toString());
				sbLine = null;
				
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

	public void load_tweetSet_ForTest(
			TweetSet tweetSet,
			String strFileName)
	        throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		int nLineNo = 0;
		try {
			inputStream = getClass().getResourceAsStream(m_strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			TweetInfo tweetInfo;
			
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
				JSONObject jsonObject = (JSONObject) JSONValue.parse(strLine);
		        if (jsonObject == null) {
					strLine = reader.readLine();
					continue;
				}
    
		    	Long longTweetId = (Long) jsonObject.get(JSON_NAME_id);
		        String strTime = (String) jsonObject.get(JSON_NAME_time);
		        String strText = (String) jsonObject.get(JSON_NAME_text);
		        String strText_Clear = (String) jsonObject.get(JSON_NAME_text_clear);
	        	tweetInfo = new TweetInfo(longTweetId, strTime, strText, strText_Clear, 0);
	        	
			    tweetSet.addTweet(tweetInfo);
		        
			    
	        	// release resources
	        	jsonObject = null;
	        	
	        	strLine = null;
	        	strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		finally {
			
			SnsException ex1 = closeReader(reader);
			SnsException ex2 = closeInputStream(inputStream);
			
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

	public void load_userTweetRelationSet_ForTest(
			UserTweetRelationSet utRelationSet,
			TweetUserRelationSet tuRelationSet,
			Set<Long> setUserIds_AsConstraint,
			String strFileName)
			throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		int nLineNo = 0;
		JSONArray jsonArray = null;
		try {
			inputStream = getClass().getResourceAsStream(m_strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
	    	Long longTweetId;
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
				JSONObject jsonObject = (JSONObject) JSONValue.parse(strLine);
		        if (jsonObject == null) {
					strLine = reader.readLine();
					continue;
				}
    
		    	Long longUserId = (Long) jsonObject.get(JSON_NAME_id);
		    	if ( (setUserIds_AsConstraint != null) &&
		    			( ! setUserIds_AsConstraint.contains(longUserId) ) ) {
					strLine = reader.readLine();
					continue;
		    	}
		    	
		        jsonArray = (JSONArray) jsonObject.get(JSON_NAME_tweets);
		        if ( (jsonArray == null) || jsonArray.isEmpty() ) {
					strLine = reader.readLine();
					continue;
				}
		        
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	
		        	longTweetId = (Long) jsonArray.get(i);
		        	
	        		if (utRelationSet != null) {
	        	        utRelationSet.addRelation(longUserId, longTweetId);
	        		}
	        		
	        		if (tuRelationSet != null) {
	        	        tuRelationSet.addRelation(longTweetId, longUserId);
	        		}
		        }
		        
	        	// release resources
	        	jsonObject = null;
	        	jsonArray.clear();
	        	jsonArray = null;
	        	
	        	strLine = null;
	        	strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		finally {
			
			SnsException ex1 = closeReader(reader);
			SnsException ex2 = closeInputStream(inputStream);
			
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

	public void load_tweetRelationSet(TweetRelationSet ttRelationSet,
			TweetSet tweetSet_AsConstraint,
			String strFileName)
			throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		int nLineNo = 0;
		JSONArray jsonArray = null;
		try {
			inputStream = getClass().getResourceAsStream(m_strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			Long longRetweetedId, longRetweetId;
			
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
				JSONObject jsonObject = (JSONObject) JSONValue.parse(strLine);
		        if (jsonObject == null) {
					strLine = reader.readLine();
					continue;
				}
    
		    	longRetweetedId = (Long) jsonObject.get(JSON_NAME_id);
	        	if ( (tweetSet_AsConstraint == null) || tweetSet_AsConstraint.contains(longRetweetedId) ) {
		    	
			        jsonArray = (JSONArray) jsonObject.get(JSON_NAME_retweetIds);
			        if ( (jsonArray == null) || jsonArray.isEmpty() ) {
						strLine = reader.readLine();
						continue;
					}
		        
			        for (int i = 0; i < jsonArray.size(); i++) {
			        	
			        	longRetweetId = (Long) jsonArray.get(i);
			        	/*
			        	if ( (tweetSet == null) || tweetSet.contains(longRetweetId) ) {
			        		ttRelationSet.addRelation(longRetweetId, longRetweetedId);
			        	}
			        	//*/
		        		ttRelationSet.addRelation(longRetweetId, longRetweetedId);
			        }
			        
		        	jsonArray.clear();
		        	jsonArray = null;
	        	}
	        	
	        	// release resources
	        	jsonObject = null;
	        	
	        	strLine = null;
	        	strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		finally {
			
			SnsException ex1 = closeReader(reader);
			SnsException ex2 = closeInputStream(inputStream);
			
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
