package yt2443.ch22;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;


public class CheckinsLoader {

	private static final String STRING_FieldDelimiter = "\t";
	
	private static final int LENGTH_DateTimeString = 20;

	private static CheckinsLoader m_theInstance = null;
	
	public static CheckinsLoader getInstance() {
		
		if (m_theInstance == null) {
			m_theInstance = new CheckinsLoader();
		}
		
		return m_theInstance;
	}
	
	protected CheckinsLoader() {
		// nothing to do here
	}

	public int load(Map<Integer, UserInfo> mapUserInfos, LocDict locDict,
			String strFileName,
			int cHoursPerUnit,
			int nMaxNumOfCheckinsForAnyUser) throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		TimePoint timePoint = new TimePoint();
		
		try {
			inputStream = getClass().getResourceAsStream(strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
			int nUserId = -1, nPrevUserId = -1;
			Integer intUserId = null;
			String strField;
			String strDateTime = null;
			String strLatitude = null, strLongitude = null, strLocId = null;
			int nFieldOrderNo;
			int nLocIndex;
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			int nLineNo = 0, cCheckinsForCurUser = 0;
			StringTokenizer tokenizer;
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
				tokenizer = new StringTokenizer(strLine, STRING_FieldDelimiter);

				nFieldOrderNo = 1;
		        while (tokenizer.hasMoreTokens()) {
		        	
		        	strField = tokenizer.nextToken();
		        	
		        	if (nFieldOrderNo == 1) {
		        		nUserId = Integer.parseInt(strField);
		        		if ( (intUserId == null) || (intUserId.intValue() != nUserId) ) {
		        			intUserId = new Integer(nUserId);
		        		}
		        		if (nMaxNumOfCheckinsForAnyUser > 0) {
			        		if ( (nPrevUserId >= 0) && 
			        				(nPrevUserId == nUserId) ) {
			        			cCheckinsForCurUser++;
			        		}
			        		else {
			        			cCheckinsForCurUser = 0;	// reset the count
			        		}
		        		}
		        	}
		        	else if (nFieldOrderNo == 2) {
		        		strDateTime = strField;
		        	}
		        	else if (nFieldOrderNo == 3) {
		        		strLatitude = strField;
		        	}
		        	else if (nFieldOrderNo == 4) {
		        		strLongitude = strField;
		        	}
		        	else if (nFieldOrderNo == 5) {
		        		strLocId = strField;
		        	}
		        	
		        	if ( (nMaxNumOfCheckinsForAnyUser > 0) &&
		        			(cCheckinsForCurUser > nMaxNumOfCheckinsForAnyUser) ) {
		        		break;
		        	}
		        	
		        	nFieldOrderNo++;
		        }
		        tokenizer = null;
		        
		        try {
		        	if ( (nMaxNumOfCheckinsForAnyUser <= 0) ||
		        			(cCheckinsForCurUser <= nMaxNumOfCheckinsForAnyUser) ) {
		        		
		        		parseTimePoint(timePoint, strDateTime, cHoursPerUnit);
		        		
		        		nLocIndex = locDict.addLoc(strLocId);
		        		
				        CheckinItem item = new CheckinItem(
				        		timePoint,
				        		Float.parseFloat(strLatitude),
				        		Float.parseFloat(strLongitude),
				        		nLocIndex);
				        
				        UserInfo userInfo = mapUserInfos.get(intUserId);
				        if (userInfo == null) {
				        	userInfo = new UserInfo();
				        	userInfo.addCheckinItem(item);
				        	mapUserInfos.put(intUserId, userInfo);
				        }
				        else {
				        	userInfo.addCheckinItem(item);
				        }
		        	}
		        }
		        catch (Exception ex) {
		        	ex.printStackTrace();
		        	Logger.showErrMsg(ex.getMessage(), " at line " + nLineNo);
		        }
		        
		        /*
		        if (ignoreRemainCheckins(strUserId)) {
		        	break;
		        }
		        */
		        
		        nPrevUserId = nUserId;
				strLine = reader.readLine();
			}
		}
		
		catch (NumberFormatException ex) {
        	ex.printStackTrace();
			throw new SnsException(SnsException.ERR_DESP_IllegalUserIdFormat);
		}
		catch (IOException ex) {
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile, strFileName);
		}
		catch (SnsException ex) {
        	ex.printStackTrace();
			exception = ex;
		}
		
		finally {
			timePoint = null;
			
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
		
		return locDict.getNumOfLocIds();
	}

	/*
	private boolean ignoreRemainCheckins(String strUserId) {
		
		try {
			int nUserId = Integer.parseInt(strUserId);
			if (nUserId > 10000) {
				return true;
			}
		}
		catch (NumberFormatException ex) {	
		    ex.printStackTrace();
		}
		
		return false;
	}
	*/

	private void parseTimePoint(TimePoint timePoint, String strDateTime, int cHoursPerUnit) 
			throws SnsException {
		
		if ( (strDateTime == null) || (strDateTime.length() != LENGTH_DateTimeString) ) {
			throw new SnsException(SnsException.ERR_DESP_IllegalDateTimeFormat);
		}
		
		String strYear = getSubString(strDateTime, 0, 4);
		String strMonth = getSubString(strDateTime, 5, 2);
		String strDay = getSubString(strDateTime, 8, 2);
		String strHour = getSubString(strDateTime, 11, 2);
		String strMinute = getSubString(strDateTime, 14, 2);
		String strSecond = getSubString(strDateTime, 17, 2);

		try {		
	        Calendar calendar = Calendar.getInstance();
	        
	        int nYear = Integer.parseInt(strYear);
	        if (nYear < 2000) {
				throw new SnsException(SnsException.ERR_DESP_YearBefore2000, strYear);
	        }
	        
	        int nMonth = Integer.parseInt(strMonth);
	        int nDay = Integer.parseInt(strDay);
	        int nHour = Integer.parseInt(strHour);
	        int nMinute = Integer.parseInt(strMinute);
	        int nSecond = Integer.parseInt(strSecond);
	        
	        timePoint.m_sYMD = TimePoint.combineYMD(nYear, nMonth, nDay);
	        if (cHoursPerUnit == 0) {
	            timePoint.m_byTimeUnit = 0;
	        }
	        else {
	            timePoint.m_byTimeUnit = TimePoint.calcTimeUnit(nHour, cHoursPerUnit);
	        }
	        
	        calendar.set(Calendar.YEAR, nYear);
	        calendar.set(Calendar.MONTH, nMonth - 1);  
	        calendar.set(Calendar.DAY_OF_MONTH, nDay);  
	        calendar.set(Calendar.HOUR_OF_DAY, nHour); 
	        calendar.set(Calendar.MINUTE, nMinute);  
	        calendar.set(Calendar.SECOND, nSecond); 
	        Date date = calendar.getTime();
	        
	        timePoint.m_lTime = date.getTime();
		}
		catch (NumberFormatException ex) {
        	ex.printStackTrace();
			throw new SnsException(SnsException.ERR_DESP_IllegalDateTimeFormat);
		}
	}
	
	private String getSubString(String strDateTime, int nBegin, int nLength) {
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < nLength; i++) {
			sb.append(strDateTime.charAt(nBegin + i));
		}
		
		return sb.toString();
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

}
