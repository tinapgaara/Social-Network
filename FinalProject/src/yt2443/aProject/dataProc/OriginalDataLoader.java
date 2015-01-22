package yt2443.aProject.dataProc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import yt2443.aProject.SnsException;
import yt2443.aProject.entity.FollowerNet;
import yt2443.aProject.entity.TweetRelationSet;
import yt2443.aProject.entity.TweetSet;
import yt2443.aProject.entity.UserSet;
import yt2443.aProject.entity.UserTweetRelationSet;

public class OriginalDataLoader {

	// _tweet
	private static final String JSON_NAME_created_at = "created_at";
	private static final String JSON_NAME_id = "id";
	private static final String JSON_NAME_text = "text";
	
	private static final String JSON_NAME_user = "user";
	/*
	private static final String JSON_NAME_name = "name";
	private static final String JSON_NAME_followers_count = "followers_count";
	private static final String JSON_NAME_friends_count = "friends_count";
	private static final String JSON_NAME_listed_count = "listed_count";
    //*/
	
	private static final String JSON_NAME_retweeted_status = "retweeted_status";
	
	// _followers
	private static final String JSON_NAME_ids = "ids";
	private static final String JSON_NAME_status = "status";
	
	private static OriginalDataLoader m_theInstance = null;
	
	private String m_strDataDir;
	
	public static OriginalDataLoader getInstance(String strDataDir) {
		
		if (m_theInstance == null) {
			m_theInstance = new OriginalDataLoader(strDataDir);
		}
		
		return m_theInstance;
	}

	private OriginalDataLoader(String strDataDir) {
		
		m_strDataDir = strDataDir;
	}

	public Long load_tweet(UserSet userSet, TweetSet tweetSet,
			UserTweetRelationSet utRelationSet, TweetRelationSet ttRelationSet,
			String strFileName) throws SnsException {
		
		Long longCurUserId = null;
		
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
    
		        longCurUserId = collectTweetInfo(jsonObject,
		    			userSet, tweetSet, 
		    			utRelationSet, ttRelationSet);
		        
	        	// release resources
	        	jsonObject = null;
	        	
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
		
		return longCurUserId;
	}

	private Long collectTweetInfo(JSONObject jsonObject,
			UserSet userSet, TweetSet tweetSet,
			UserTweetRelationSet utRelationSet, TweetRelationSet ttRelationSet)
		    throws SnsException {
		
        String strCreatedAt = (String) jsonObject.get(JSON_NAME_created_at);
    	Long longTweetId = (Long) jsonObject.get(JSON_NAME_id);
        String strText = (String) jsonObject.get(JSON_NAME_text);
        /* defer adding the tweet until "retweeted_status" parsed
    	tweetSet.addTweet(longTweetId, strCreatedAt, strText);
    	//*/
        
    	JSONObject jsonObject_user = (JSONObject) jsonObject.get(JSON_NAME_user);
        
        Long longUserId = (Long) jsonObject_user.get(JSON_NAME_id);
        /*
        String strUserName = (String) jsonObject_user.get(JSON_NAME_name);
    	Long longFollowersCount = (Long) jsonObject_user.get(JSON_NAME_followers_count);
    	Long longFriendsCount = (Long) jsonObject_user.get(JSON_NAME_friends_count);
    	Long longListedCount = (Long) jsonObject_user.get(JSON_NAME_listed_count);
    	userSet.addUser(longUserId, strUserName,
    			longFollowersCount.intValue(), longFriendsCount.intValue(), longListedCount.intValue());
    	//*/
        userSet.addUserIdIfNew(longUserId);
    	
    	utRelationSet.addRelation(longUserId, longTweetId);
    	
    	///////////////////////////////////////////////////////////////
    	// retweeted_status
    	
    	jsonObject = (JSONObject) jsonObject.get(JSON_NAME_retweeted_status);
    	if (jsonObject == null) {
        	tweetSet.addTweet(longTweetId, strCreatedAt, strText);
    	}
    	else {
    		strText = null;
        	tweetSet.addTweet(longTweetId, strCreatedAt, null);
        	
	        strCreatedAt = (String) jsonObject.get(JSON_NAME_created_at);
        	Long longTweetId_retweeted = (Long) jsonObject.get(JSON_NAME_id);
	        strText = (String) jsonObject.get(JSON_NAME_text);
        	tweetSet.addTweet(longTweetId_retweeted, strCreatedAt, strText);
	        
        	tweetSet.setRetweetRelation(longTweetId, longTweetId_retweeted);
        	
        	
	        jsonObject = (JSONObject) jsonObject.get(JSON_NAME_user);
	        
	        Long longUserId_retweeted = (Long) jsonObject.get(JSON_NAME_id);
	        /*
	        String strUserName_retweeted = (String) jsonObject.get(JSON_NAME_name);
        	longFollowersCount = (Long) jsonObject.get(JSON_NAME_followers_count);
        	longFriendsCount = (Long) jsonObject.get(JSON_NAME_friends_count);
        	longListedCount = (Long) jsonObject.get(JSON_NAME_listed_count);
        	userSet.addUser(longUserId_retweeted, strUserName_retweeted,
        			longFollowersCount.intValue(), longFriendsCount.intValue(), longListedCount.intValue());
        	//*/
	        userSet.addUserIdIfNew(longUserId_retweeted);
	        
        	utRelationSet.addRelation(longUserId_retweeted, longTweetId_retweeted);
        	
        	ttRelationSet.addRelation(longTweetId, longTweetId_retweeted);
        	
        	longTweetId_retweeted = null;
        	/*
        	strUserName_retweeted = null;
        	//*/
        	longUserId_retweeted = null;
    	}
    	
    	///////////////////////////////////////////////////////////////
    	
    	// release resources
    	strCreatedAt = null;
    	longTweetId = null;
    	strText = null;
    	/*
    	strUserName = null;
    	longFollowersCount = null;
    	longFriendsCount = null;
    	longListedCount = null;
    	//*/
    	
    	return longUserId;
	}

	public void load_followers(Long longUserId, UserSet userSet, FollowerNet followerNet,
			String strFileName) throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		JSONArray jsonArray = null;
		int nLineNo = 0;
		try {
			inputStream = getClass().getResourceAsStream(m_strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			Long longFollowerId;
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
    
		        jsonArray = (JSONArray) jsonObject.get(JSON_NAME_ids);
		        if ( (jsonArray == null) || jsonArray.isEmpty() ) {
					strLine = reader.readLine();
					continue;
				}
		        
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	
		        	longFollowerId = (Long) jsonArray.get(i);
		        	
		        	userSet.addUserIdIfNew(longFollowerId);
		        	followerNet.addFollower(longFollowerId, longUserId);
		        }
		        
		        strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		finally {
			
			if (jsonArray != null) {
				jsonArray.clear();
				jsonArray = null;
			}
			
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

	public void load_usersTweets(UserSet userSet, TweetSet tweetSet,
			UserTweetRelationSet utRelationSet, TweetRelationSet ttRelationSet,
			String strFileName) throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		JSONArray jsonArray = null;
		int nLineNo = 0;
		try {
			inputStream = getClass().getResourceAsStream(m_strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			JSONObject jsonObject;
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
		    	jsonArray = (JSONArray) JSONValue.parse(strLine);
		        if ( (jsonArray == null) || jsonArray.isEmpty() ) {
					strLine = reader.readLine();
					continue;
				}
    
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	
		        	jsonObject = (JSONObject) jsonArray.get(i);
		        	
			        collectTweetInfo(jsonObject,
			    			userSet, tweetSet,
			    			utRelationSet, ttRelationSet);
		        }
		        
		        strLine = null;
		        strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		finally {
			
			if (jsonArray != null) {
				jsonArray.clear();
				jsonArray = null;
			}
			
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

	public void load_retweeted_by(UserSet userSet, TweetSet tweetSet,
			UserTweetRelationSet utRelationSet, TweetRelationSet ttRelationSet,
			String strFileName) throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		JSONArray jsonArray = null;
		int nLineNo = 0;
		try {
			inputStream = getClass().getResourceAsStream(m_strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			JSONObject jsonObject;
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
		    	jsonArray = (JSONArray) JSONValue.parse(strLine);
		        if ( (jsonArray == null) || jsonArray.isEmpty() ) {
					strLine = reader.readLine();
					continue;
				}
    
		        for (int i = 0; i < jsonArray.size(); i++) {
		        	
		        	jsonObject = (JSONObject) jsonArray.get(i);
		        	
		            Long longUserId = (Long) jsonObject.get(JSON_NAME_id);
		            /*
		            String strUserName = (String) jsonObject.get(JSON_NAME_name);
		        	Long longFollowersCount = (Long) jsonObject.get(JSON_NAME_followers_count);
		        	Long longFriendsCount = (Long) jsonObject.get(JSON_NAME_friends_count);
		        	Long longListedCount = (Long) jsonObject.get(JSON_NAME_listed_count);
		        	userSet.addUser(longUserId, strUserName,
		        			longFollowersCount.intValue(), longFriendsCount.intValue(), longListedCount.intValue());
		        	//*/
		            userSet.addUserIdIfNew(longUserId);
		        	
		        	jsonObject = (JSONObject) jsonObject.get(JSON_NAME_status);
		            if (jsonObject == null) {
		            	continue;
		            }
		            
		            String strCreatedAt = (String) jsonObject.get(JSON_NAME_created_at);
		        	Long longTweetId = (Long) jsonObject.get(JSON_NAME_id);
		            String strText = (String) jsonObject.get(JSON_NAME_text);
		            /* defer adding the tweet until "retweeted_status" parsed
		        	tweetSet.addTweet(longTweetId, strCreatedAt, strText);
		        	//*/
		            
		        	utRelationSet.addRelation(longUserId, longTweetId);
		        	
		        	///////////////////////////////////////////////////////////////
		        	// retweeted_status
		        	
		        	jsonObject = (JSONObject) jsonObject.get(JSON_NAME_retweeted_status);
		        	if (jsonObject == null) {
		            	tweetSet.addTweet(longTweetId, strCreatedAt, strText);
		        	}
		        	else {
		        		strText = null;
		            	tweetSet.addTweet(longTweetId, strCreatedAt, null);
		            	
		    	        strCreatedAt = (String) jsonObject.get(JSON_NAME_created_at);
		            	Long longTweetId_retweeted = (Long) jsonObject.get(JSON_NAME_id);
		    	        strText = (String) jsonObject.get(JSON_NAME_text);
		            	tweetSet.addTweet(longTweetId_retweeted, strCreatedAt, strText);
		    	        
		            	tweetSet.setRetweetRelation(longTweetId, longTweetId_retweeted);
		            			            	
		            	ttRelationSet.addRelation(longTweetId, longTweetId_retweeted);
		            	
		            	longTweetId_retweeted = null;
		        	}
		        	
		        	///////////////////////////////////////////////////////////////
		        	
		        	// release resources
		        	strCreatedAt = null;
		        	longTweetId = null;
		        	strText = null;
		        	/*
		        	strUserName = null;
		        	longFollowersCount = null;
		        	longFriendsCount = null;
		        	longListedCount = null;
		        	//*/
		        }
		        
		        strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
			
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName + " line no " + nLineNo);
		}
		
		finally {
			
			if (jsonArray != null) {
				jsonArray.clear();
				jsonArray = null;
			}
			
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

    private SnsException closeInputStream(InputStream inStream) {
		
		if (inStream == null) {
			return null;
		}
		
		SnsException exception = null;
	    try {
	    	inStream.close();
		}
	    catch (IOException ex) {
        	ex.printStackTrace();
	    	exception = new SnsException(ex.getMessage());
		}
		return exception;
	}

	private SnsException closeReader(BufferedReader reader) {
		
		if (reader == null) {
			return null;
		}
		
		SnsException exception = null;
	    try {
	    	reader.close();
		}
	    catch (IOException ex) {
        	ex.printStackTrace();
	    	exception = new SnsException(ex.getMessage());
		}
		return exception;
	}

	public void release() {
		
		m_strDataDir = null;
	}

	/*
	public void load_followersUsersTweets(UserSet userSet, TweetSet tweetSet,
			UserTweetRelationSet utRelationSet, TweetRelationSet ttRelationSet,
			String string)
		    throws SnsException {
		// TODO Auto-generated method stub
		
	}

	public void load_retweetedUsersTweets(UserSet userSet, TweetSet tweetSet,
			UserTweetRelationSet utRelationSet, TweetRelationSet ttRelationSet,
			String string) 
			throws SnsException {
		// TODO Auto-generated method stub
		
	}
	//*/

}
