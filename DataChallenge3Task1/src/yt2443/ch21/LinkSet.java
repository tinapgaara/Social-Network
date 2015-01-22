package yt2443.ch21;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class LinkSet extends FileAccessor {

	private static final String STRING_FieldDelimiter = " ";
	
	private Map<Integer, Integer> m_mapLinks;
	
	public LinkSet() {
		
		m_mapLinks = new HashMap<Integer, Integer>();
	}
	
	public Map<Integer, Integer> getLinks() {
		
		return m_mapLinks;
	}

	public int getLinkCount() {
		
		return m_mapLinks.size();
	}
	
	@Override
	protected void parseLine(String strLine) throws SnsException {
		
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
	        		
	        		/*
	        		addLink(Integer.parseInt(strNode1),
			        		Integer.parseInt(strNode2));
			        //*/
	        		m_mapLinks.put(new Integer(Integer.parseInt(strNode1)), 
	        				new Integer(Integer.parseInt(strNode2)));
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
		
	public void addLink(int nNodeNo1, int nNodeNo2) {
		
		if (m_mapLinks == null) {
			m_mapLinks = new HashMap<Integer, Integer>();
		}
		m_mapLinks.put(new Integer(nNodeNo1), new Integer(nNodeNo2));
	}
	
	public int compareWith(LinkSet another) {
		
		if ( (m_mapLinks == null) || m_mapLinks.isEmpty() ||
				(another.m_mapLinks == null) || another.m_mapLinks.isEmpty() ) {
			return 0;
		}
		
		int cSameLinks = 0;
		
		Integer intValue;
		int nKey, nValue, nValue_Another;
		for (Integer intKey : m_mapLinks.keySet()) {
			
			intValue = m_mapLinks.get(intKey);
			if (intValue == null) {
				continue;
			}
			nValue = intValue.intValue();
			
			intValue = another.m_mapLinks.get(intKey);
			if (intValue == null) {
				continue;
			}
			nValue_Another = intValue.intValue();
			
			if (nValue == nValue_Another) {
				cSameLinks++;
			}
			else {
				nKey = intKey.intValue();
				Logger.showInfo("Error link = [" + nKey + "-->" + nValue +
						"], Correct link = [" + nKey + "-->" + nValue_Another + "]");
			}
		}
		
		return cSameLinks;
	}

	/**************************************************************************
	private void addLink(int nNodeNo1, int nNodeNo2) {
		
		int nLength_Cur = m_arrLinks.length;
		int nLength_New = nLength_Cur;
		while (nLength_New <= nNodeNo1) {
			nLength_New += INCREMENT_NodeNum;
		}
		if (nLength_New > nLength_Cur) {
			int[] arrLinks_New = new int[nLength_New];
			for (int i = 0; i < nLength_Cur; i++) {
				arrLinks_New[i] = m_arrLinks[i];
			}
			
			// release m_arrNodeDegrees
			m_arrLinks = null;
			m_arrLinks = arrLinks_New;
		}
		
		m_arrLinks[nNodeNo1] = nNodeNo2;
	}
	**************************************************************************/

	public void saveIntoFile(String strFileName) throws SnsException {
		
		SnsException exception = null;
		
		OutputStream outputStream = null;
		BufferedWriter writer = null;
		int nLineNo = 1;
		
		try {
			outputStream = new FileOutputStream(strFileName);
			
			writer = new BufferedWriter(new OutputStreamWriter(outputStream));
			
			String strLine;
			
			/*
			for (int i = 1; i < m_arrLinks.length; i++) {
				
				if (m_arrLinks[i] == 0) {
					continue;
				}
				
				strLine = "" + i + STRING_FieldDelimiter + m_arrLinks[i] + "\r\n";
				writer.write(strLine);
			}
			//*/
			
			Set<Integer> setKeys = m_mapLinks.keySet();
			if (setKeys != null) {
				for (Integer intNode1 : setKeys) {
					strLine = "" + intNode1.intValue() + STRING_FieldDelimiter +
							m_mapLinks.get(intNode1).intValue() + "\r\n";
					writer.write(strLine);
				}
			}
		}
		
		catch (IOException ex) {
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_FailedWriteFile,
					strFileName, "" + nLineNo);
		}
		
		finally {
			SnsException ex1 = closeWriter(writer);
			SnsException ex2 = closeOutputStream(outputStream);
			
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

	private SnsException closeOutputStream(OutputStream outputStream) {
		
		if (outputStream == null) {
			return null;
		}
		
		SnsException exception = null;
	    try {
	    	outputStream.close();
		}
	    catch (IOException ex) {
        	ex.printStackTrace();
	    	exception = new SnsException(ex.getMessage());
		}
		return exception;
	}

	private SnsException closeWriter(BufferedWriter writer) {
		
		if (writer == null) {
			return null;
		}
		
		SnsException exception = null;
	    try {
	    	writer.close();
		}
	    catch (IOException ex) {
        	ex.printStackTrace();
	    	exception = new SnsException(ex.getMessage());
		}
		return exception;
	}
	
	public void release() {
		
		m_mapLinks.clear();
		m_mapLinks = null;
	}

}
