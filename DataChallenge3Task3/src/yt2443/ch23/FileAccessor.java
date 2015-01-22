package yt2443.ch23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class FileAccessor {

	protected abstract void parseLine(String strLine)
			throws SnsException;

	public FileAccessor() {
		// nothing to do
	}
	
	public void buildFromFile(String strFileName)
			throws SnsException {
		
		SnsException exception = null;
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		int nLineNo = 0;
		
		try {
			inputStream = getClass().getResourceAsStream(strFileName);
			if (inputStream == null) {
				throw new SnsException(SnsException.EX_DESP_FileNotExist, strFileName);
			}
			
		    reader = new BufferedReader(new InputStreamReader(inputStream));
			String strLine = reader.readLine();
			
			while (strLine != null) {
				
				nLineNo++;
		    	if (strLine.isEmpty()) {
					strLine = reader.readLine();
		    		continue;
		    	}
		    	
		    	parseLine(strLine);
		    	
				strLine = reader.readLine();
			}
		}
		
		catch (IOException ex) {
        	ex.printStackTrace();
			exception = new SnsException(SnsException.EX_DESP_IllegalDataFile,
					strFileName, "" + nLineNo);
		}
		catch (SnsException ex) {
        	ex.printStackTrace();
			exception = ex;
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
