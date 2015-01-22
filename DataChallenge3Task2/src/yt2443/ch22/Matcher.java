package yt2443.ch22;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Matcher {

	private Map<Integer, UserInfo> m_mapTrainDataset;
	private Map<Integer, UserInfo> m_mapTestDataset;
	
	private Set<Integer> m_setTestUserIds_LinkDeprived;
	private Set<Integer> m_setTestUserIds_NoLinkFoundSoFar;
	
	public Matcher(Map<Integer, UserInfo> mapTrainDataset,
			Map<Integer, UserInfo> mapTestDataset) {
		
		m_mapTrainDataset = mapTrainDataset;
		m_mapTestDataset = mapTestDataset;
		
		m_setTestUserIds_LinkDeprived = new HashSet<Integer>();
		m_setTestUserIds_NoLinkFoundSoFar = new HashSet<Integer>();
	}

	public LinkSet doMatch(Params params,
			int nMaxNumOfCandidateLinks, int nMaxNumOfCandidateLinks_Phase2,
			int nMaxNumOfTestUsers,
			boolean bNoDepriveFlag) {
		
		if ( (m_mapTrainDataset == null) || m_mapTrainDataset.isEmpty() ||
				(m_mapTestDataset == null) || m_mapTestDataset.isEmpty() ) {
			
		    return null;
		}
		
		// init
		m_setTestUserIds_LinkDeprived.clear();
		m_setTestUserIds_NoLinkFoundSoFar.clear();
		
		/*
		long lStartTime = System.currentTimeMillis();
		params.m_dMaxDist = calcMaxDist();
		long lTime = System.currentTimeMillis() - lStartTime;
		Logger.showInfo("Calc max distance completed, time = [" + lTime + "], maxDist = [" + params.m_dMaxDist + "]");
		//*/
		
		// prepare result links
		LinkSet result = new LinkSet();
		
		///////////////////////////////////////////////////////////////////////
		// phase 1
		
		int cTotalUsers_test = m_mapTestDataset.size();
		int cProcessedUsersSoFar_test = 0;
		int cProgressReportTimes = 0;
		float fProgress;
		
		UserInfo userInfo_test;
		for (Integer intUserId_test : m_mapTestDataset.keySet()) {
			
			userInfo_test = m_mapTestDataset.get(intUserId_test);
			if (userInfo_test != null) {
				userInfo_test.setMaxNumOfCandidateLinks(nMaxNumOfCandidateLinks);
				matchTheUser(result, intUserId_test, userInfo_test, params,
						null, // setTrainUserIds_Linked
						m_setTestUserIds_NoLinkFoundSoFar,
						bNoDepriveFlag);
			}
			
			cProcessedUsersSoFar_test++;
			
			if ( (nMaxNumOfTestUsers > 0) && 
					(cProcessedUsersSoFar_test >= nMaxNumOfTestUsers) ) {
				break;
			}
			
			fProgress = (cProcessedUsersSoFar_test * (float) 100.0) / cTotalUsers_test;
			if (fProgress >= cProgressReportTimes * 2) {
			    Logger.showInfo("Phase-1 progress = [" + fProgress + "%]");
			    cProgressReportTimes++;
			}
		}

		
		///////////////////////////////////////////////////////////////////////
		// phase 2
		
		int cLinks_before;
		int cLinks_now = result.getLinkCount();
		while (cLinks_now < cTotalUsers_test) {
			
			cLinks_before = cLinks_now;
			
			//*
			if ( ! bNoDepriveFlag ) {
			    matchForUsers_LinkDeprived(result);
		    }
			//*/
			
			//*
			matchForUsers_NoLinkFoundSoFar(result, nMaxNumOfCandidateLinks_Phase2,
					params,
					bNoDepriveFlag);
			//*/
			
			if (m_setTestUserIds_LinkDeprived.isEmpty() &&
					m_setTestUserIds_NoLinkFoundSoFar.isEmpty() ) {
				break;
			}
			
			cLinks_now = result.getLinkCount();
			if (cLinks_now <= cLinks_before) {
				break;
			}
		}
		//*/
		
		return result;
	}

	/******************************************************************************
	private double calcMaxDist() {
		
		double dMaxDist = 0;
		
		double dDist;
		
		int cTotalUsers_test = m_mapTestDataset.size();
		int cProcessedUsersSoFar_test = 0;
		int cProgressReportTimes = 0;
		float fProgress;
		
		for (UserInfo userInfo_test : m_mapTestDataset.values()) {
			
			for (UserInfo userInfo_train : m_mapTrainDataset.values()) {
				dDist = userInfo_test.calcMaxDist(userInfo_train);
				if (dMaxDist < dDist) {
					dMaxDist = dDist;
				}
			}
			
			cProcessedUsersSoFar_test++;
			fProgress = (cProcessedUsersSoFar_test * (float) 100.0) / cTotalUsers_test;
			if (fProgress >= cProgressReportTimes * 2) {
			    Logger.showDbgMsg("Calc max distance progress = [" + fProgress + "%]");
			    cProgressReportTimes++;
			}
		}
		
		return dMaxDist;
	}
	******************************************************************************/

	private void matchTheUser(LinkSet result,
			Integer intUserId_test, UserInfo userInfo_test, Params params,
			Set<Integer> setTrainUserIds_Exclude,
			Set<Integer> setTestUserIds_NoLinkFoundSoFar,
			boolean bNoDepriveFlag) {
		
		Similarity similarity;		
		UserInfo userInfo_train;
		for (Integer intUserId_train : m_mapTrainDataset.keySet()) {
			
			userInfo_train = m_mapTrainDataset.get(intUserId_train);
			if (userInfo_train == null) {
				continue;
			}
			
			/*
			if ( (setTrainUserIds_Exclude != null) && 
					setTrainUserIds_Exclude.contains(intUserId_train) ) {
				continue;
			}
			//*/
			
			similarity = SimAnalyzer.calcSimilarity(userInfo_test, userInfo_train, params);
			userInfo_test.addCandidateLinkWithSimilarity(
					intUserId_train.intValue(), similarity);
		}
		
		Integer intChosenUserId_train = decideLink(userInfo_test, result,
				m_setTestUserIds_LinkDeprived,
				bNoDepriveFlag);
		if (intChosenUserId_train == null) {
			setTestUserIds_NoLinkFoundSoFar.add(intUserId_test);
		}
		else {
			result.addLink(intChosenUserId_train, intUserId_test);
			Logger.showInfo("Add link [" + intUserId_test.intValue() + " --> " + intChosenUserId_train.intValue() + "]");
		}
	}

	private void matchForUsers_LinkDeprived(LinkSet result) {
		
		Set<Integer> setTestUserIds_LinkDeprived_New = new HashSet<Integer>();
		
		int cTotalUsers_test = m_setTestUserIds_LinkDeprived.size();
		int cProcessedUsersSoFar_test = 0;
		int cProgressReportTimes = 0;
		float fProgress;
		
		UserInfo userInfo_test;
		for (Integer intUserId_test : m_setTestUserIds_LinkDeprived) {
			
			userInfo_test = m_mapTestDataset.get(intUserId_test);
			
			Integer intChosenUserId_train = decideLink(userInfo_test, result,
					setTestUserIds_LinkDeprived_New,
					false); // bNoDepriveFlag
			if (intChosenUserId_train == null) {
				m_setTestUserIds_NoLinkFoundSoFar.add(intUserId_test);
			}
			else {
				result.addLink(intChosenUserId_train, intUserId_test);
				Logger.showInfo("Add link [" + intUserId_test.intValue() + " --> " + intChosenUserId_train.intValue() + "]");
			}
			
			cProcessedUsersSoFar_test++;
			fProgress = (cProcessedUsersSoFar_test * (float) 100.0) / cTotalUsers_test;
			if (fProgress >= cProgressReportTimes * 20) {
			    Logger.showInfo("Phase-2.1 progress = [" + fProgress + "%]");
			    cProgressReportTimes++;
			}
		}
		
		m_setTestUserIds_LinkDeprived.clear();
		m_setTestUserIds_LinkDeprived = setTestUserIds_LinkDeprived_New;
	}

	private void matchForUsers_NoLinkFoundSoFar(LinkSet result,
			int nMaxNumOfCandidateLinks_Phase2, Params params,
			boolean bNoDepriveFlag) {
		
		Set<Integer> setTestUserIds_NoLinkFoundSoFar_New = new HashSet<Integer>();
		
		Set<Integer> setTrainUserIds_Linked = new HashSet<Integer>();
		Map<Integer, Integer> mapLinks = result.getLinks();
		setTrainUserIds_Linked.addAll(mapLinks.keySet());
		
		int cTotalUsers_test = m_setTestUserIds_NoLinkFoundSoFar.size();
		int cProcessedUsersSoFar_test = 0;
		int cProgressReportTimes = 0;
		float fProgress;
		
		UserInfo userInfo_test;
		for (Integer intUserId_test : m_setTestUserIds_NoLinkFoundSoFar) {
			
			userInfo_test = m_mapTestDataset.get(intUserId_test);
			if (userInfo_test != null) {
				
				/*
				userInfo_test.clearCandidateLinks();
				//*/
				//*
				userInfo_test.resetMaxNumOfCandidateLinks(nMaxNumOfCandidateLinks_Phase2);
				//*/
				
				matchTheUser(result, intUserId_test, userInfo_test, params,
						setTrainUserIds_Linked,
						setTestUserIds_NoLinkFoundSoFar_New,
						bNoDepriveFlag);
			}
			
			cProcessedUsersSoFar_test++;
			fProgress = (cProcessedUsersSoFar_test * (float) 100.0) / cTotalUsers_test;
			if (fProgress >= cProgressReportTimes * 20) {
			    Logger.showInfo("Phase-2.2 progress = [" + fProgress + "%]");
			    cProgressReportTimes++;
			}
		}
		
		m_setTestUserIds_NoLinkFoundSoFar.clear();
		m_setTestUserIds_NoLinkFoundSoFar = setTestUserIds_NoLinkFoundSoFar_New;
	}

	private Integer decideLink(UserInfo userInfo_test, LinkSet result,
			Set<Integer> setTestUserIds_LinkDeprived,
			boolean bNoDepriveFlag) {
		
		Integer intResult = null;
		
		Integer intUserIdChosen_train;	
		UserIdWithSimilarity uiws, uiwsLinked;
		Integer intUserIdLinked_test = null;
		UserInfo userInfoLinked_test;
		
		int cCandidateLinks = userInfo_test.getNumOfCandidateLink();
		for (int i = 0; i < cCandidateLinks; i++) {
			uiws = userInfo_test.getCandidateLinkByOrder(i);
			if (uiws.isDeprived()) {
				continue;
			}
			
			intUserIdChosen_train = new Integer(uiws.m_nUserId);
			intUserIdLinked_test = result.getLink(intUserIdChosen_train);
			if (intUserIdLinked_test == null) {
				intResult = intUserIdChosen_train;
				break;
			}
			
			if (bNoDepriveFlag) {
				continue;
			}
			
			userInfoLinked_test = m_mapTestDataset.get(intUserIdLinked_test);
			uiwsLinked = userInfoLinked_test.getCandidateLinkByUserId(uiws.m_nUserId);
			if (uiwsLinked.m_fSimilarity >= uiws.m_fSimilarity) {
				uiws.setDeprivedFlag(true);
				continue;
			}
			
			// split the existing link !!!
			setTestUserIds_LinkDeprived.add(intUserIdLinked_test);
			uiwsLinked.setDeprivedFlag(true);
			result.removeLink(intUserIdChosen_train);
			Logger.showInfo("Deprive link [" + intUserIdLinked_test.intValue() +
					" --> " + intUserIdChosen_train.intValue() +
					"], similarity=[<" + uiwsLinked.m_fSimilarity +
					", " + uiwsLinked.m_fSimilarity_1 + ", " + uiwsLinked.m_fSimilarity_2 + ">" +
					" Less Than <" + uiws.m_fSimilarity + 
					", " + uiws.m_fSimilarity_1 + ", " + uiws.m_fSimilarity_2 + ">" +
					"]");
			
			intResult = intUserIdChosen_train;
			break;
		}
		
		return intResult;
	}

	public void release() {
		
		m_setTestUserIds_LinkDeprived.clear();
		m_setTestUserIds_LinkDeprived = null;
		
		m_setTestUserIds_NoLinkFoundSoFar.clear();
		m_setTestUserIds_NoLinkFoundSoFar = null;
		
		
		releaseDataset(m_mapTrainDataset);
		m_mapTrainDataset = null;
		
		releaseDataset(m_mapTestDataset);
		m_mapTestDataset = null;
	}

	private void releaseDataset(Map<Integer, UserInfo> mapDataset) {
		
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
