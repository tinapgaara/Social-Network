package yt2443.aProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import yt2443.aProject.analyzer.TopicAnalyzer;
import yt2443.aProject.analyzer.OpinionAnalyzer;
import yt2443.aProject.dataProc.Phase4DataManager;
import yt2443.aProject.dataProc.Phase5DataManager;
import yt2443.aProject.dataProc.Phase6DataManager;
import yt2443.aProject.entity.FollowerNet;
import yt2443.aProject.entity.TestResult_ForTweet;
import yt2443.aProject.entity.Topic;
import yt2443.aProject.entity.TweetInfo;
import yt2443.aProject.entity.TweetRelationSet;
import yt2443.aProject.entity.TweetSet;
import yt2443.aProject.entity.UserTweetRelationSet;
import yt2443.aProject.entity.Opinion;

public class Test {

	public static final String DATA_FILE_DIR_ForRead = "./../../../data/";	
	public static final String FILE_NAME_tweets = "tweets.txt";
	public static final String FILE_NAME_followers_y = "followers_y.txt";
	public static final String FILE_NAME_followers_n = "followers_n.txt";

	public  static final String FILE_NAME_tweets_ForTest = "tweets_test.txt";
	public  static final String FILE_NAME_utRelationSet_ForTest = "utRelations_test.txt";
	public  static final String FILE_NAME_ttRelationSet_ForTest = "ttRelation_test.txt";
	
	private static final String FILE_NAME_topics = "topics.txt";
	private static final String FILE_NAME_sentiments = "sentiments.txt";
	private static final int MAX_NumOfTopics_ForEachTweet = 3;
	private static final float MIN_TweetTopicRelevance = 0.001f;

	private static final String DATA_FILE_DIR_Result_ForWrite = "./bin/data/result/";
	private static final String FILE_NAME_testResult = "testResult.txt";
	private static final String FILE_NAME_sentiments_test = "sentiments_test.txt";

	private static final float WEIGHT_UserInterest = 0.5f;
	private static final float WEIGHT_SocialInfluence = 0.1f;
	
	private static final int ITERATION_Times = 10;
	private static final float TEST_RESULT_SCOPE = 0.02f;
	private static final boolean FLAG_OnlySN = false;

	
	public static void test() {
		
		FollowerNet followerNet = null, followerNet_2 = null;
		TweetSet tweetSet = null;
		UserTweetRelationSet utRelationSet = null;
		
		TopicAnalyzer topicAnalyzer = null;
		OpinionAnalyzer viewpointAnalyzer = null;
		
		TweetSet tweetSet_Test = null;
		UserTweetRelationSet utRelationSet_Test = null;
		TweetRelationSet ttRelationSet_Test = null;
		Set<Long> setAllUserIds = null;
		
		List<TestResult_ForTweet> listTestResults = null;
		
		Phase4DataManager dataReader_Phase4 = null;
		Phase5DataManager dataReader_Phase5 = null;
		Phase6DataManager dataWriter_Phase6 = null;
		try {
			
			// step-1: load users and followers
			
			dataReader_Phase4 = Phase4DataManager.getInstance(DATA_FILE_DIR_ForRead);
			
			followerNet = new FollowerNet();
			dataReader_Phase4.load_followerNet(
					followerNet,
					FILE_NAME_followers_y);

			followerNet_2 = new FollowerNet();
			dataReader_Phase4.load_followerNet(
					followerNet_2,
					FILE_NAME_followers_n);
			
			followerNet.combine(followerNet_2);
			setAllUserIds = followerNet.getAllUserIds();
			/*
			setAllUserIds = getUserSet_Seed(followerNet_1);
			setAllUserIds.addAll( 
					getUserSet_Followers(followerNet_1, followerNet_2));
			//*/
			
			Logger.showInfo("Phase 6 -- step-1 completed. userNum=" + setAllUserIds.size());
			
			
			// step-2: load tweet set for testing 
		    
			tweetSet_Test = new TweetSet();
			utRelationSet_Test = new UserTweetRelationSet();
			ttRelationSet_Test = new TweetRelationSet();

			dataReader_Phase5 = Phase5DataManager.getInstance(DATA_FILE_DIR_ForRead);			
			dataReader_Phase5.load_tweetSet_ForTest(tweetSet_Test,
					FILE_NAME_tweets_ForTest);	// bForSentimentAnalysis
			dataReader_Phase5.load_userTweetRelationSet_ForTest(
					utRelationSet_Test,
					null, // tuRelationSet
					setAllUserIds,
					FILE_NAME_utRelationSet_ForTest);
			dataReader_Phase5.load_tweetRelationSet(
					ttRelationSet_Test,
					null, // tweetSet_AsConstraint
					FILE_NAME_ttRelationSet_ForTest);

		    Logger.showInfo("Number of test tweets [" + tweetSet_Test.getNumOfTweets() + "]");
			Logger.showInfo("Phase 6 -- step-2 completed.");

			
			// step-3: load tweets and topics, then assign topic(s) for each tweet
			
			tweetSet = new TweetSet();
			utRelationSet = new UserTweetRelationSet();
			dataReader_Phase4.load_tweetSet(
					tweetSet,
					utRelationSet,
					FILE_NAME_tweets);
			
			topicAnalyzer = TopicAnalyzer.getInstance();
			dataReader_Phase4.load_topics(
					topicAnalyzer,
					FILE_NAME_topics);
			
			Map<Long, Set<Long>> mapUser2Tweets = utRelationSet.getUser2Tweets();
			if (mapUser2Tweets != null) {
				
				List<Topic> listAllTopicsOfUser;
				Set<Long> setTweetIds;
				TweetInfo tweetInfo;
				Map<Topic, Float> mapRelevantTopics; 
				for (Long longUserId : mapUser2Tweets.keySet()) {
					
					listAllTopicsOfUser = topicAnalyzer.getTopicsOfUser(longUserId);
					if (listAllTopicsOfUser == null) {
						Logger.showErrMsg("No topic for the user[" + longUserId + "]");
						continue;
					}
					
					setTweetIds = mapUser2Tweets.get(longUserId);
					if (setTweetIds == null) {
						Logger.showErrMsg("No tweet for the user[" + longUserId + "]");
						continue;
					}
					
					for (Long longTweetId : setTweetIds) {
						
						tweetInfo = tweetSet.getTweetInfo(longTweetId);
						
						mapRelevantTopics = topicAnalyzer.getRelevantTopicsOfTweet(tweetInfo, listAllTopicsOfUser,
								MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance);
						
						if (mapRelevantTopics != null) {
							topicAnalyzer.addRelevantTopicsOfTweet(longUserId, longTweetId, mapRelevantTopics);
						}
					}
				}
			}
			
			Logger.showInfo("Phase 6 -- step-3 completed.");
			

			// step-4: load sentiment analysis results and analyze viewpoints for topics and tweets
			
			dataReader_Phase4.load_sentiments(
					tweetSet,
					FILE_NAME_sentiments);
			
			dataReader_Phase4.load_sentiments(
					tweetSet_Test,
					FILE_NAME_sentiments_test);
			
			if (mapUser2Tweets != null) {
				
				viewpointAnalyzer = OpinionAnalyzer.getInstance();
				
				Set<Long> setTweetIds;
				TweetInfo tweetInfo;
				Opinion viewpoint;
				for (Long longUserId : mapUser2Tweets.keySet()) {
					
					setTweetIds = mapUser2Tweets.get(longUserId);
					if (setTweetIds == null) {
						Logger.showErrMsg("No tweet for the user[" + longUserId + "]");
						continue;
					}
					
					for (Long longTweetId : setTweetIds) {
						
						tweetInfo = tweetSet.getTweetInfo(longTweetId);
						viewpoint = new Opinion(tweetInfo);
						viewpointAnalyzer.addTweetOpinion(longUserId, longTweetId, viewpoint);
					}
				}
			}
			
			Logger.showInfo("Phase 6 -- step-4 completed.");
			
			
			// step-5: test
			
			/*
			listTestResults = doTest_UserInterest(
					viewpointAnalyzer,
					setAllUserIds,
					tweetSet_Test, utRelationSet_Test, ttRelationSet_Test,
					tweetSet, utRelationSet);
			//*/
			
			/*
			listTestResults = doTest_ViewpointSimilarity(
					viewpointAnalyzer,
					setAllUserIds,
					tweetSet_Test, utRelationSet_Test, ttRelationSet_Test);
			//*/
			
			/*
			listTestResults = doTest_BothUserInterestAndViewpointSimilarity(
					viewpointAnalyzer,
					setAllUserIds,
					tweetSet_Test, utRelationSet_Test, ttRelationSet_Test,
					tweetSet, utRelationSet);
			//*/

			//*
			if (FLAG_OnlySN) {
				Logger.showInfo("Phase 6 -- test begin, mode = ONLY-SN");
				
				listTestResults = doTest_OnlySN(
						viewpointAnalyzer,
						setAllUserIds,
						tweetSet_Test, utRelationSet_Test, ttRelationSet_Test,
						tweetSet, utRelationSet,
						followerNet,
						ITERATION_Times);
			}
			
			else {
				Logger.showInfo("Phase 6 -- test begin, mode = OpinionAnalysis + SN");
				
				listTestResults = doTest_WithSN(
						viewpointAnalyzer,
						setAllUserIds,
						tweetSet_Test, utRelationSet_Test, ttRelationSet_Test,
						tweetSet, utRelationSet,
						followerNet,
						ITERATION_Times);
			}
			//*/
			
			// step-6: write out test results
			dataWriter_Phase6 = Phase6DataManager.getInstance(DATA_FILE_DIR_Result_ForWrite);			
			dataWriter_Phase6.write_testResult(listTestResults,
					FILE_NAME_testResult,
					TEST_RESULT_SCOPE,
					FLAG_OnlySN);
			
			if (FLAG_OnlySN) {
				Logger.showInfo("Phase 6 -- test end. mode = ONLY-SN,  Result file = [" + FILE_NAME_testResult + "]");
			}
			else {
				Logger.showInfo("Phase 6 -- test end. mode = OpinionAnalysis + SN,  Result file = [" + FILE_NAME_testResult + "]");
			}
		}
		
		catch (SnsException ex) {
	    	// ex.printStackTrace();
			Logger.showErrMsg(ex.getExDesp());
		}
		
		finally {
			
			if (dataWriter_Phase6 != null) {
				dataWriter_Phase6.release();
				dataWriter_Phase6 = null;
			}
			
			if (tweetSet_Test != null) {
				tweetSet_Test.release();
				tweetSet_Test = null;
			}
			if (utRelationSet_Test != null) {
				utRelationSet_Test.release();
				utRelationSet_Test = null;
			}
			if (setAllUserIds != null) {
				setAllUserIds.clear();
				setAllUserIds = null;
			}
			
			if (viewpointAnalyzer != null) {
				viewpointAnalyzer.release();
				viewpointAnalyzer = null;
			}
			if (topicAnalyzer != null) {
				topicAnalyzer.release();
				topicAnalyzer = null;
			}
			
			if (utRelationSet != null) {
				utRelationSet.release();
				utRelationSet = null;
			}
			if (tweetSet != null) {
				tweetSet.release();
				tweetSet = null;
			}
			if (followerNet != null) {
				followerNet.release();
				followerNet = null;
			}
			if (followerNet_2 != null) {
				followerNet_2.release();
				followerNet_2 = null;
			}
			
			if (dataReader_Phase5 != null) {
				dataReader_Phase5.release();
				dataReader_Phase5 = null;
			}
			if (dataReader_Phase4 != null) {
				dataReader_Phase4.release();
				dataReader_Phase4 = null;
			}
		}
	}


	private static List<TestResult_ForTweet> doTest_WithSN(
			OpinionAnalyzer viewpointAnalyzer,
			Set<Long> setAllUserIds,
			TweetSet tweetSet_Test, UserTweetRelationSet utRelationSet_Test, TweetRelationSet ttRelationSet_Test,
			TweetSet tweetSet, UserTweetRelationSet utRelationSet,
			FollowerNet followerNet,
			int cIterationTimes) {
		
		List<TestResult_ForTweet> listTestResults = new ArrayList<TestResult_ForTweet>();
		
		TestResult_ForTweet testResult;
		
		int cUsers = setAllUserIds.size();
		int nMaxNumOfFollowedUserIds = followerNet.getMaxNumOfFollowedUserIds(setAllUserIds);		
		
		Set<Long> setRetweetIds;
	    Map<Long, TweetInfo> mapTweetInfos = tweetSet_Test.getTweetInfos();
	    TweetInfo tweetInfo;
	    float fInterest, fSimilarity;
	    float fScore, fScore_DeltaBySocialInfluence;
	    float fMaxScore, fMinScore, fAvgScore;
	    float fMaxScore_New, fMinScore_New, fAvgScore_New;
	    Map<Long, Float> mapUserScores = new HashMap<Long, Float>();
	    Map<Long, Float> mapUserScores_New = new HashMap<Long, Float>();
	    for (Long longTweetId : mapTweetInfos.keySet()) {
	    	
	    	testResult = new TestResult_ForTweet(longTweetId);
	    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longTweetId));
	    	setRetweetIds = ttRelationSet_Test.getAllRetweetIds();
	    	if (setRetweetIds != null) {
		    	for (Long longRetweetId : setRetweetIds) {
			    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longRetweetId));
		    	}
	    	}
	    	
	    	listTestResults.add(testResult);
	    	
	    	
	    	tweetInfo = tweetSet_Test.getTweetInfo(longTweetId);
	    	
	    	fMaxScore = 0;
	    	fMinScore = 0;
	    	fAvgScore = 0;
			for (Long longUserId : setAllUserIds) {
				
	    	    fInterest = viewpointAnalyzer.calcUserInterestOfTweet(longUserId, tweetInfo,
	    	    	MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance,
	    			tweetSet,
	    			utRelationSet);
	    	    
	    	    fSimilarity = viewpointAnalyzer.calcOpinionSimilarity(longUserId, tweetInfo,
		    	    	MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance);
		    	
	    	    fScore = WEIGHT_UserInterest * fInterest + (1 - WEIGHT_UserInterest) * fSimilarity;
	    	    mapUserScores.put(longUserId, new Float(fScore));
	    	    
	    	    fAvgScore += fScore;
	    	    if ( (fMaxScore == 0) || (fScore > fMaxScore) ) {
	    	    	fMaxScore = fScore;
	    	    }
	    	    if ( (fMinScore == 0) || (fScore < fMinScore) ) {
	    	    	fMinScore = fScore;
	    	    }
			}
			fAvgScore = fAvgScore / cUsers;
			
			for (int nIterationIndex = 0; nIterationIndex < cIterationTimes; nIterationIndex++) {
				
		    	fMaxScore_New = 0;
		    	fMinScore_New = 0;
		    	fAvgScore_New = 0;
				for (Long longUserId : setAllUserIds) {
					
					if (fMaxScore > 0) {
						fScore_DeltaBySocialInfluence = calcSocialInfluence(
								viewpointAnalyzer,
								tweetInfo,
								longUserId,
								followerNet, mapUserScores,
								fMaxScore, fMinScore, fAvgScore,
								nMaxNumOfFollowedUserIds,
								false);	// bOnlySN
					}
					else {
						fScore_DeltaBySocialInfluence = 0;
					}
					
					fScore = mapUserScores.get(longUserId).floatValue();
					fScore = (1 - WEIGHT_SocialInfluence) * fScore +
							WEIGHT_SocialInfluence * fScore_DeltaBySocialInfluence;
					mapUserScores_New.put(longUserId, new Float(fScore));
					
		    	    fAvgScore_New += fScore;
		    	    if ( (fMaxScore_New == 0) || (fScore > fMaxScore_New) ) {
		    	    	fMaxScore_New = fScore;
		    	    }
		    	    if ( (fMinScore_New == 0) || (fScore < fMinScore_New) ) {
		    	    	fMinScore_New = fScore;
		    	    }
				}
				fAvgScore_New = fAvgScore_New / cUsers;
				
				fMaxScore = fMaxScore_New;
				fMinScore = fMinScore_New;
				fAvgScore = fAvgScore_New;
				
				for (Long longUserId : mapUserScores_New.keySet()) {	
					mapUserScores.put(longUserId, mapUserScores_New.get(longUserId));
				}
			}
			
			for (Long longUserId : setAllUserIds) {
	    	    testResult.addUserScore(longUserId, mapUserScores.get(longUserId).floatValue());
			}
			
		    testResult.sortByScores();
		    testResult.cutTail(setAllUserIds.size(), TEST_RESULT_SCOPE);
	    }
	    
	    mapUserScores.clear();
	    mapUserScores = null;
	    
	    mapUserScores_New.clear();
	    mapUserScores_New = null;
	    
	    return listTestResults;
	}

	private static List<TestResult_ForTweet> doTest_OnlySN(
			OpinionAnalyzer viewpointAnalyzer,
			Set<Long> setAllUserIds,
			TweetSet tweetSet_Test, UserTweetRelationSet utRelationSet_Test, TweetRelationSet ttRelationSet_Test,
			TweetSet tweetSet, UserTweetRelationSet utRelationSet,
			FollowerNet followerNet,
			int cIterationTimes) {
		
		List<TestResult_ForTweet> listTestResults = new ArrayList<TestResult_ForTweet>();
		
		TestResult_ForTweet testResult;
		
		int cUsers = setAllUserIds.size();
		int nMaxNumOfFollowedUserIds = followerNet.getMaxNumOfFollowedUserIds(setAllUserIds);		
		
		Set<Long> setRetweetIds;
	    Map<Long, TweetInfo> mapTweetInfos = tweetSet_Test.getTweetInfos();
	    TweetInfo tweetInfo;
	    float fScore, fScore_DeltaBySocialInfluence;
	    float fMaxScore, fMinScore, fAvgScore;
	    float fMaxScore_New, fMinScore_New, fAvgScore_New;
	    Map<Long, Float> mapUserScores = new HashMap<Long, Float>();
	    Map<Long, Float> mapUserScores_New = new HashMap<Long, Float>();
	    Random random = new Random();
	    for (Long longTweetId : mapTweetInfos.keySet()) {
	    	
	    	testResult = new TestResult_ForTweet(longTweetId);
	    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longTweetId));
	    	setRetweetIds = ttRelationSet_Test.getAllRetweetIds();
	    	if (setRetweetIds != null) {
		    	for (Long longRetweetId : setRetweetIds) {
			    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longRetweetId));
		    	}
	    	}
	    	
	    	listTestResults.add(testResult);
	    	
	    	
	    	tweetInfo = tweetSet_Test.getTweetInfo(longTweetId);
	    	
	    	fMaxScore = 0;
	    	fMinScore = 0;
	    	fAvgScore = 0;
			for (Long longUserId : setAllUserIds) {
				
				/*
				if (isTweetSentByUser(longTweetId, longUserId,
						utRelationSet_Test, ttRelationSet_Test)) {
	    	        fScore = 1;
				}
				else {
	    	        fScore = 0;
				}
				//*/
				fScore = ( (float) random.nextInt(cUsers) ) / cUsers;
				
	    	    mapUserScores.put(longUserId, new Float(fScore));
	    	    
	    	    fAvgScore += fScore;
	    	    if ( (fMaxScore == 0) || (fScore > fMaxScore) ) {
	    	    	fMaxScore = fScore;
	    	    }
	    	    if ( (fMinScore == 0) || (fScore < fMinScore) ) {
	    	    	fMinScore = fScore;
	    	    }
			}
			fAvgScore = fAvgScore / cUsers;
			
			for (int nIterationIndex = 0; nIterationIndex < cIterationTimes; nIterationIndex++) {
				
		    	fMaxScore_New = 0;
		    	fMinScore_New = 0;
		    	fAvgScore_New = 0;
				for (Long longUserId : setAllUserIds) {
					
					if (fMaxScore > 0) {
						fScore_DeltaBySocialInfluence = calcSocialInfluence(
								viewpointAnalyzer,
								tweetInfo,
								longUserId,
								followerNet, mapUserScores,
								fMaxScore, fMinScore, fAvgScore,
								nMaxNumOfFollowedUserIds,
								true);	// bOnlySN
					}
					else {
						fScore_DeltaBySocialInfluence = 0;
					}
					
					fScore = mapUserScores.get(longUserId).floatValue();
					fScore = (1 - WEIGHT_SocialInfluence) * fScore +
							WEIGHT_SocialInfluence * fScore_DeltaBySocialInfluence;
					mapUserScores_New.put(longUserId, new Float(fScore));
					
		    	    fAvgScore_New += fScore;
		    	    if ( (fMaxScore_New == 0) || (fScore > fMaxScore_New) ) {
		    	    	fMaxScore_New = fScore;
		    	    }
		    	    if ( (fMinScore_New == 0) || (fScore < fMinScore_New) ) {
		    	    	fMinScore_New = fScore;
		    	    }
				}
				fAvgScore_New = fAvgScore_New / cUsers;
				
				fMaxScore = fMaxScore_New;
				fMinScore = fMinScore_New;
				fAvgScore = fAvgScore_New;
				
				for (Long longUserId : mapUserScores_New.keySet()) {	
					mapUserScores.put(longUserId, mapUserScores_New.get(longUserId));
				}
			}
			
			for (Long longUserId : setAllUserIds) {
	    	    testResult.addUserScore(longUserId, mapUserScores.get(longUserId).floatValue());
			}
			
		    testResult.sortByScores();
		    testResult.cutTail(setAllUserIds.size(), TEST_RESULT_SCOPE);
	    }
	    
	    mapUserScores.clear();
	    mapUserScores = null;
	    
	    mapUserScores_New.clear();
	    mapUserScores_New = null;
	    
	    return listTestResults;
	}


	private static boolean isTweetSentByUser(Long longTweetId, Long longUserId,
			UserTweetRelationSet utRelationSet,
			TweetRelationSet ttRelationSet) {
		
		Set<Long> setTweetIds = utRelationSet.getTweetIdsByUserId(longUserId);
		if (setTweetIds == null) {
		    return false;
		}
		
		if (setTweetIds.contains(longTweetId)) {
			return true;
		}
		
		Set<Long> setRetweetIds = ttRelationSet.getRetweetIds(longTweetId);
		if (setRetweetIds == null) {
			return false;
		}
		
		for (Long tweetId : setTweetIds) {
			if (setRetweetIds.contains(tweetId)) {
				return true;
			}
		}
		
		return false;
	}


	private static float calcSocialInfluence(
			OpinionAnalyzer viewpointAnalyzer,
			TweetInfo tweetInfo,
			Long longUserId, FollowerNet followerNet,
			Map<Long, Float> mapUserScores,
			float fMaxScore, float fMinScore, float fAvgScore,
			int nMaxNumOfFollowedUserIds,
			boolean bOnlySN) {
		
		float fMaxDelta = fMaxScore - fAvgScore;
		float fMinDelta = fAvgScore - fMinScore;
		float fMinFloat = 10 * Float.MIN_VALUE;
		if ( (fMaxDelta <= fMinFloat) || (fMinDelta <= fMinFloat) ) {
			return 0;
		}
		
		Set<Long> setFollowedUserIds = followerNet.getFollowedUserIdsByFollowerIds(longUserId);
		
		if ( (setFollowedUserIds == null) || setFollowedUserIds.isEmpty() ) {
			return 0;
		}
		

		float fOpinionSimilarity;
		float fInfluence = 0;
		float fScore;
		Float floatScore;
		for (Long longFollowedUserId : setFollowedUserIds) {
			
			if (bOnlySN) {
				fOpinionSimilarity = 1;
			}
			else {
				fOpinionSimilarity = viewpointAnalyzer.calcUserSimilarity_ForTweet(
						longUserId, longFollowedUserId,
						tweetInfo,
						MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance);
			}
			
			if (fOpinionSimilarity != 0) {
				
				floatScore = mapUserScores.get(longFollowedUserId);
				if (floatScore == null) {
					fScore = 0;
				}
				else {
					fScore = floatScore.floatValue();
				}
				
				if (fScore >= fAvgScore) {
				    fInfluence += ( ( (fScore - fAvgScore) / fMaxDelta ) / nMaxNumOfFollowedUserIds ) * fOpinionSimilarity;
				}
				/*
				else {
				    fInfluence -= ( ( (fAvgScore - fScore) / fMinDelta ) / nMaxNumOfFollowedUserIds ) * fOpinionSimilarity;
				}
				//*/
			}
		}
		
		return fInfluence;
	}


	private static List<TestResult_ForTweet> doTest_ViewpointSimilarity(
			OpinionAnalyzer viewpointAnalyzer,
			Set<Long> setAllUserIds,
			TweetSet tweetSet_Test,
			UserTweetRelationSet utRelationSet_Test,
			TweetRelationSet ttRelationSet_Test) {
		
		List<TestResult_ForTweet> listTestResults = new ArrayList<TestResult_ForTweet>();
		
		TestResult_ForTweet testResult;
		
		Set<Long> setRetweetIds;
 	    Map<Long, TweetInfo> mapTweetInfos = tweetSet_Test.getTweetInfos();
	    for (Long longTweetId : mapTweetInfos.keySet()) {
	    	
	    	testResult = new TestResult_ForTweet(longTweetId);
	    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longTweetId));
	    	setRetweetIds = ttRelationSet_Test.getAllRetweetIds();
	    	if (setRetweetIds != null) {
		    	for (Long longRetweetId : setRetweetIds) {
			    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longRetweetId));
		    	}
	    	}
	    	
	    	listTestResults.add(testResult);
	    	
	    	TweetInfo tweetInfo = tweetSet_Test.getTweetInfo(longTweetId);
	    	
			for (Long longUserId : setAllUserIds) {
				
	    	    float fSimilarity = viewpointAnalyzer.calcOpinionSimilarity(longUserId, tweetInfo,
	    	    	MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance);
	    	    
	    	    testResult.addUserScore(longUserId, fSimilarity);
			}
			
		    testResult.sortByScores();
		    testResult.cutTail(setAllUserIds.size(), TEST_RESULT_SCOPE);
	    }
	    
	    return listTestResults;
	}

	private static List<TestResult_ForTweet> doTest_UserInterest(
			OpinionAnalyzer viewpointAnalyzer,
			Set<Long> setAllUserIds,
			TweetSet tweetSet_Test,
			UserTweetRelationSet utRelationSet_Test,
			TweetRelationSet ttRelationSet_Test,
			TweetSet tweetSet, UserTweetRelationSet utRelationSet) {
		
		List<TestResult_ForTweet> listTestResults = new ArrayList<TestResult_ForTweet>();
		
		TestResult_ForTweet testResult;
		
		Set<Long> setRetweetIds;
	    Map<Long, TweetInfo> mapTweetInfos = tweetSet_Test.getTweetInfos();
	    for (Long longTweetId : mapTweetInfos.keySet()) {
	    	
	    	testResult = new TestResult_ForTweet(longTweetId);
	    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longTweetId));
	    	setRetweetIds = ttRelationSet_Test.getAllRetweetIds();
	    	if (setRetweetIds != null) {
		    	for (Long longRetweetId : setRetweetIds) {
			    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longRetweetId));
		    	}
	    	}
	    	
	    	listTestResults.add(testResult);
	    	
	    	TweetInfo tweetInfo = tweetSet_Test.getTweetInfo(longTweetId);
	    	
			for (Long longUserId : setAllUserIds) {
				
	    	    float fInterest = viewpointAnalyzer.calcUserInterestOfTweet(longUserId, tweetInfo,
	    	    	MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance,
	    			tweetSet,
	    			utRelationSet);
	    	    
	    	    testResult.addUserScore(longUserId, fInterest);
			}
			
		    testResult.sortByScores();
		    testResult.cutTail(setAllUserIds.size(), TEST_RESULT_SCOPE);
	    }
	    
	    return listTestResults;
	}

	private static List<TestResult_ForTweet> doTest_BothUserInterestAndViewpointSimilarity(
			OpinionAnalyzer viewpointAnalyzer,
			Set<Long> setAllUserIds,
			TweetSet tweetSet_Test,
			UserTweetRelationSet utRelationSet_Test,
			TweetRelationSet ttRelationSet_Test,
			TweetSet tweetSet, UserTweetRelationSet utRelationSet) {
		
		List<TestResult_ForTweet> listTestResults = new ArrayList<TestResult_ForTweet>();
		
		TestResult_ForTweet testResult;
		
		Set<Long> setRetweetIds;
	    Map<Long, TweetInfo> mapTweetInfos = tweetSet_Test.getTweetInfos();
	    for (Long longTweetId : mapTweetInfos.keySet()) {
	    	
	    	testResult = new TestResult_ForTweet(longTweetId);
	    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longTweetId));
	    	setRetweetIds = ttRelationSet_Test.getAllRetweetIds();
	    	if (setRetweetIds != null) {
		    	for (Long longRetweetId : setRetweetIds) {
			    	testResult.addCorrectUserIds(utRelationSet_Test.getUserIdsByTweetId(longRetweetId));
		    	}
	    	}
	    	
	    	listTestResults.add(testResult);
	    	
	    	TweetInfo tweetInfo = tweetSet_Test.getTweetInfo(longTweetId);
	    	
			for (Long longUserId : setAllUserIds) {
				
	    	    float fInterest = viewpointAnalyzer.calcUserInterestOfTweet(longUserId, tweetInfo,
	    	    	MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance,
	    			tweetSet,
	    			utRelationSet);
	    	    
	    	    float fSimilarity = viewpointAnalyzer.calcOpinionSimilarity(longUserId, tweetInfo,
		    	    	MAX_NumOfTopics_ForEachTweet, MIN_TweetTopicRelevance);
		    	    
	    	    testResult.addUserScore(longUserId, 
	    	    		WEIGHT_UserInterest * fInterest + (1 - WEIGHT_UserInterest) * fSimilarity);
			}
			
		    testResult.sortByScores();
		    testResult.cutTail(setAllUserIds.size(), TEST_RESULT_SCOPE);
	    }
	    
	    return listTestResults;
	}

}
