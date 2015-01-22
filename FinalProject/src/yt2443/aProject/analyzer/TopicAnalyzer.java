package yt2443.aProject.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import yt2443.aProject.entity.Topic;
import yt2443.aProject.entity.TweetInfo;

public class TopicAnalyzer {

	private static final String STRING_WordDelimiter = " ";
	
	private static TopicAnalyzer m_theInstance = null;
	
	private Map<Long, List<Topic>> m_mapUserTopics;
	private Map<Long, Map<Long, Map<Topic, Float>>> m_mapUserTweetTopics;
	
	
	public static TopicAnalyzer getInstance() {
		
		if (m_theInstance == null) {
			m_theInstance = new TopicAnalyzer();
		}
		
		return m_theInstance;
	}

	private TopicAnalyzer() {
		
		m_mapUserTopics = null;
		m_mapUserTweetTopics = null;
	}
	
	public Map<Long, Float> getTweets_ByUser_OnTopic(Long longUserId, Topic topic) {
		
		Map<Long, Map<Topic, Float>> mapTweetIds2Topics = null;
		
		if (m_mapUserTweetTopics != null) {
			mapTweetIds2Topics = m_mapUserTweetTopics.get(longUserId);
		}
		
		if (mapTweetIds2Topics == null) {
			return null;
		}
		
		Map<Long, Float> mapTweetId2RelevanceOfTheTopic = null;
		
		Float floatRelevance;
		Map<Topic, Float> mapTopics2Relevance;
		for (Long longTweetId : mapTweetIds2Topics.keySet()) {
			
			mapTopics2Relevance = mapTweetIds2Topics.get(longTweetId);
			if (mapTopics2Relevance == null) {
				continue;
			}
			
			floatRelevance = mapTopics2Relevance.get(topic);
			if (floatRelevance != null) {
				
				if (mapTweetId2RelevanceOfTheTopic == null) {
					mapTweetId2RelevanceOfTheTopic = new HashMap<Long, Float>();
				}
				
				mapTweetId2RelevanceOfTheTopic.put(longTweetId, floatRelevance);
			}
		}
		
		return mapTweetId2RelevanceOfTheTopic;
	}

	public void addUserTopic(Long longUserId, Topic topic) {
		
		if (m_mapUserTopics == null) {
			m_mapUserTopics = new HashMap<Long, List<Topic>>();
		}
			
		List<Topic> list = m_mapUserTopics.get(longUserId);
		if (list == null) {
			list = new ArrayList<Topic>();
			m_mapUserTopics.put(longUserId, list);
		}
		list.add(topic);
	}
	
	public void addRelevantTopicsOfTweet(Long longUserId, Long longTweetId,
			Map<Topic, Float> mapRelevantTopics) {
		
		if (m_mapUserTweetTopics == null) {
			m_mapUserTweetTopics = new HashMap<Long, Map<Long, Map<Topic, Float>>>();
		}
		
		Map<Long, Map<Topic, Float>> map_ForUser = m_mapUserTweetTopics.get(longUserId);
		if (map_ForUser == null) {
			map_ForUser = new HashMap<Long, Map<Topic, Float>>();
			m_mapUserTweetTopics.put(longUserId, map_ForUser);
		}
		
		Map<Topic, Float> mapRelevantTopics_Old = map_ForUser.get(longTweetId);
		if (mapRelevantTopics_Old != null) {
			mapRelevantTopics_Old.clear();
			mapRelevantTopics_Old = null;
		}
		
		map_ForUser.put(longTweetId, mapRelevantTopics);
	}

	public float calcTweetTopicRelevance(TweetInfo tweetInfo, Topic topic) {
		
		float fRelevance = 0;
		
		StringTokenizer tokenizer = new StringTokenizer(tweetInfo.m_strText_Clear, STRING_WordDelimiter);
		
		while (tokenizer.hasMoreTokens()) {
			
			fRelevance += topic.getWeightOfWord(tokenizer.nextToken());
		}
		
		tokenizer = null;
		
		return fRelevance;
	}
	
	public Map<Topic, Float> getRelevantTopicsOfTweet(TweetInfo tweetInfo, List<Topic> listAllTopics,
			int nMaxNumOfTopics, float fMinRelevance) {
		
		Map<Topic, Float> mapTopicRelevances = null;
		
		List<Topic> listTopics = null;
		
		float fRelevance;
		for (Topic topic : listAllTopics) {
			
			fRelevance = calcTweetTopicRelevance(tweetInfo, topic);
			if (fRelevance >= fMinRelevance) {
				
				if (mapTopicRelevances == null) {
					mapTopicRelevances = new HashMap<Topic, Float>();
				}
				mapTopicRelevances.put(topic, new Float(fRelevance));
				
				if (listTopics == null) {
					listTopics = new ArrayList<Topic>();
				}
				listTopics.add(topic);
			}
		}
		
		if (mapTopicRelevances == null) {
			return null;
		}
		
		Comparator<Topic> comparator = new TopicComparator_ByRelevance(mapTopicRelevances);
		Collections.sort(listTopics, comparator);
		comparator = null;

		int cTopics_Result;
		int cTopics = listTopics.size();
		if (cTopics > nMaxNumOfTopics) {
			cTopics_Result = nMaxNumOfTopics;
		}
		else {
			cTopics_Result = cTopics;
		}
		
		float fRelevance_Sum = 0;
		Topic topic;
		int nIndex;
		for (nIndex = 0; nIndex < cTopics_Result; nIndex++) {
			
			topic = listTopics.get(nIndex);
			fRelevance_Sum += mapTopicRelevances.get(topic).floatValue();
		}
		
		for (nIndex = 0; nIndex < cTopics_Result; nIndex++) {
			
			topic = listTopics.get(nIndex);
			fRelevance = mapTopicRelevances.get(topic).floatValue();
			
			mapTopicRelevances.put(topic, new Float(fRelevance / fRelevance_Sum));
		}
		
		for (nIndex = cTopics_Result; nIndex < cTopics; nIndex++) {
			
			topic = listTopics.get(nIndex);
			mapTopicRelevances.remove(topic);
		}
		
		listTopics.clear();
		listTopics = null;
		
		return mapTopicRelevances;
	}
	
	public List<Topic> getTopicsOfUser(Long longUserId) {
		
		if (m_mapUserTopics == null) {
			return null;
		}
		
		return m_mapUserTopics.get(longUserId);
	}
	
	public class TopicComparator_ByRelevance implements Comparator<Topic> {

		private Map<Topic, Float> m_mapTopicRelevances;
		
		public TopicComparator_ByRelevance(Map<Topic, Float> mapTopicRelevances) {
			
			m_mapTopicRelevances = mapTopicRelevances;
		}
		
		@Override
		public int compare(Topic topic_1, Topic topic_2) {
			
			float fRelevance_1 = m_mapTopicRelevances.get(topic_1).floatValue();
			float fRelevance_2 = m_mapTopicRelevances.get(topic_2).floatValue();
			
			if (fRelevance_1 < fRelevance_2) {
				return +1;
			}
			else if (fRelevance_1 > fRelevance_2) {
				return -1;
			}
			
			return 0;
		}
	}

	public void release() {

		if (m_mapUserTweetTopics != null) {
			
			for (Map<Long, Map<Topic, Float>> map : m_mapUserTweetTopics.values()) {
				
				if (map != null) {
					
					for (Map<Topic, Float> map_2 : map.values()) {
						if (map_2 != null) {
							map_2.clear();
						}
					}
					
					map.clear();
				}
			}
			
			m_mapUserTweetTopics.clear();
			m_mapUserTweetTopics = null;
		}

		if (m_mapUserTopics != null) {
			
			for (List<Topic> list : m_mapUserTopics.values()) {
				
				if (list != null) {
					
					for (Topic topic : list) {
						if (topic != null) {
							topic.release();
						}
					}
					
					list.clear();
				}
			}
			
			m_mapUserTopics.clear();
			m_mapUserTopics = null;
		}
	}

}
