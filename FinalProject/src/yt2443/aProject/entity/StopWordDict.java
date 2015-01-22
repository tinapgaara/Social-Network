package yt2443.aProject.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import yt2443.aProject.SnsException;

public class StopWordDict {

	private static StopWordDict m_theInstance = null;
	
	private Map<Long, Set<String>> m_mapStopWords;
	
	/*
	private int[] m_arrPrimes;
	//*/
	
	public static StopWordDict getInstance() {
		
		if (m_theInstance == null) {
			m_theInstance = new StopWordDict();
		}
		
		return m_theInstance;
	}

	private StopWordDict() {
		
		m_mapStopWords = null;
		
		/*
		m_arrPrimes = new int[] {
				3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101
		};
		//*/
	}
	
    public void loadStopWords(String strDataDir, String strFileName) throws SnsException {
    	
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		int nLineNo = 0;
		try {
			inputStream = getClass().getResourceAsStream(strDataDir + strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			while (strLine != null) {
				
				nLineNo++;
				strLine = strLine.trim();
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
	        	addToDict(strLine.toLowerCase());
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
    }
	
    private void addToDict(String strWord) {
		
		Long longHashKey = new Long(calcHashKey(strWord));
		
		Set<String> set;
		if (m_mapStopWords == null) {
			m_mapStopWords = new HashMap<Long, Set<String>>();
			set = new HashSet<String>();
			set.add(strWord);
			m_mapStopWords.put(longHashKey, set);
		}
		else {
		    set = m_mapStopWords.get(longHashKey);
		    if (set == null) {
				set = new HashSet<String>();
				set.add(strWord);
				m_mapStopWords.put(longHashKey, set);
		    }
		    else {
				set.add(strWord);
		    }
		}
	}

	public boolean isStopWord(String strWord) {
		
		if (m_mapStopWords == null) {
		    return false;
		}
		
		String strWordInLowerCase = strWord.toLowerCase();		
		Long longHashKey = new Long(calcHashKey(strWordInLowerCase));
	    Set<String> set = m_mapStopWords.get(longHashKey);
	    if (set == null) {
	    	return false;
	    }
		
	    return set.contains(strWordInLowerCase);
	}

	public int calcHashKey(String strWord) {
		
		return strWord.hashCode();
		
		/*
		int nWordLength = strWord.length();
		int cPrimes = m_arrPrimes.length;
		
		long lHashKey = 0;
		for (int i = 0; i < nWordLength; i++) {
			if (i < cPrimes) {
			    lHashKey += m_arrPrimes[i] * ...;
		}
		return null;
		//*/
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
		
		if (m_mapStopWords != null) {
			
			for (Set<String> set : m_mapStopWords.values()) {	
				set.clear();
			}
			
			m_mapStopWords.clear();
			m_mapStopWords = null;
		}
	}

}
