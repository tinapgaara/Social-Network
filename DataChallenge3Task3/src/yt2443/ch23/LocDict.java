package yt2443.ch23;

import java.util.HashMap;
import java.util.Map;

public class LocDict {

	private Map<String, Integer> m_mapLocIds;
	private int m_nCurMaxLocIndex;
	
	public LocDict() {
		
		m_mapLocIds = new HashMap<String, Integer>();
		m_nCurMaxLocIndex = -1;
	}
	
	public int addLoc(String strLocId) {
		
		Integer intLocIndex = m_mapLocIds.get(strLocId);
		if (intLocIndex != null) {
			return intLocIndex.intValue();
		}
		
		m_nCurMaxLocIndex++;
		m_mapLocIds.put(strLocId, new Integer(m_nCurMaxLocIndex));
		return m_nCurMaxLocIndex;
	}
	
	public int getNumOfLocIds() {
		
		return m_nCurMaxLocIndex + 1;
	}
	
	public void release() {
		
		m_mapLocIds.clear();
		m_mapLocIds = null;
		m_nCurMaxLocIndex = -1;
	}
}
