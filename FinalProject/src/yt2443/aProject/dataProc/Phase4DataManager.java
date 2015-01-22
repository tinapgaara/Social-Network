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
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import yt2443.aProject.Logger;
import yt2443.aProject.SnsException;
import yt2443.aProject.analyzer.TopicAnalyzer;
import yt2443.aProject.entity.FollowerNet;
import yt2443.aProject.entity.Topic;
import yt2443.aProject.entity.TweetInfo;
import yt2443.aProject.entity.TweetSet;
import yt2443.aProject.entity.UserTweetRelationSet;

public class Phase4DataManager extends GenericDataManager {

	private static final String JSON_NAME_numTweets = "numTweets";
	private static final String JSON_NAME_userId = "userId";
	private static final String JSON_NAME_tweetId = "tweetId";
	private static final String JSON_NAME_time = "time";
	private static final String JSON_NAME_text = "text";
	private static final String JSON_NAME_text_clear = "clearText";
	
	private static final String JSON_NAME_id = "id";
	private static final String JSON_NAME_followers = "followers";
	
	private static final String JSON_NAME_topics = "topics";
	private static final String STRING_DelimiterInSentmentFile = "\t";

	public static final int OPTION_WriteTweet_ForTopicAnalysis = 1;
	public static final int OPTION_WriteTweet_ForSentimentAnalysis = 2;
	public static final int OPTION_WriteTweet_ForPersistance = 3;
	
	
	private static Phase4DataManager m_theInstance = null;
	
	private String m_strDataDir;
	
	public static Phase4DataManager getInstance(String strDataDir) {
		
		if (m_theInstance == null) {
			m_theInstance = new Phase4DataManager(strDataDir);
		}
		
		return m_theInstance;
	}

	private Phase4DataManager(String strDataDir) {
		
		m_strDataDir = strDataDir;
	}

	public void release() {
		
		m_strDataDir = null;
	}

	public void write_tweets(
			TweetSet tweetSet,
			FollowerNet followerNet_Yes,
			UserTweetRelationSet utRelationSet,
			FollowerNet followerNet_DidNotRetweetAnySeedTweet,
			UserTweetRelationSet utRelationSet_ForUsers_DidNotRetweetAnySeedTweet,
			String strOutputFileName,
			int nOption)
			throws SnsException {
		
		if (tweetSet == null) {
			return;
		}
		
		SnsException exception = null;
		
		OutputStream outputStream = null;
		BufferedWriter writer = null;
		try {
			outputStream = new FileOutputStream(m_strDataDir + strOutputFileName);
			
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			__write_tweets(writer, tweetSet,
					followerNet_Yes, utRelationSet,
					nOption,
					false); // bOnlyFollowers
			
			__write_tweets(writer, tweetSet,
					followerNet_DidNotRetweetAnySeedTweet, utRelationSet_ForUsers_DidNotRetweetAnySeedTweet,
					nOption,
					true);	// bOnlyFollowers
		}
		
		catch (IOException ex) {
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_FailedWriteFile,
					strOutputFileName);
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

	private void __write_tweets(BufferedWriter writer, TweetSet tweetSet,
			FollowerNet followerNet, UserTweetRelationSet utRelationSet,
			int nOption,
			boolean bOnlyFollowers)
	        throws IOException {
		
		Map<Long, Set<Long>> mapFollowerIds = followerNet.getFollowerIds();
		Set<Long> setFollowerIds;
		for (Long longUserId : mapFollowerIds.keySet()) {
			
			if ( ! bOnlyFollowers ) {
				__write_tweets(writer, tweetSet, longUserId, utRelationSet, nOption);
			}
			
			setFollowerIds = mapFollowerIds.get(longUserId);
			if (setFollowerIds == null) {
				continue;
			}
			
			for (Long longFollowerId : setFollowerIds) {
				__write_tweets(writer, tweetSet, longFollowerId, utRelationSet, nOption);
			}
		}
	}
	
	private void __write_tweets(BufferedWriter writer, TweetSet tweetSet,
			Long longUserId, UserTweetRelationSet utRelationSet,
			int nOption)
			throws IOException {
		
		Set<Long> setTweetIds = utRelationSet.getTweetIdsByUserId(longUserId);
		if ( (setTweetIds == null) || setTweetIds.isEmpty() ) {
			return;
		}
		
		StringBuffer sbLine;
		TweetInfo tweetInfo;
		if (nOption == OPTION_WriteTweet_ForTopicAnalysis) {
			
			sbLine = new StringBuffer();
			
		    sbLine.append("{\"" + JSON_NAME_userId + "\":" + longUserId + ",");
		    sbLine.append("\"" + JSON_NAME_numTweets + "\":" + calcNumOfTweets(setTweetIds, tweetSet) + "}\r\n");
			writer.write(sbLine.toString());
			sbLine = null;
		}
		
		for (Long longTweetId : setTweetIds) {
			
			tweetInfo = tweetSet.getTweetInfo(longTweetId);
			if (tweetInfo == null) {
				continue;
			}
			
			if (nOption == OPTION_WriteTweet_ForTopicAnalysis) {
				
				if (tweetInfo.m_strText_Clear != null) {
				    writer.write(tweetInfo.m_strText_Clear + "\r\n");
				}
			}
			
			else if (nOption == OPTION_WriteTweet_ForSentimentAnalysis) {
				
				if (tweetInfo.m_strText != null) {
					
					sbLine = new StringBuffer();
					
				    sbLine.append("" + longTweetId + STRING_DelimiterInSentmentFile);
				    sbLine.append(tweetInfo.m_strText);
			        sbLine.append("\r\n");
				    writer.write(sbLine.toString());
				    sbLine = null;
				}
			}
			
			else {
				sbLine = new StringBuffer();
				
			    sbLine.append("{\"" + JSON_NAME_userId + "\":" + longUserId + ",");
			    sbLine.append("\"" + JSON_NAME_tweetId + "\":" + longTweetId + ",");
			
			    sbLine.append("\"" + JSON_NAME_time + "\":\"" + ( (tweetInfo.m_time == null) ? "" : tweetInfo.m_time.toString() ) + "\",");
			    sbLine.append("\"" + JSON_NAME_text + "\":\"" + ( (tweetInfo.m_strText == null) ? "" : tweetInfo.m_strText ) + "\",");
			    sbLine.append("\"" + JSON_NAME_text_clear + "\":\"" + ( (tweetInfo.m_strText_Clear == null) ? "" : tweetInfo.m_strText_Clear ) + "\"");
			    
			    sbLine.append("}\r\n");
				writer.write(sbLine.toString());
				sbLine = null;
			}
		}
	}

	private int calcNumOfTweets(Set<Long> setTweetIds, TweetSet tweetSet) {
		
		if (setTweetIds == null) {
			return 0;
		}
		
		int cTweets = 0;
		
		TweetInfo tweetInfo;
		for (Long longTweetId : setTweetIds) {
			
			tweetInfo = tweetSet.getTweetInfo(longTweetId);
			if ( (tweetInfo != null) && (tweetInfo.m_strText_Clear != null) ) {
				cTweets++;
			}
		}
		
		return cTweets;
	}

	public void write_followerNet(FollowerNet followerNet, String strOutputFileName)
	    throws SnsException {
		
		if (followerNet == null) {
			return;
		}
		
		Map<Long, Set<Long>> mapFollowers = followerNet.getFollowerIds();
		if ( (mapFollowers == null) || mapFollowers.isEmpty() ) {
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
			
			Set<Long> setFollowerIds;
			boolean bInitFlag;
			for (Long longUserId : mapFollowers.keySet()) {
				
				setFollowerIds = mapFollowers.get(longUserId);
				if ( (setFollowerIds == null) || setFollowerIds.isEmpty() ) {
					continue;
				}
				
				sbLine = new StringBuffer();
			    sbLine.append("{\"" + JSON_NAME_id + "\":" + longUserId.longValue() + ",");
			
			    sbLine.append("\"" + JSON_NAME_followers + "\":[");
			    
			    bInitFlag = true;
				for (Long longFollower : setFollowerIds) {
					
					if (bInitFlag) {
						sbLine.append(longFollower.toString());
						bInitFlag = false;
					}
					else {
						sbLine.append("," + longFollower);
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

	public void load_followerNet(FollowerNet followerNet,
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
			
			Long longFollowedUserId, longFollowerId;
			
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
    
		    	longFollowedUserId = (Long) jsonObject.get(JSON_NAME_id);
		    	
		        jsonArray = (JSONArray) jsonObject.get(JSON_NAME_followers);
		        if ( (jsonArray == null) || jsonArray.isEmpty() ) {
					strLine = reader.readLine();
					continue;
				}
	        
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	
		        	longFollowerId = (Long) jsonArray.get(i);
		        	followerNet.addFollower(longFollowerId, longFollowedUserId);
		        }
		        
	        	// release resources
	        	jsonArray.clear();
	        	jsonArray = null;
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

	public void load_tweetSet(
			TweetSet tweetSet,
			UserTweetRelationSet utRelationSet,
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
    
		    	Long longUserId = (Long) jsonObject.get(JSON_NAME_userId);
		    	Long longTweetId = (Long) jsonObject.get(JSON_NAME_tweetId);
		    	
		    	utRelationSet.addRelation(longUserId, longTweetId);
		    	
		    	
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

	public void load_topics(
			TopicAnalyzer topicAnalyzer,
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
			Topic topic;
			int nTopicIndex;
			
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
    
		    	Long longUserId = (Long) jsonObject.get(JSON_NAME_userId);
		    	
		    	int cTopics = ( (Long) jsonObject.get(JSON_NAME_topics) ).intValue();
		    	for (nTopicIndex = 0; nTopicIndex < cTopics; nTopicIndex++) {
		    		
					nLineNo++;
		        	strLine = null;
					strLine = reader.readLine();
		    		topic = Topic.parse(strLine);
		    		if (topic == null) {
		    			throw new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		    		}
		    		
		    		topicAnalyzer.addUserTopic(longUserId, topic);
		    	}
		    	
	        	// release resources
	        	jsonObject = null;
	        	
	        	strLine = null;
	        	strLine = reader.readLine();
			}
		}
		
		catch (NumberFormatException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		catch (SnsException ex) {
			
        	ex.printStackTrace();
			exception = ex;
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

	public void load_sentiments(TweetSet tweetSet,
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
			
			StringTokenizer tokenizer;
			String strTweetId = null, strPositiveSentiment = null, strNegativeSentiment = null;
			Long longTweetId;
			TweetInfo tweetInfo;
			boolean bFirstFlag;
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
				tokenizer = new StringTokenizer(strLine, STRING_DelimiterInSentmentFile);
				bFirstFlag = true;
				strPositiveSentiment = null;
				strNegativeSentiment = null;
				while (tokenizer.hasMoreTokens()) {
					
					if (bFirstFlag) {
						strTweetId = tokenizer.nextToken();
						bFirstFlag = false;
					}
					else {
						strPositiveSentiment = strNegativeSentiment;
						strNegativeSentiment = tokenizer.nextToken();
					}
				}

				try {
					longTweetId = new Long(Long.parseLong(strTweetId));
					
					tweetInfo = tweetSet.getTweetInfo(longTweetId);
					if (tweetInfo != null) {
						tweetInfo.m_nPositiveSentiment = Integer.parseInt(strPositiveSentiment);
						tweetInfo.m_nNegativeSentiment = - Integer.parseInt(strNegativeSentiment);
					}
				}
				catch (NumberFormatException ex) {
					Logger.showErrMsg("NumberFormatException in Phase4DataManager.load_sentments, line no = [" + nLineNo + "], text=[" + strLine + "]");
				}
				catch (NullPointerException ex) {
					Logger.showErrMsg("NullPointerException in Phase4DataManager.load_sentments, line no = [" + nLineNo + "], text=[" + strLine + "]");
				}
				
	        	strLine = null;
	        	strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		catch (SnsException ex) {
			
        	ex.printStackTrace();
			exception = ex;
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
