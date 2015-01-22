package yt2443.aProject.dataProc;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import yt2443.aProject.SnsException;
import yt2443.aProject.dataProc.Tagger.TaggedToken;
import yt2443.aProject.entity.StopWordDict;
import yt2443.aProject.entity.TweetDataset;
import yt2443.aProject.entity.TweetInfo;

public class TweetPreProcessor {

	private static final char CHAR_Tag_DiscourseMarker = '~';
	private static final char CHAR_Tag_AtMention = '@';
	private static final char CHAR_Tag_PreOrPostPosition = 'P';
	private static final char CHAR_Tag_Determiner = 'D';
	private static final char CHAR_Tag_Pronoun = 'O';
	private static final char CHAR_Tag_Punctuation = ',';
	private static final char CHAR_Tag_Url = 'U';
	private static final char CHAR_Tag_CoordinatingConjunction = '&';
	private static final char CHAR_Tag_VerbParticle = 'T';
	private static final char CHAR_Tag_Existential = 'X';
	private static final char CHAR_Tag_ExistentialPlusVerbal = 'Y';
	private static final char CHAR_Tag_Emoticon = 'E';
	private static final char CHAR_Tag_Numeral = '$';
	private static final char CHAR_Tag_OtherAbbreviations = 'G';
	private static final char CHAR_Tag_Interjection = '!';
	
	private static final String STRING_WordDelimiter = " ";

	String FILE_NAME_Model = "/cmu/arktweetnlp/model.20120919";
	
	private Tagger m_tagger;
	private StopWordDict m_stopWordDict;
	
	private static TweetPreProcessor m_theInstance = null;
	
	public static TweetPreProcessor getInstance() {
		
		if (m_theInstance == null) {
			m_theInstance = new TweetPreProcessor();
		}
		
		return m_theInstance;
	}

	private TweetPreProcessor() {
		
		m_tagger = null;
		m_stopWordDict = null;
	}
	
	private void createTagger() throws SnsException {
		
		if (m_tagger != null) {
			return;
		}
		
		SnsException exception = null;
		
		try {
			m_tagger = new Tagger();
			m_tagger.loadModel(FILE_NAME_Model);
		}
		
		catch (IOException ex) {
			exception = new SnsException(SnsException.EX_DESP_IOExceptionWhileCreatingTagger, ex.getMessage());
		}
		
		if (exception != null) {
			throw exception;
		}
	}
	
	public void preProcessTweet(TweetDataset tweetDataset) throws SnsException {
		
		if (m_tagger == null) {
			createTagger();
		}
		
		/*
		TweetSet tweetSet = tweetDataset.getTweetSet();
		TweetRelationSet ttRelationSet = tweetDataset.getTweetRelationSet();
		
		tweetSet.clearTextOfRetweets(ttRelationSet);
		int nNumOfTweets = tweetSet.getNumOfTweets();
		/*
		int nNumOfRetweetsDiscovered = tweetSet.getNumOfRetweetsDiscovered();
		////
		int nNumOfTweetsWithRTprefix = tweetSet.getNumOfTweetsWithRTprefix();
		System.out.println("nNumOfTweets[" + nNumOfTweets + 
				// "], nNumOfRetweetsDiscovered[" + nNumOfRetweetsDiscovered +
				"], nNumOfTweetsWithRTprefix[" + nNumOfTweetsWithRTprefix + "], [" +
				( (100.0f * nNumOfTweetsWithRTprefix) / nNumOfTweets ) + "%]");
		// Number of ignored tweets = [9005536, 99.41451%], nNumOfTweets[53037], nNumOfRetweetsDiscovered[0], nNumOfTweetsWithRTprefix[941]
		// Number of ignored tweets = [8870775, 97.93279%], nNumOfTweets[187248], nNumOfRetweetsDiscovered[0], nNumOfTweetsWithRTprefix[2872]
		//*/
		
		/*
		String text = "RT @DjBlack_Pearl: wat muhfuckaz wearin 4 the lingerie party?????";
		List<TaggedToken> taggedTokens = m_tagger.tokenizeAndTag(text);

		for (TaggedToken token : taggedTokens) {
			System.out.printf("%s\t%s\n", token.tag, token.token);
		}

		System.out.println("\r\n\r\n");
		
		text = "show some love..... New Rochelle's Allen To Wrestle in Iowa  Read it: http://t.co/3BQgF3jV http://t.co/3BQgF3jV";
		taggedTokens = m_tagger.tokenizeAndTag(text);

		for (TaggedToken token : taggedTokens) {
			System.out.printf("%s\t%s\n", token.tag, token.token);
		}
		// IOException, ClassNotFoundException
		//*/
		
		/*
		String text = "Cu¨¢ndo despertar¨¢ este pueblo aneste";
		List<TaggedToken> taggedTokens = m_tagger.tokenizeAndTag(text);
		for (TaggedToken token : taggedTokens) {
			System.out.printf("%s\t%s\n", token.tag, token.token);
		}
		//*/

		/*
		List<TaggedToken> taggedTokens;
		TweetSet tweetSet = tweetDataset.getTweetSet();
		Map<Long, TweetInfo> mapTweetInfos = tweetSet.getTweetInfos();
		if (mapTweetInfos != null) {
			
			int cTimes = 0;
		    for (TweetInfo tweetInfo : mapTweetInfos.values()) {
		    	
		    	if ( (tweetInfo.m_strText == null) || tweetInfo.m_strText.isEmpty() ) {
		    		continue;
		    	}
		    	
		    	taggedTokens = m_tagger.tokenizeAndTag(tweetInfo.m_strText);
				for (TaggedToken token : taggedTokens) {
					System.out.printf("%s\t%s\r\n", token.tag, token.token);
				}
				
				cTimes++;
				if (cTimes > 1000) {
					break;
				}
		    }
		}
		//*/
		
	}

	public boolean isFilterOut(TweetInfo tweetInfo, TweetFilter tweetFilter) {
		
		if (tweetFilter == null) {
			return false;
		}
		
		if ( ! tweetInfo.m_time.isBetween_IgnoreTimeOfDay(tweetFilter.m_timeFrom, tweetFilter.m_timeTo) ) {	
			return true;
		}
		
		if (tweetInfo.m_strText == null) {
			return false;	// Do NOT filter out tweets which retweet some other tweets
		}
		
		if (tweetFilter.m_nMinLength > 0) {
			
			if (tweetInfo.m_strText.length() < tweetFilter.m_nMinLength) {
				return true;
			}
		}
		
		TextWithTokenCount textWithTokenCount = null;
		if ( (tweetInfo.m_strText_Clear == null) && 
				(tweetFilter.m_bWipeOffNoisyWords) ) {
			textWithTokenCount = wipeOffNoisyWords(tweetInfo.m_strText);
			tweetInfo.m_strText_Clear = textWithTokenCount.m_strText;
		}
		
		if (tweetFilter.m_cMinWords > 0) {
			
			int cWords;
			if (textWithTokenCount == null) {
				
				StringTokenizer tokenizer;
				if (tweetInfo.m_strText_Clear == null) {
					tokenizer = new StringTokenizer(tweetInfo.m_strText, STRING_WordDelimiter);
				}
				else {
					tokenizer = new StringTokenizer(tweetInfo.m_strText_Clear, STRING_WordDelimiter);
				}
				
				cWords = tokenizer.countTokens();
				tokenizer = null;
			}
			else {
				cWords = textWithTokenCount.m_cTokens;
			}
			if (cWords < tweetFilter.m_cMinWords) {
				return true;
			}
		}
		
		return false;
	}
	
	private TextWithTokenCount wipeOffNoisyWords(String strText) {
		
		if ( (strText == null) || strText.isEmpty() ) {
		    return null;
		}
		
		TextWithTokenCount textWithTokenCount = new TextWithTokenCount();
		
		List<TaggedToken> listTaggedTokens = null;
		try {
			
		    if (m_tagger == null) {
				createTagger();
			}
		    
		    StringBuffer sbResult = new StringBuffer();
		    
		    listTaggedTokens = m_tagger.tokenizeAndTag(strText);
		    boolean bFirstFlag = true;
			for (TaggedToken taggedToken : listTaggedTokens) {
				
				if (isNoisyWord(taggedToken.tag, taggedToken.token)) {
					continue;
				}
				
				if (bFirstFlag) {
				    sbResult.append(taggedToken.token);
				    bFirstFlag = false;
				}
				else {
				    sbResult.append(" " + taggedToken.token);
				}
				textWithTokenCount.m_cTokens++;
			}
			
			textWithTokenCount.m_strText = sbResult.toString();
			sbResult = null;
		}
		
		catch (SnsException ex) {
			ex.printStackTrace();
			
			textWithTokenCount.m_strText = strText;
			
			StringTokenizer tokenizer = new StringTokenizer(strText, STRING_WordDelimiter);
			textWithTokenCount.m_cTokens = tokenizer.countTokens();
			tokenizer = null;
		}
		
		finally {
			
			if (listTaggedTokens != null) {
				listTaggedTokens.clear();
				listTaggedTokens = null;
			}
		}
		
		return textWithTokenCount;
	}

	private boolean isNoisyWord(String strTag, String strToken) {
		
		boolean bNoisy = false;
		
		char chFirst = strTag.charAt(0);
		switch (chFirst) {
		case CHAR_Tag_DiscourseMarker:
		case CHAR_Tag_AtMention:
		case CHAR_Tag_PreOrPostPosition:
		case CHAR_Tag_Determiner:
		case CHAR_Tag_Punctuation:
		case CHAR_Tag_Pronoun:
		case CHAR_Tag_Url:
		case CHAR_Tag_CoordinatingConjunction:
		case CHAR_Tag_VerbParticle:
		case CHAR_Tag_Existential:
		case CHAR_Tag_ExistentialPlusVerbal:
		case CHAR_Tag_Emoticon:
		case CHAR_Tag_Numeral:
		case CHAR_Tag_OtherAbbreviations:
		case CHAR_Tag_Interjection:
			bNoisy = true;
			break;
			
		default:
			bNoisy = isStopWord(strToken);
			break;
		}
		
		return bNoisy;
	}

	private boolean isStopWord(String strToken) {
		
		if (m_stopWordDict == null) {
			m_stopWordDict = StopWordDict.getInstance();
		}
		
		return m_stopWordDict.isStopWord(strToken);
	}

	public void release() {
		
		if (m_tagger != null) {
			m_tagger.model = null;
			m_tagger = null;
		}	
	}
	
	public class TextWithTokenCount {

		public int m_cTokens;
		public String m_strText;
		
	}
}
