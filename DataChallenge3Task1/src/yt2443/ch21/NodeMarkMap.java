package yt2443.ch21;

import java.util.HashMap;
import java.util.Map;

public class NodeMarkMap {

	private Map<Integer, Integer> m_mapNodeMarks;
	
	public NodeMarkMap() {
		
		m_mapNodeMarks = null;
	}
	
	public int getMark(int nNodeNo) {
		
		if (m_mapNodeMarks == null) {
		    return 0;
		}
		
		Integer intMark = m_mapNodeMarks.get(new Integer(nNodeNo));
		if (intMark == null) {
			return 0;
		}
		
		return intMark.intValue();
	}
	
	public void setNodeAndMark(int nNodeNo, int nMark) {
	
		if (m_mapNodeMarks == null) {
			m_mapNodeMarks = new HashMap<Integer, Integer>();
		}
		
		m_mapNodeMarks.put(new Integer(nNodeNo), new Integer(nMark));
	}

	public void removeNode(int nNodeNo) {
		
		if (m_mapNodeMarks != null) {
			m_mapNodeMarks.remove(new Integer(nNodeNo));
		}
	}

	public void release() {
		
		if (m_mapNodeMarks != null) {
			m_mapNodeMarks.clear();
			m_mapNodeMarks = null;
		}
	}

}
