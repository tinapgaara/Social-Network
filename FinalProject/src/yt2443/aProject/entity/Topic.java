package yt2443.aProject.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import yt2443.aProject.Logger;

public class Topic {

	private static final int MIN_TextLengthOfTopic = 20;
	private static final int POS_TopicWeight_Begin = 9;
	private static final int LENGTH_TopicWeight = 5;
	private static final int POS_TopicContent_Begin = 17;
	private static final String STRING_DelimiterBetweenWords = " + ";
	private static final String STRING_DelimiterBetweenWeightAndWord = "*";
	
	
	private float m_fTopicWeight;
	private List<WordRelevance> m_listWordRelevances;
	
	
	public Topic(float fTopicWeight) {
		
		m_fTopicWeight = fTopicWeight;
	}
	
	public void addWordWithWeight(String strWord, float fWordWeight) {
		
		if (m_listWordRelevances == null) {
			m_listWordRelevances = new ArrayList<WordRelevance>();
		}
		
		m_listWordRelevances.add(new WordRelevance(strWord, fWordWeight));
	}
	
	public float getWeightOfWord(String strWord) {
		
		if (m_listWordRelevances == null) {
			return 0;
		}
		
		float fWeight = 0;
		
		for (WordRelevance wordRelevance : m_listWordRelevances) {
			
			if (wordRelevance.m_strWord.equalsIgnoreCase(strWord)) {
				fWeight = wordRelevance.m_fRelevance;
				break;
			}
		}
		
		return fWeight;
	}
	
	public class WordRelevance {
		
		public String m_strWord;
		public float m_fRelevance;
		
		public WordRelevance(String strWord, float fRelevance) {
			
			m_strWord = strWord;
			m_fRelevance = fRelevance;
		}

		public void release() {
			
			m_strWord = null;
		}
	}

	public static Topic parse(String strText) throws NumberFormatException {
		
		if (strText == null) {
			return null;
		}
		
		int nTextLength = strText.length();
		if (nTextLength <= MIN_TextLengthOfTopic) {
		    return null;
		}
		
		String str = strText.substring(POS_TopicWeight_Begin, POS_TopicWeight_Begin + LENGTH_TopicWeight);
		Topic topic = new Topic(Float.parseFloat(str));
		
		str = strText.substring(POS_TopicContent_Begin);
		
		StringTokenizer tokenizer = new StringTokenizer(str, STRING_DelimiterBetweenWords);
		
		StringTokenizer tokenizer_Inner;
		float fWordWeight = 0;
		boolean bErrorFlag = false;
		while (tokenizer.hasMoreElements()) {
			
			str = tokenizer.nextToken();
			tokenizer_Inner = new StringTokenizer(str, STRING_DelimiterBetweenWeightAndWord);
			
			if ( ! tokenizer_Inner.hasMoreTokens() ) {
				tokenizer_Inner = null;
				continue;
			}
			
			str = tokenizer_Inner.nextToken();
			try {
			    fWordWeight = Float.parseFloat(str);
			}
			catch (NumberFormatException ex) {
				Logger.showErrMsg("NumberFormatException in Topic.parse, text=[" + strText + "]");
				bErrorFlag = true;
			}
			
			if (bErrorFlag) {
				tokenizer_Inner = null;
				continue;
			}
			
			if (tokenizer_Inner.hasMoreTokens()) {
			    topic.addWordWithWeight(tokenizer_Inner.nextToken(), fWordWeight);
			}
			
			tokenizer_Inner = null;
			
		}
		
		tokenizer = null;
		
		return topic;
	}

	public void release() {
		
		if (m_listWordRelevances != null) {
			
			for (WordRelevance wr : m_listWordRelevances) {
				wr.release();
			}
			
			m_listWordRelevances.clear();
			m_listWordRelevances = null;
		}
	}

}
