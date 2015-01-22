package yt2443.ch21;

public class SnsException extends Exception {

	public static final String EX_DESP_FileNotExist = "File does not exist.";
	public static final String EX_DESP_IllegalDataFile = "Illegal data file.";
	public static final String EX_DESP_IllegalNodeNoFormat = "Illegal node no format.";
	public static final String EX_DESP_FailedWriteFile = "Failed in writing file";
	
	private String m_strExDesp;
	private String m_strParam1, m_strParam2;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public SnsException(String strExDesp) {
		
		this(strExDesp, null, null);
		
	}

	public SnsException(String strExDesp, String strParam1) {
		
		this(strExDesp, strParam1, null);
		
	}

	public SnsException(String strExDesp, String strParam1, String strParam2) {
		
		m_strExDesp = strExDesp;
		m_strParam1 = strParam1;
		m_strParam2 = strParam2;
	}

	public String getExDesp() {
		
		String strExDesp = m_strExDesp;
		
		if (m_strParam1 != null) {
			strExDesp = strExDesp + " param1 = [" + m_strParam1 + "]";
		}
		
		if (m_strParam2 != null) {
			strExDesp = strExDesp + " param2 = [" + m_strParam2 + "]";
		}
		
		return strExDesp;
	}
	
	public String toString() {
		
		return getExDesp();
	}
}
