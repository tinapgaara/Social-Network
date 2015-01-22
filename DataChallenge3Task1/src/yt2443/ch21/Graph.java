package yt2443.ch21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Graph extends FileAccessor {

	private static final String STRING_FieldDelimiter = " ";
	private static final int INCREMENT_NodeNum = 10000;
	
	private int m_nMaxNodeNo;
	private Map<Integer, List<Integer>> m_mapEdges;
	private int[] m_arrNodeDegrees;
	
	public Graph(int nMaxNodeNo) {
		
		m_nMaxNodeNo = nMaxNodeNo;
		m_arrNodeDegrees = new int[m_nMaxNodeNo + 1];
		m_mapEdges = new HashMap<Integer, List<Integer>>();
	}

	public List<Integer> getNeighbours(Integer intNodeNo) {
		if (m_mapEdges == null) {
		    return null;
		}
		
		return m_mapEdges.get(intNodeNo);
	}

	/*
	public List<Integer> getNeighbours_2(Integer intNodeNo) {
		if (m_mapEdges == null) {
		    return null;
		}
		
		List<Integer> listNeighbours = m_mapEdges.get(intNodeNo);
		if (listNeighbours == null) {
		    return null;
		}
		
		List<Integer> listNeighbours_2 = new ArrayList<Integer>();
		for (Integer intNeighbour : listNeighbours) {
			listNeighbours_2.add(intNeighbour);
			
			List<Integer> list = getNeighbours(intNeighbour);
			if (list != null) {
				listNeighbours_2.addAll(list);
			}
		}
		
		return listNeighbours_2;
	}
	//*/

	public int getMaxNodeNo() {
		return m_arrNodeDegrees.length - 1;
	}
	
	public int getNodeDegree(int nNodeNo) {
		return m_arrNodeDegrees[nNodeNo];
	}
	
	public int getMaxDegree() {
		
		int nMaxDegree = 0;
		
		for (int i = 0; i < m_arrNodeDegrees.length; i++) {
			if (nMaxDegree < m_arrNodeDegrees[i]) {
				nMaxDegree = m_arrNodeDegrees[i];
			}
		}
		
		return nMaxDegree;
	}

	@Override
	protected void parseLine(String strLine)
	        throws SnsException {
		
    	if ( (strLine == null) || strLine.isEmpty() ) {
    		return;
    	}
    	
		SnsException exception = null;
		
		StringTokenizer tokenizer = null;
		String strNode1 = null, strNode2 = null;
		try {
			
			tokenizer = new StringTokenizer(strLine, STRING_FieldDelimiter);

			String strField;
			int nFieldOrderNo = 1;
	        while (tokenizer.hasMoreTokens()) {
	        	
	        	strField = tokenizer.nextToken();
	        	
	        	if (nFieldOrderNo == 1) {
	        		strNode1 = strField;
	        	}
	        	else if (nFieldOrderNo == 2) {
	        		strNode2 = strField;
	        		addEdge(Integer.parseInt(strNode1),
			        		Integer.parseInt(strNode2));
	        		break;
	        	}
	        	nFieldOrderNo++;
	        }
		}
		
		catch (NumberFormatException ex) {
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalNodeNoFormat, 
					strNode1, strNode2);
		}
		
		finally {
	        tokenizer = null;
		}
		
		if (exception != null) {
			throw exception;
		}
	}

	private void addEdge(int nNodeNo1, int nNodeNo2) {
		
		if (nNodeNo1 == nNodeNo2) {
			return;
		}
		
		int nNodeNo_Greater = nNodeNo1;
		if (nNodeNo_Greater < nNodeNo2) {
			nNodeNo_Greater = nNodeNo2;
		}
		int nLength_Cur = m_arrNodeDegrees.length;
		int nLength_New = nLength_Cur;
		while (nLength_New <= nNodeNo_Greater) {
			nLength_New += INCREMENT_NodeNum;
		}
		if (nLength_New > nLength_Cur) {
			int[] arrNodeDegrees_New = new int[nLength_New];
			for (int i = 0; i < nLength_Cur; i++) {
				arrNodeDegrees_New[i] = m_arrNodeDegrees[i];
			}
			
			// release m_arrNodeDegrees
			m_arrNodeDegrees = null;
			m_arrNodeDegrees = arrNodeDegrees_New;
		}
		
		Integer intNodeNo1 = new Integer(nNodeNo1);
		Integer intNodeNo2 = new Integer(nNodeNo2);
		
		List<Integer> listNodeNos_Dst = m_mapEdges.get(intNodeNo1);
		if (listNodeNos_Dst == null) {
			listNodeNos_Dst = new ArrayList<Integer>();
			listNodeNos_Dst.add(intNodeNo2);
			m_mapEdges.put(intNodeNo1, listNodeNos_Dst);
			m_arrNodeDegrees[nNodeNo1] = m_arrNodeDegrees[nNodeNo1] + 1;
		}
		else {
			if ( ! listNodeNos_Dst.contains(intNodeNo2) ) {
			    listNodeNos_Dst.add(intNodeNo2);
				m_arrNodeDegrees[nNodeNo1] = m_arrNodeDegrees[nNodeNo1] + 1;
			}
		}

		listNodeNos_Dst = m_mapEdges.get(intNodeNo2);
		if (listNodeNos_Dst == null) {
			listNodeNos_Dst = new ArrayList<Integer>();
			listNodeNos_Dst.add(intNodeNo1);
			m_mapEdges.put(intNodeNo2, listNodeNos_Dst);
			m_arrNodeDegrees[nNodeNo2] = m_arrNodeDegrees[nNodeNo2] + 1;
		}
		else {
			if ( ! listNodeNos_Dst.contains(intNodeNo1) ) {
			    listNodeNos_Dst.add(intNodeNo1);
				m_arrNodeDegrees[nNodeNo2] = m_arrNodeDegrees[nNodeNo2] + 1;
			}
		}
	}

	public void release() {
		
		m_nMaxNodeNo = 0;
		m_arrNodeDegrees = null;
		
		Set<Integer> setNodes = m_mapEdges.keySet();
		if (setNodes != null) {
			for (Integer intNodeNo : setNodes) {
				List<Integer> listDstNodes = m_mapEdges.get(intNodeNo);
				if (listDstNodes != null) {
					listDstNodes.clear();
					listDstNodes = null;
					m_mapEdges.put(intNodeNo, null);
				}
			}
		}
		m_mapEdges.clear();
		m_mapEdges = null;
	}

}
