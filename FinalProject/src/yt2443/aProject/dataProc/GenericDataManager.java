package yt2443.aProject.dataProc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import yt2443.aProject.SnsException;

public abstract class GenericDataManager {

    protected SnsException closeInputStream(InputStream inStream) {
		
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

    protected SnsException closeReader(BufferedReader reader) {
		
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

    protected SnsException closeOutputStream(OutputStream outputStream) {
		
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

    protected SnsException closeWriter(BufferedWriter writer) {
		
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

}
