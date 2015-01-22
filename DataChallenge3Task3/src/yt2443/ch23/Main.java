package yt2443.ch23;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Main {

	public static final String FILE_NAME_C1 = "C1.txt"; // test dataset
	public static final String FILE_NAME_C2 = "C2.txt"; // train dataset
	public static final String FILE_NAME_T3 = "T3.txt"; // answer
	public static final String FILE_NAME_Output = "output.txt";
	
	public static final int MAX_NumOfCheckinsForAnyUser = 0;
	public static final int MAX_NumOfCandidateLinks = 3;
	public static final int MAX_NumOfCandidateLinks_Phase2 = 20;
	
	
	public static void main(String[] args) {
		
		boolean bNeedHelp = false;
		
		boolean bTestFlag = false;
		if ( (args == null) || (args.length == 0) ) {	
			bTestFlag = false;
		}
		else if (args.length == 1) {
			String strRunMode = args[0];
			if (strRunMode.equalsIgnoreCase("-test")) {
				bTestFlag = true;
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
		int nMaxNumOfTestUsers_InTestMode = 0;
		int cTestUsers_InTestMode = 0;
		int nMaxNumCheckinItemsForEachTestUser_InTestMode = 0;
		int nMaxNumCheckinItemsForEachTrainUser_InTestMode = 0;
		if (bTestFlag) {
			bTestFlag = true;
			bDebugFlag = true;
			
			nMaxNumOfTestUsers_InTestMode = 1000;
			cTestUsers_InTestMode = 100;
			nMaxNumCheckinItemsForEachTestUser_InTestMode = 800;
			nMaxNumCheckinItemsForEachTrainUser_InTestMode = 800;
		}
		
		run(bTestFlag, bDebugFlag,
				nMaxNumOfTestUsers_InTestMode,
				cTestUsers_InTestMode,
				nMaxNumCheckinItemsForEachTestUser_InTestMode,
				nMaxNumCheckinItemsForEachTrainUser_InTestMode);
	}
	
	private static void run(boolean bTestFlag, boolean bDebugMode,
			int nMaxNumOfTestUsers_InTestMode,
			int cTestUsers_InTestMode,
			int nMaxNumCheckinItemsForEachTestUser_InTestMode,
			int nMaxNumCheckinItemsForEachTrainUser_InTestMode) {
		
		Logger.m_bDebugFlag = bDebugMode;	// set the flag to show or hide debug messages
		
		LinkSet linkSet = null, linkSet_Answer = null, linkSet_Answer_2 = null;
		Matcher matcher = null;
		LocDict locDict = new LocDict();
		Map<Integer, UserInfo> mapTestDataset = null, mapTrainDataset = null;
		IModel model = null;
		long lTime = 0, lStartTime;
		try {
			model = prepareModel();
			IModelParams params = model.getParams();
			
			int cHoursPerUnit = 0;
			int nNumOfTimeUnits = params.getNumOfTimeUnits();
			if (nNumOfTimeUnits != 0) {
				cHoursPerUnit = 24 / nNumOfTimeUnits;		
			}
			
			CheckinsLoader loader = CheckinsLoader.getInstance();
			
			lStartTime = System.currentTimeMillis();
			mapTestDataset = new HashMap<Integer, UserInfo>();
			loader.load(mapTestDataset, locDict,
					FILE_NAME_C1,
					cHoursPerUnit,
					0); // nMaxNumOfCheckinsForAnyUser = 0
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Load file [" + FILE_NAME_C1 + "] completed, time = [" + lTime + "]");
			
			lStartTime = System.currentTimeMillis();
			mapTrainDataset = new HashMap<Integer, UserInfo>();
			int nNumOfLocIds = loader.load(mapTrainDataset, locDict,
					FILE_NAME_C2,
					cHoursPerUnit,
					MAX_NumOfCheckinsForAnyUser);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Load file [" + FILE_NAME_C2 + "] completed, time = [" + lTime + "]");
			
			params.setNumOfLocIds(nNumOfLocIds);
			
			Map<Integer, UserInfo> mapTestDataset_2 = null;
			Map<Integer, UserInfo> mapTrainDataset_2 = null;
			
			if (bTestFlag) {
				linkSet_Answer = new LinkSet();
				linkSet_Answer.buildFromFile(FILE_NAME_T3);
	
				mapTestDataset_2 = new HashMap<Integer, UserInfo>();
				mapTrainDataset_2 = new HashMap<Integer, UserInfo>();
				linkSet_Answer_2 = new LinkSet();
				nNumOfLocIds = generateDatasetsForTestMode(
						mapTestDataset_2, mapTrainDataset_2, linkSet_Answer_2,
						mapTestDataset, mapTrainDataset, linkSet_Answer,
						cTestUsers_InTestMode,
						nMaxNumCheckinItemsForEachTestUser_InTestMode,
						nMaxNumCheckinItemsForEachTrainUser_InTestMode);
				params.setNumOfLocIds(nNumOfLocIds);
				
			    releaseDataset(mapTrainDataset);
				mapTrainDataset = null;
				releaseDataset(mapTestDataset);
				mapTestDataset = null;
			}
			else {
				mapTrainDataset_2 = mapTrainDataset;
				mapTestDataset_2 = mapTestDataset;
			}
			
			matcher = new Matcher(mapTrainDataset_2, mapTestDataset_2);
			int nMaxNumOfTestUsers = 0;
			if (bTestFlag) {
				nMaxNumOfTestUsers = nMaxNumOfTestUsers_InTestMode;
			}
			lStartTime = System.currentTimeMillis();
			linkSet = matcher.doMatch(model,
					MAX_NumOfCandidateLinks, MAX_NumOfCandidateLinks_Phase2, 
					nMaxNumOfTestUsers);
			lTime = System.currentTimeMillis() - lStartTime;
			Logger.showInfo("Match completed, time = [" + lTime + "]");
			
			if (linkSet == null) {
				Logger.showInfo("No link found.");
			}
			else {
				lStartTime = System.currentTimeMillis();
				linkSet.saveIntoFile(FILE_NAME_Output);
				lTime = System.currentTimeMillis() - lStartTime;
				Logger.showInfo("Save file [" + FILE_NAME_Output + "] completed, time = [" + lTime + "]");
				
				if (bTestFlag) {
					int cLinks_Answer = linkSet_Answer_2.getLinkCount();
					
					int cLinks = linkSet.getLinkCount();
					int cCorrectLinks = analyzeResult(linkSet, linkSet_Answer_2,
							mapTestDataset_2, mapTrainDataset_2, model);
					
					LinkSet linkSet_Missed = linkSet_Answer_2.subtract_Reverse(linkSet);
					if (linkSet_Missed != null) {
						Map<Integer, Integer> map_Missed = linkSet_Missed.getLinks();
						for (Integer intKey : map_Missed.keySet()) {
							Logger.showDbgMsg("Miss link= [" + intKey.intValue() +
									" --> " + map_Missed.get(intKey).intValue() + "]");
						}
						linkSet_Missed.release();
						linkSet_Missed = null;
					}
					
					double dPrecision = (double) cCorrectLinks / cLinks;
					double dRecall = (double) cCorrectLinks / cLinks_Answer;
					double dF1 = 2 * (dPrecision * dRecall) / (dPrecision + dRecall);
					Logger.showDbgMsg("Number of links found = [" + cLinks +
							"], correctLinks = [" + cCorrectLinks + 
							"], precision = [" + (100 * dPrecision) + "%], F1 = [" + dF1 + "]");					
				}
			}
		}
		
		catch (SnsException ex) {
        	ex.printStackTrace();
			Logger.showErrMsg(ex.getExDesp());
		}
		
		finally {
			locDict.release();
			locDict = null;
			
			if (linkSet_Answer != null) {
				linkSet_Answer.release();
				linkSet_Answer = null;
			}
			if (linkSet_Answer_2 != null) {
				linkSet_Answer_2.release();
				linkSet_Answer_2 = null;
			}
			
			if (mapTrainDataset != null) {
			    releaseDataset(mapTrainDataset);
				mapTrainDataset = null;
			}
			if (mapTestDataset != null) {
				releaseDataset(mapTestDataset);
				mapTestDataset = null;
			}
			
			if (matcher != null) {
				matcher.release();
				matcher = null;
			}
			if (linkSet != null) {
				linkSet.release();
				linkSet = null;
			}
			
			if (model != null) {
				model.release();
				model = null;
			}
		}		
	}

	private static IModel prepareModel() {
		
		//*
		ModelParams_2 params = new ModelParams_2(
				ModelParams_2.PARAM_WeightOfProb, 
				0,	// nNumOfLocIds unknown
				ModelParams_2.PARAM_Smooth_NumOfLocations,
				1, // dMaxDist default to 1
				ModelParams_2.PARAM_Smooth_TimeDiff);
			
		return new Model_2(params);
		//*/
		
		/*
		ModelParams_3 params = new ModelParams_3(
				ModelParams_3.PARAM_WeightOfProb, 
				0,	// nNumOfLocIds unknown
				ModelParams_3.PARAM_Smooth_NumOfLocations,
				1, // dMaxDist default to 1
				ModelParams_3.PARAM_Smooth_TimeDiff);
			
		return new Model_3(params);
		//*/

		/*
		ModelParams_4 params = new ModelParams_4(
				ModelParams_4.PARAM_WeightOfProb, 
				0,	// nNumOfLocIds unknown
				ModelParams_4.PARAM_Smooth_NumOfLocations,
				1, // dMaxDist default to 1
				ModelParams_4.PARAM_Smooth_TimeDiff,
				ModelParams_4.PARAM_NumOfTimeUnits);
			
		return new Model_4(params);
		//*/
	}

	private static int generateDatasetsForTestMode(
			Map<Integer, UserInfo> mapTestDataset_2,
			Map<Integer, UserInfo> mapTrainDataset_2,
			LinkSet linkSet_Answer_2,
			Map<Integer, UserInfo> mapTestDataset,
			Map<Integer, UserInfo> mapTrainDataset, LinkSet linkSet_Answer,
			int nNumTestUsersForTestMode,
			int nMaxNumCheckinItemsForEachTestUserForTestMode,
		    int nMaxNumCheckinItemsForEachTrainUserForTestMode) {
		
		int cTestUserIds = mapTestDataset.size();
		
		int nNumTestUsersForTestMode_2 = nNumTestUsersForTestMode;
		if (nNumTestUsersForTestMode_2 > cTestUserIds) {
			nNumTestUsersForTestMode_2 = cTestUserIds;
		}
		
		Object[] arrUserIds_test = mapTestDataset.keySet().toArray();
		
		Integer intUserId_test, intUserId_train;
		UserInfo userInfo_test, userInfo_train;
		
		Random random = new Random();
		List<CheckinItem> listCheckinItems;
		LocDict locDictForTestMode = new LocDict();
		for (int i = 0; i < nNumTestUsersForTestMode_2; i++) {
			
			intUserId_test = (Integer) arrUserIds_test[random.nextInt(cTestUserIds)];
			
			intUserId_train = linkSet_Answer.getLink(intUserId_test);
			userInfo_test = mapTestDataset.get(intUserId_test);
			userInfo_train = mapTrainDataset.get(intUserId_train);
			
			if ( (intUserId_train != null) && 
					(userInfo_test != null) && (userInfo_train != null) &&
					(userInfo_test.getNumOfCheckinItems() > 1) &&
					(userInfo_train.getNumOfCheckinItems() > 1) ) {
				
				listCheckinItems = pickCheckinItemsRandomly(
						userInfo_test.getCheckinItems(), nMaxNumCheckinItemsForEachTestUserForTestMode);
				buildLocDictForTestMode(locDictForTestMode, listCheckinItems);
				mapTestDataset_2.put(intUserId_test, new UserInfo(listCheckinItems));
				
				listCheckinItems = pickCheckinItemsRandomly(
						userInfo_train.getCheckinItems(), nMaxNumCheckinItemsForEachTrainUserForTestMode);
				buildLocDictForTestMode(locDictForTestMode, listCheckinItems);
				mapTrainDataset_2.put(intUserId_train, new UserInfo(listCheckinItems));
				
				linkSet_Answer_2.addLink(intUserId_test, intUserId_train);
			}
		}
		
		int cLocDictEntries = locDictForTestMode.getNumOfLocIds();
		locDictForTestMode.release();
		
		arrUserIds_test = null; // release memory
		
		return cLocDictEntries;
	}

	private static List<CheckinItem> pickCheckinItemsRandomly(
			List<CheckinItem> listCheckinItems, int nMaxNumCheckinItems) {
		
		List<CheckinItem> result = new ArrayList<CheckinItem>();
		
		int cItems = listCheckinItems.size();
		
		int nMaxNumCheckinItems_2 = nMaxNumCheckinItems;
		if (nMaxNumCheckinItems_2 > cItems) {
			nMaxNumCheckinItems_2 = cItems;
		}
		
		CheckinItem item;
		Random random = new Random();
		for (int i = 0; i < nMaxNumCheckinItems_2; i++) {
			
			item = listCheckinItems.get(random.nextInt(cItems));
			result.add(item.copy());
		}
		
		return result;
	}


	private static void buildLocDictForTestMode(LocDict locDict,
			List<CheckinItem> listCheckinItems) {
		
		if (listCheckinItems == null) {
			return;
		}
		
		for (CheckinItem item : listCheckinItems) {
			locDict.addLoc("" + item.m_nLocIndex);
		}
	}


	private static int analyzeResult(LinkSet linkSet_Result, LinkSet linkSet_Answer,
			Map<Integer, UserInfo> mapTestDataset,
			Map<Integer, UserInfo> mapTrainDataset,
			IModel model) {
		
		int cLinks_Result = linkSet_Result.getLinkCount();
		
		LinkSet linkSet_Error = linkSet_Result.calcDiffLinks_Reverse(linkSet_Answer);
		if (linkSet_Error == null) {
		    return cLinks_Result;
		}
		
		Map<Integer, Integer> mapLinks_Error = linkSet_Error.getLinks();
		
		Integer intUserId_Error_test, intUserId_Answer_train;
		UserInfo userInfo_test, userInfo_train;
		Similarity similarity_Error, similarity_Answer;
		
		// possible deprive ?
		boolean bDeprivePossible, bDepriveMissed, bIsInCandidateList;
		Integer intUserId_Answer_test;
		UserInfo userInfo_Deprive_test, userInfo_Deprive_train;
		Similarity similarity_Deprive;
		
		int nUserId_Error_test, nUserId_Error_train, nUserId_Answer_train, nUserId_Answer_test;
		
		for (Integer intUserId_Error_train : mapLinks_Error.keySet()) {
			
			intUserId_Error_test = mapLinks_Error.get(intUserId_Error_train);
			
			intUserId_Answer_train = linkSet_Answer.getLink(intUserId_Error_test);
			nUserId_Answer_train = intUserId_Answer_train.intValue();
			
			userInfo_test = mapTestDataset.get(intUserId_Error_test);
			userInfo_train = mapTrainDataset.get(intUserId_Answer_train);
			
			similarity_Error = userInfo_test.getCandidateSimilarityByUserId(intUserId_Error_train);
			similarity_Answer = userInfo_test.getCandidateSimilarityByUserId(intUserId_Answer_train);
			if (similarity_Answer == null) {
				bIsInCandidateList = false;
			    similarity_Answer = model.calcSimilarity(userInfo_test, userInfo_train);
			}
			else {
				bIsInCandidateList = true;
			}
			
			bDeprivePossible = false;
			bDepriveMissed = false;
			similarity_Deprive = null;
			intUserId_Answer_test = linkSet_Answer.getLinkSourceByTarget(intUserId_Error_train);
			if (intUserId_Answer_test == null) {
				nUserId_Answer_test = 0;
			}
			else {
				nUserId_Answer_test = intUserId_Answer_test.intValue();
				
				bDepriveMissed = linkSet_Result.isContainTargetNode(intUserId_Answer_test);
				
				userInfo_Deprive_test = mapTestDataset.get(intUserId_Answer_test);
				if (userInfo_Deprive_test != null) {
					
					similarity_Deprive = userInfo_Deprive_test.getCandidateSimilarityByUserId(
							intUserId_Error_train);
					if (similarity_Deprive == null) {
						
						userInfo_Deprive_train = mapTrainDataset.get(intUserId_Error_train);
						if (userInfo_Deprive_train != null) {
							similarity_Deprive = model.calcSimilarity(userInfo_Deprive_test,
									userInfo_Deprive_train);
							bDeprivePossible = isDeprivePossible(similarity_Error, similarity_Deprive);
						}
					}
				}
			}
			
			nUserId_Error_test = intUserId_Error_test.intValue();
			nUserId_Error_train = intUserId_Error_train.intValue();
			Logger.showDbgMsg("ErrLink--" + 
			    constructErrorLinkReport(nUserId_Error_test, nUserId_Error_train, nUserId_Answer_train,
			    		similarity_Error, similarity_Answer,
			    		bDepriveMissed, bDeprivePossible, bIsInCandidateList,
			    		nUserId_Answer_test, similarity_Deprive) 
			    + "--");
		}
		
		return cLinks_Result - mapLinks_Error.size();
	}


	private static boolean isDeprivePossible(Similarity similarity_Error,
			Similarity similarity_Deprive) {
		
		return (similarity_Error.m_fSimilarity < similarity_Deprive.m_fSimilarity);
	}


	private static String constructErrorLinkReport(
			int nUserId_Error_test, int nUserId_Error_train, int nUserId_Answer_train,
			Similarity similarity_Error, Similarity similarity_Answer,
			boolean bDepriveMissed, boolean bDeprivePossible, boolean bIsInCandidateList,
			int nUserId_Answer_test, Similarity similarity_Deprive) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("Link<" + nUserId_Error_test + " --> " + nUserId_Error_train + "[" + nUserId_Answer_train + "]>\r\n");
		sb.append("    Sim<" + ( (similarity_Error == null) ? "NULL" : similarity_Error.toString() ) + "[" +
				( (similarity_Answer == null) ? "NULL" : similarity_Answer.toString() ) + "]>\r\n");
		sb.append("    Dep<Missed=" + (bDepriveMissed ? "T" : "F") + 
				", Possible=" + (bDeprivePossible ? "T" : "F") +
				", Candidate=" + (bIsInCandidateList ? "T" : "F") +
				", " +  nUserId_Answer_test + " --> " + nUserId_Error_train + "\r\n" +
				"        Sim=#" + ( (similarity_Deprive == null) ? "NULL" : similarity_Deprive.toString() ) + "#>");
		
		return sb.toString();
	}
	
	private static void releaseDataset(Map<Integer, UserInfo> mapDataset) {
		
		if (mapDataset != null) {
			
			Collection<UserInfo> userInfos = mapDataset.values();
			if (userInfos != null) {
				for (UserInfo userInfo : userInfos) {
					userInfo.release();
				}
			}
			
			mapDataset.clear();
		}	
	}
}
