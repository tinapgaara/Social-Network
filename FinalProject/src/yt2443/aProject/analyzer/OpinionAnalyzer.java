package yt2443.aProject.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import yt2443.aProject.entity.Topic;
import yt2443.aProject.entity.TweetInfo;
import yt2443.aProject.entity.TweetSet;
import yt2443.aProject.entity.UserTweetRelationSet;
import yt2443.aProject.entity.Opinion;

public class OpinionAnalyzer {

	private static OpinionAnalyzer m_theInstance = null;
	
	private TopicAnalyzer m_topicAnalyzer;

	private Map<Long, Map<Long, Opinion>> m_userOpinions_ForTweets;
	private Map<Long, Map<Topic, Opinion>> m_userOpinions_ForTopics;
	
	public static OpinionAnalyzer getInstance() {
		
		if (m_theInstance == null) {
			m_theInstance = new OpinionAnalyzer();
		}
		
		return m_theInstance;
	}

	private OpinionAnalyzer() {
		
		m_topicAnalyzer = TopicAnalyzer.getInstance();
		
		m_userOpinions_ForTopics = null;
		m_userOpinions_ForTweets = null;
	}
	
	public void addTweetOpinion(Long longUserId, Long longTweetId,
			Opinion viewpoint) {
		
		if (m_userOpinions_ForTweets == null) {
			m_userOpinions_ForTweets = new HashMap<Long, Map<Long, Opinion>>();
		}
		
		Map<Long, Opinion> map = m_userOpinions_ForTweets.get(longUserId);
		if (map == null) {
			map = new HashMap<Long, Opinion>();
			m_userOpinions_ForTweets.put(longUserId, map);
		}
		
		Opinion vp = map.get(longTweetId);
		if (vp != null) {
			vp.release();
		}
		
		map.put(longTweetId, viewpoint);
	}

	public float calcUserInterestOfTweet(Long longUserId, TweetInfo tweetInfo,
			int nMaxNumOfTopics, float fMinRelevance,
			TweetSet tweetSet,
			UserTweetRelationSet utRelations) {
		
		List<Topic> listAllTopics = m_topicAnalyzer.getTopicsOfUser(longUserId);
		if (listAllTopics == null) {
			return 0;
		}
		
		Map<Topic, Float> mapRelevantTopics = m_topicAnalyzer.getRelevantTopicsOfTweet(
				tweetInfo, listAllTopics,
				nMaxNumOfTopics, fMinRelevance);
		
		if (mapRelevantTopics == null) {
			return 0;
		}
		
		float fRelevance, fRelevance_Sum = 0;
		float fInterest_Sum = 0;
		for (Topic topic : mapRelevantTopics.keySet()) {
			
			fRelevance = mapRelevantTopics.get(topic).floatValue();
			fRelevance_Sum += fRelevance;
			
			fInterest_Sum += (fRelevance * 
					calcUserInterestOfTopic(longUserId, topic, tweetSet, utRelations));
		}
		
		if (fRelevance_Sum == 0) {
			return 0;
		}
		
		return fInterest_Sum / fRelevance_Sum;
	}
	
	public float calcOpinionSimilarity(Long longUserId, TweetInfo tweetInfo,
			int nMaxNumOfTopics, float fMinRelevance) {
		
		Opinion opinionOfUser_ForTweet = calcUserViewpoint_ForTweet(longUserId, tweetInfo,
				nMaxNumOfTopics, fMinRelevance);
		if (opinionOfUser_ForTweet == null) {
			return 0;
		}
		
		Opinion opinionOfTweet = new Opinion(tweetInfo);

		float fSimilarity = opinionOfUser_ForTweet.calcSimilarity(opinionOfTweet);
		
		opinionOfUser_ForTweet.release();
		opinionOfUser_ForTweet = null;
		
		opinionOfTweet.release();
		opinionOfTweet = null;
		
		return fSimilarity;
	}
	
	public float calcUserSimilarity_ForTweet(Long longUserId_1, Long longUserId_2, TweetInfo tweetInfo,
			int nMaxNumOfTopics, float fMinRelevance) {
		
		Opinion viewpoint_1 = calcUserViewpoint_ForTweet(longUserId_1, tweetInfo,
				nMaxNumOfTopics, fMinRelevance);
		if (viewpoint_1 == null) {
			return 0;
		}
		
		Opinion viewpoint_2 = calcUserViewpoint_ForTweet(longUserId_2, tweetInfo,
				nMaxNumOfTopics, fMinRelevance);
		if (viewpoint_2 == null) {
			return 0;
		}
		
		return viewpoint_1.calcSimilarity(viewpoint_2);
	}
	
	private float calcUserInterestOfTopic(Long longUserId, Topic topic,
			TweetSet tweetSet,
			UserTweetRelationSet utRelations) {
		
		Map<Long, Float> mapTweetId2Relevances = m_topicAnalyzer.getTweets_ByUser_OnTopic(longUserId, topic);
		if (mapTweetId2Relevances == null) {
			return 0;
		}
		
		TweetInfo tweetInfo;
		float fRelevance, fRelevance_Sum = 0;
		float fSentimentStrength_Sum = 0;
		for (Long longTweetId : mapTweetId2Relevances.keySet()) {
			
			tweetInfo = tweetSet.getTweetInfo(longTweetId);
			
			fRelevance = mapTweetId2Relevances.get(longTweetId);
			fRelevance_Sum += fRelevance;
			
			//*
			fSentimentStrength_Sum += ( fRelevance * 
					(tweetInfo.m_nPositiveSentiment + tweetInfo.m_nNegativeSentiment) );
			//*/
			/*
			fSentimentStrength_Sum += ( fRelevance * 
					( ( (float) tweetInfo.m_nPositiveSentiment ) / tweetInfo.m_nNegativeSentiment) );
			//*/
		}
		if (fRelevance_Sum == 0) {
			return 0;
		}
		fSentimentStrength_Sum = fSentimentStrength_Sum / fRelevance_Sum;
		
		
		Set<Long> setAllTweetIdsByUser = utRelations.getTweetIdsByUserId(longUserId);
		if (setAllTweetIdsByUser == null) {
			return 0;
		}
		
		float fSentimentStrength_Sum_Denominator = 0;
		for (Long longTweetId : setAllTweetIdsByUser) {
			
			tweetInfo = tweetSet.getTweetInfo(longTweetId);
			
			fSentimentStrength_Sum_Denominator += (tweetInfo.m_nPositiveSentiment 
					+ tweetInfo.m_nNegativeSentiment);
		}
		
		if (fSentimentStrength_Sum_Denominator == 0) {
			return 0;
		}
		
		return fSentimentStrength_Sum / fSentimentStrength_Sum_Denominator;
	}

	private Opinion calcUserViewpoint_ForTweet(Long longUserId, TweetInfo tweetInfo,
			int nMaxNumOfTopics, float fMinRelevance) {
		
		List<Topic> listAllTopics = m_topicAnalyzer.getTopicsOfUser(longUserId);
		if (listAllTopics == null) {
			return null;
		}
		
		Map<Topic, Float> mapRelevantTopics = m_topicAnalyzer.getRelevantTopicsOfTweet(
				tweetInfo, listAllTopics,
				nMaxNumOfTopics, fMinRelevance);
		
		if (mapRelevantTopics == null) {
			return null;
		}
		
		Opinion viewpoint_Result = new Opinion();
		
		float fTopicRelevance;
		Opinion viewpointOfUser_ForTopic;
		for (Topic topic : mapRelevantTopics.keySet()) {
			
			fTopicRelevance = mapRelevantTopics.get(topic).floatValue();
			
			viewpointOfUser_ForTopic = getUserViewpoint_ForTopic(longUserId, topic);
			if (viewpointOfUser_ForTopic != null) {
			    viewpoint_Result.accumulate(viewpointOfUser_ForTopic, fTopicRelevance);
			}
		}
		
		mapRelevantTopics.clear();
		mapRelevantTopics = null;
		
		return viewpoint_Result;
	}

	private Opinion getUserViewpoint_ForTopic(Long longUserId, Topic topic) {
		
		Opinion viewpoint = null;
		
		if (m_userOpinions_ForTopics != null) {
			
			Map<Topic, Opinion> map = m_userOpinions_ForTopics.get(longUserId);
			if (map != null) {
				viewpoint = map.get(topic);
			}
		}
		
		if (viewpoint == null) {
			
			viewpoint = calcUserViewpoint_ForTopic(longUserId, topic);
			if (viewpoint != null) {
			    recordUserViewpoint_ForTopic(longUserId, topic, viewpoint);
			}
		}
		
		return viewpoint;
	}

	private Opinion calcUserViewpoint_ForTopic(Long longUserId, Topic topic) {
		
		Map<Long, Float> mapTweetId2Relevances = m_topicAnalyzer.getTweets_ByUser_OnTopic(longUserId, topic);
		if (mapTweetId2Relevances == null) {
			return null;
		}
		
		Map<Long, Opinion> mapTweetId2Viewpoints = m_userOpinions_ForTweets.get(longUserId);
		if (mapTweetId2Viewpoints == null) {
			return null;
		}
		
		Opinion viewpoint_Result = new Opinion();
		
		float fRelevance, fRelevance_Sum = 0;
		Opinion viewpointOfTweet;
		for (Long longTweetId : mapTweetId2Relevances.keySet()) {
			
			fRelevance = mapTweetId2Relevances.get(longTweetId).floatValue();
			fRelevance_Sum += fRelevance;
			
			viewpointOfTweet = mapTweetId2Viewpoints.get(longTweetId);
			viewpoint_Result.accumulate(viewpointOfTweet, fRelevance);
		}
		
		mapTweetId2Relevances.clear();
		mapTweetId2Relevances = null;
		
		viewpoint_Result.normalizeBy(fRelevance_Sum);
		return viewpoint_Result;
	}

	private void recordUserViewpoint_ForTopic(Long longUserId, Topic topic,
			Opinion viewpoint) {
		
		if (m_userOpinions_ForTopics == null) {
			m_userOpinions_ForTopics = new HashMap<Long, Map<Topic, Opinion>>();
		}
		
		Map<Topic, Opinion> map = m_userOpinions_ForTopics.get(longUserId);
		if (map == null) {
			map = new HashMap<Topic, Opinion>();
			m_userOpinions_ForTopics.put(longUserId, map);
		}
		
		Opinion vp = map.get(topic);
		if (vp != null) {
			vp.release();
		}
		
		map.put(topic, viewpoint);
	}

	public void release() {
		
		if (m_userOpinions_ForTweets != null) {
			
			for (Map<Long, Opinion> map : m_userOpinions_ForTweets.values()) {
				
				if (map != null) {
					
					for (Opinion vp : map.values()) {
						vp.release();
					}
					
					map.clear();
				}
			}
			
			m_userOpinions_ForTweets.clear();
			m_userOpinions_ForTweets = null;
		}
		
		if (m_userOpinions_ForTopics != null) {
			
			for (Map<Topic, Opinion> map : m_userOpinions_ForTopics.values()) {
				
				if (map != null) {
					
					for (Opinion vp : map.values()) {
						vp.release();
					}
					
					map.clear();
				}
			}
			
			m_userOpinions_ForTopics.clear();
			m_userOpinions_ForTopics = null;
		}
	}

}
