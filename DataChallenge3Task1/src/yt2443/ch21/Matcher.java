package yt2443.ch21;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/*
big dataset, random, degree >= 2 exp j
[INFO] Number of links found = [49312], correctLinks = [710], precision = [1.439811810512654%], F1 = [0.014298372804897697]

small dataset, random1
[INFO] Number of links found = [6933], correctLinks = [91], precision = [1.3125631039953845%], F1 = [0.013062513457259744]

small dataset, random2, degree >= 2 exp j
[INFO] Number of links found = [6893], correctLinks = [76], precision = [1.1025678224285507%], F1 = [0.010940761534585763]
//*/

public class Matcher {

	private Graph m_graph_1, m_graph_2;

	private Map<Integer, NodeMarkMap> m_mapCandidateLinks;
	private Map<Integer, List<Long>> m_mapMark2NodePairList;
	private int m_nCurMaxMark;
	
	private byte[] m_arrNodeLinkedFlags_1;
	private byte[] m_arrNodeLinkedFlags_2;
	
	public Matcher(Graph graph_1, Graph graph_2) {
		
		m_graph_1 = graph_1;
		m_graph_2 = graph_2;
		
		m_mapCandidateLinks = null;
		m_mapMark2NodePairList = null;
		m_nCurMaxMark = 0;
		
		m_arrNodeLinkedFlags_1 = null;
		m_arrNodeLinkedFlags_2 = null;
	}

	public void doMatch(LinkSet linkSet, int nMarkThreshold,
			boolean bRandomFlag_1, boolean bRandomFlag_2) {
		
		if ( (m_graph_1 == null) || (m_graph_2 == null) ) {
			return;
		}
		
		initBeforeMatch();
		
		if (bRandomFlag_2) {
			doMatch_Random_2(linkSet, nMarkThreshold);
			return;
		}
		
		int cTotalNodes_1 = m_graph_1.getMaxNodeNo();
		float fProgress;
		int cProgressReportTimes = 0;
		
		Map<Integer, Integer> mapUnusedLinks = null;
		Map<Integer, Integer> mapLinks;
		boolean bInitPhaseFlag;
		List<Long> listNodePairs;
		while (true) {
			
			mapLinks = mapUnusedLinks;
			if (mapLinks == null) {
				mapLinks = linkSet.getLinks();
				bInitPhaseFlag = true;
			}
			else {
				bInitPhaseFlag = false;
			}
			
			if (mapLinks == null) {
				break;
			}
			
			Set<Integer> setNodes_1 = mapLinks.keySet();
			Integer intNode_2;
			if (bInitPhaseFlag) {
				for (Integer intNode_1 : setNodes_1) {
					m_arrNodeLinkedFlags_1[intNode_1.intValue()] = 1;
					
					intNode_2 = mapLinks.get(intNode_1);
					m_arrNodeLinkedFlags_2[intNode_2.intValue()] = 1;
				}
			}
			
			/*
			int cTotalLinks = setNodes_1.size();
			int cLinksSoFar = 0;
			//*/
			for (Integer intNode_1 : setNodes_1) {
				intNode_2 = mapLinks.get(intNode_1);
				addMarkByExistLink(intNode_1, intNode_2, bInitPhaseFlag);
				
				/*
				cLinksSoFar++;
				Logger.showDbgMsg("Progress=[" + ( (cLinksSoFar * (float) 100.0) / cTotalLinks) + "%]");
				//*/
			}
			
			if (mapUnusedLinks != null) {
				mapUnusedLinks.clear(); // because all unused links have been used in the previous for loop
			}
			
			if (m_nCurMaxMark >= nMarkThreshold) {
				
				listNodePairs = m_mapMark2NodePairList.get(new Integer(m_nCurMaxMark));
				
				int nNodePairIndex;
				if (bRandomFlag_1) {
					nNodePairIndex = locateNodePair_Random_1(listNodePairs);
				}
				else {
					nNodePairIndex = locateNodePairWithMaxDegree(listNodePairs);
				}
				Long longNodePair = listNodePairs.remove(nNodePairIndex);
					
				long lNodePair = longNodePair.longValue();
				int nNodeNo_1 = getNodeNo_1(lNodePair);
				int nNodeNo_2 = getNodeNo_2(lNodePair);
				removeUselessCandidateLinks(nNodeNo_1, nNodeNo_2);
				adjustMarkInfo();
				
				linkSet.addLink(nNodeNo_1, nNodeNo_2);

				fProgress = ( linkSet.getLinkCount() * (float) 100.0 ) / cTotalNodes_1;
				if (fProgress >= cProgressReportTimes * 2) {
				    Logger.showInfo("Progress = [" + fProgress + "%]");
					cProgressReportTimes++;
				}
				
				if (mapUnusedLinks == null) {
					mapUnusedLinks = new HashMap<Integer, Integer>();
				}
				mapUnusedLinks.put(new Integer(nNodeNo_1), new Integer(nNodeNo_2));
				
				m_arrNodeLinkedFlags_1[nNodeNo_1] = 1;
				m_arrNodeLinkedFlags_2[nNodeNo_2] = 1;
			}
			else {
				break; // finished
			}
		}
	}

	private void doMatch_Random_2(LinkSet linkSet, int nMarkThreshold) {
		
		Map<Integer, Integer> mapLinks = linkSet.getLinks();
		Set<Integer> setNodes_1 = mapLinks.keySet();
		Integer intNode_2;
		for (Integer intNode_1 : setNodes_1) {
			m_arrNodeLinkedFlags_1[intNode_1.intValue()] = 1;
			
			intNode_2 = mapLinks.get(intNode_1);
			m_arrNodeLinkedFlags_2[intNode_2.intValue()] = 1;
		}
		
		for (Integer intNode_1 : setNodes_1) {
			intNode_2 = mapLinks.get(intNode_1);
			addMarkByExistLink(intNode_1, intNode_2, true); // bInitPhaseFlag
		}
		
		
		int nMaxDegree_1 = m_graph_1.getMaxDegree();
		int nMaxDegree_2 = m_graph_2.getMaxDegree();
		
		if (nMaxDegree_2 < nMaxDegree_1) {
			nMaxDegree_2 = nMaxDegree_1;
		}
		int cPhases = (int) ( Math.log(nMaxDegree_2) / Math.log(2) );
		
		int cTotalNodes_1 = m_graph_1.getMaxNodeNo();
		float fProgress;
		int cProgressReportTimes = 0;
		
		Map<Integer, Integer> mapUnusedLinks = null;
		List<Long> listNodePairs;
		while (true) {
			
			if (mapUnusedLinks != null) {
				setNodes_1 = mapUnusedLinks.keySet();
				
				for (Integer intNode_1 : setNodes_1) {
					intNode_2 = mapLinks.get(intNode_1);
					addMarkByExistLink(intNode_1, intNode_2, false); // bInitPhaseFlag
				}
			
				mapUnusedLinks.clear(); // because all unused links have been used in the previous for loop
			}
			
			if (m_nCurMaxMark < nMarkThreshold) {
				break; // finished
			}
			
			listNodePairs = m_mapMark2NodePairList.get(new Integer(m_nCurMaxMark));
			
			for (int nPhase = cPhases; nPhase > 0; nPhase--) {
				
				int nNodePairIndex = locateNodePair_Random_2(listNodePairs, nPhase);
				if (nNodePairIndex >= 0) {
				
					Long longNodePair = listNodePairs.remove(nNodePairIndex);
					
					long lNodePair = longNodePair.longValue();
					int nNodeNo_1 = getNodeNo_1(lNodePair);
					int nNodeNo_2 = getNodeNo_2(lNodePair);
					removeUselessCandidateLinks(nNodeNo_1, nNodeNo_2);
					adjustMarkInfo();
					
					linkSet.addLink(nNodeNo_1, nNodeNo_2);
	
					fProgress = ( linkSet.getLinkCount() * (float) 100.0 ) / cTotalNodes_1;
					if (fProgress >= cProgressReportTimes * 2) {
						cProgressReportTimes++;
					    Logger.showInfo("Progress = [" + fProgress + "%]");
					}
					
					if (mapUnusedLinks == null) {
						mapUnusedLinks = new HashMap<Integer, Integer>();
					}
					mapUnusedLinks.put(new Integer(nNodeNo_1), new Integer(nNodeNo_2));
					
					m_arrNodeLinkedFlags_1[nNodeNo_1] = 1;
					m_arrNodeLinkedFlags_2[nNodeNo_2] = 1;
					
					break;
				}
			}
		}
	}

	private int locateNodePair_Random_1(List<Long> listNodePairs) {
		
		Random random = new Random();
		int nNodePairIndex = random.nextInt(listNodePairs.size());
		random = null;
		
		return nNodePairIndex;
	}

	private int locateNodePair_Random_2(List<Long> listNodePairs, int nPhase) {
		
		if ( (listNodePairs == null) || listNodePairs.isEmpty() ) {
			
			return -1;
		}
		
		int nNodePairIndex = -1;
		
		int nMinDegree = (int) Math.pow(2, nPhase);
		
		List<Integer> listNodePairIndexesInPhase = null;
		
		long lNodePair;
		int nNodeNo_1, nNodeNo_2;
		int cNodePairs = listNodePairs.size();
		for (int nIndex = 0; nIndex < cNodePairs; nIndex++) {
			
			lNodePair = listNodePairs.get(nIndex);
			nNodeNo_1 = getNodeNo_1(lNodePair);
			nNodeNo_2 = getNodeNo_2(lNodePair);
			
			if ( (m_graph_1.getNodeDegree(nNodeNo_1) >= nMinDegree) && 
					(m_graph_2.getNodeDegree(nNodeNo_2) >= nMinDegree) ) {
				
				if (listNodePairIndexesInPhase == null) {
					listNodePairIndexesInPhase = new ArrayList<Integer>();
				}
				listNodePairIndexesInPhase.add(new Integer(nIndex));
			}
		}
		
		if (listNodePairIndexesInPhase != null) {
			
			int cNodePairsInPhase = listNodePairIndexesInPhase.size();
			Random random = new Random();
			int nRandom = random.nextInt(cNodePairsInPhase);
			nNodePairIndex = listNodePairIndexesInPhase.get(nRandom);
			random = null;
			
			listNodePairIndexesInPhase.clear();
			listNodePairIndexesInPhase = null;
		}
		
		return nNodePairIndex;
	}

	private void initBeforeMatch() {
		
		m_mapCandidateLinks = new HashMap<Integer, NodeMarkMap>();
		m_mapMark2NodePairList = new HashMap<Integer, List<Long>>();
		m_nCurMaxMark = 0;
		
		int nMaxNodeNo_1 = m_graph_1.getMaxNodeNo();
		int nMaxNodeNo_2 = m_graph_2.getMaxNodeNo();
		m_arrNodeLinkedFlags_1 = new byte[nMaxNodeNo_1 + 1];
		m_arrNodeLinkedFlags_2 = new byte[nMaxNodeNo_2 + 1];
	}

	private void addMarkByExistLink(Integer intLinkNodeNo_1, Integer intLinkNodeNo_2,
			boolean bInitPhaseFlag) {
		
		List<Integer> listNeighbour_1 = m_graph_1.getNeighbours(intLinkNodeNo_1);
		List<Integer> listNeighbour_2 = m_graph_2.getNeighbours(intLinkNodeNo_2);
		if ( (listNeighbour_1 == null) || (listNeighbour_2 == null) ) {
			return;
		}
		
		/*
		int nLinkNodeNo_1 = intLinkNodeNo_1.intValue();
		int nLinkNodeNo_2 = intLinkNodeNo_2.intValue();
		//*/
		
		int nNodeNo_1, nNodeNo_2;
		/*
		List<Integer> listNeighbour_11, listNeighbour_22;
		int nNodeNo_11, nNodeNo_22;
		//*/
		for (Integer intNodeNo_1 : listNeighbour_1) {
			nNodeNo_1 = intNodeNo_1.intValue();
			if (m_arrNodeLinkedFlags_1[nNodeNo_1] == 1) {
				continue; // this node has been linked
			}
			
			/*
			listNeighbour_11 = m_graph_1.getNeighbours(intNodeNo_1);
			//*/
			
			for (Integer intNodeNo_2 : listNeighbour_2) {
				nNodeNo_2 = intNodeNo_2.intValue();
				if (m_arrNodeLinkedFlags_2[nNodeNo_2] == 1) {
					continue; // this node has been linked
				}

				addMark(nNodeNo_1, nNodeNo_2, 1);
				
				/*
				if (bInitPhaseFlag) {
					
				    listNeighbour_22 = m_graph_2.getNeighbours(intNodeNo_2);
					if ( (listNeighbour_11 != null) && (listNeighbour_22 != null) ) {
						
						for (Integer intNodeNo_11 : listNeighbour_11) {
							nNodeNo_11 = intNodeNo_11.intValue();
							if ( (nNodeNo_11 == nLinkNodeNo_1) || (m_arrNodeLinkedFlags_1[nNodeNo_11] == 1) ) {
								continue; // this node has been linked
							}
							
							for (Integer intNodeNo_22 : listNeighbour_22) {
								nNodeNo_22 = intNodeNo_22.intValue();
								if ( (nNodeNo_22 == nLinkNodeNo_2) || (m_arrNodeLinkedFlags_2[nNodeNo_22] == 1) ) {
									continue; // this node has been linked
								}
	
								addMark(nNodeNo_11, nNodeNo_22, 1);
							}
						}
					}
				}
				//*/
			}
		}
	}

	private void addMark(int nNodeNo_1, int nNodeNo_2, int nMarkInc) {
		
		Integer intNodeNo_1 = new Integer(nNodeNo_1);
		
		int nMark_Old = 0;
		NodeMarkMap map = m_mapCandidateLinks.get(intNodeNo_1);
		if (map == null) {
			map = new NodeMarkMap();
			m_mapCandidateLinks.put(intNodeNo_1, map);
		}
		else {
			nMark_Old = map.getMark(nNodeNo_2);
		}
		
		int nMark_New = nMark_Old + nMarkInc;
		map.setNodeAndMark(nNodeNo_2, nMark_New);

		
		
		Long longNodePair = new Long(createNodePair(nNodeNo_1, nNodeNo_2));
		
		List<Long> listNodePairs;
		if (nMark_Old > 0) {
			listNodePairs = m_mapMark2NodePairList.get(new Integer(nMark_Old));
			if (listNodePairs != null) {
				listNodePairs.remove(longNodePair);
			}
		}
		
		Integer intMark_New = new Integer(nMark_New);
		listNodePairs = m_mapMark2NodePairList.get(intMark_New);
		if (listNodePairs == null) {
			listNodePairs = new ArrayList<Long>();
			m_mapMark2NodePairList.put(intMark_New, listNodePairs);
		}
		listNodePairs.add(longNodePair);
		
		if (nMark_New > m_nCurMaxMark) {
			m_nCurMaxMark = nMark_New;
		}
	}

	private int locateNodePairWithMaxDegree(List<Long> listNodePairs) {
		
		int nIndex_WithMaxDegree = 0;
		
		long lNodePair;
		int nNodeNo_1, nNodeNo_2, nDegree;
		int nIndex = 0;
		int nMaxDegree = 0;
		for (Long longNodePair : listNodePairs) {
			
			lNodePair = longNodePair.longValue();
			nNodeNo_1 = getNodeNo_1(lNodePair);
			nNodeNo_2 = getNodeNo_2(lNodePair);
			
			nDegree = m_graph_1.getNodeDegree(nNodeNo_1) + m_graph_2.getNodeDegree(nNodeNo_2);
			if (nDegree > nMaxDegree) {
				nMaxDegree = nDegree;
				nIndex_WithMaxDegree = nIndex;
			}
			
			nIndex++;
		}
		
		return nIndex_WithMaxDegree;
	}

	private void removeUselessCandidateLinks(int nNodeNo_1, int nNodeNo_2) {
		
		Integer intNodeNo_1 = new Integer(nNodeNo_1);	
		NodeMarkMap map = m_mapCandidateLinks.get(intNodeNo_1);
		if (map != null) {
			map.release();
			m_mapCandidateLinks.remove(intNodeNo_1);
		}
		
		Collection<Integer> keys = m_mapCandidateLinks.keySet();
		if (keys != null) {
			NodeMarkMap map2;
			for (Integer key : keys) {
				map2 = m_mapCandidateLinks.get(key);
				if (map2 != null) {
				    map2.removeNode(nNodeNo_2);
				}
			}
		}
		
		List<Long> listNodePairs;
		long lNodePair;
		List<Long> listNodePairs_ToDelete = new ArrayList<Long>();
		for (int nMark = m_nCurMaxMark; nMark > 0; nMark--) {
			listNodePairs = m_mapMark2NodePairList.get(new Integer(nMark));
			if (listNodePairs == null) {
				continue;
			}
			
			for (Long longNodePair : listNodePairs) {
				lNodePair = longNodePair.longValue();
				if ( (getNodeNo_1(lNodePair) == nNodeNo_1) ||
						(getNodeNo_2(lNodePair) == nNodeNo_2) ) {
					listNodePairs_ToDelete.add(longNodePair);
				}
			}
			
			for (Long longNodePair : listNodePairs_ToDelete) {
				listNodePairs.remove(longNodePair);
			}
			
			listNodePairs_ToDelete.clear();
		}
		
		listNodePairs_ToDelete = null;
	}

	private void adjustMarkInfo() {
		
		int nMaxMark = m_nCurMaxMark;
		
		List<Long> listNodePairs = m_mapMark2NodePairList.get(new Integer(nMaxMark));
		if ( (listNodePairs == null) || listNodePairs.isEmpty()) {
			
			if (nMaxMark == 1) {
				m_nCurMaxMark = 0;
			}
			else {
				
				while (true) {
					nMaxMark = nMaxMark - 1;
					if (nMaxMark == 0) {
						m_nCurMaxMark = 0;
						break;
					}
					
					listNodePairs = m_mapMark2NodePairList.get(new Integer(nMaxMark));
					if ( (listNodePairs == null) || listNodePairs.isEmpty() ) {
						continue;
					}
					
					m_nCurMaxMark = nMaxMark;
					break;
				}
			}
		}
	}

	private long createNodePair(int nNodeNo_1, int nNodeNo_2) {
		
		long lNodeNo_1 = nNodeNo_1;
		long lNodeNo_2 = nNodeNo_2;
		return ( (lNodeNo_1 << 32) | lNodeNo_2 );
	}

	private int getNodeNo_1(long lNodePair) {
		
		return (int) (lNodePair >> 32);
	}

	private int getNodeNo_2(long lNodePair) {
		
		return (int) lNodePair;
	}

	public void release() {
		
		m_graph_1 = null;
		m_graph_2 = null;
		
		if (m_mapCandidateLinks != null) {
			Collection<NodeMarkMap> maps = m_mapCandidateLinks.values();
			if (maps != null) {
			    for (NodeMarkMap map : maps) {
			    	map.release();
			    }
			}
			
			m_mapCandidateLinks.clear();
			m_mapCandidateLinks = null;
		}
		
		if (m_mapMark2NodePairList != null) {
			Collection<List<Long>> lists = m_mapMark2NodePairList.values();
			if (lists != null) {
			    for (List<Long> list : lists) {
			    	list.clear();
			    }
			}
			
			m_mapMark2NodePairList.clear();
			m_mapMark2NodePairList = null;
		}
		
		m_nCurMaxMark = 0;
		
		m_arrNodeLinkedFlags_1 = null;
		m_arrNodeLinkedFlags_2 = null;
	}

}
