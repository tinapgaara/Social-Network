package yt2443.aProject;

import java.util.Comparator;

import yt2443.aProject.entity.TweetInfo;
import yt2443.aProject.entity.TweetSet;

public class TweetComparator_ByTime implements Comparator<Long> {

	private TweetSet m_tweetSet;
	
	public TweetComparator_ByTime(TweetSet tweetSet) {
		
		m_tweetSet = tweetSet;
	}
	
	@Override
	public int compare(Long longTweetId_1, Long longTweetId_2) {
		
		TweetInfo tweetInfo_1 = m_tweetSet.getTweetInfo(longTweetId_1);
		TweetInfo tweetInfo_2 = m_tweetSet.getTweetInfo(longTweetId_2);
		
		if ( (tweetInfo_1 == null) || (tweetInfo_1.m_time == null) ) {
			
			if ( (tweetInfo_2 == null) || (tweetInfo_2.m_time == null) ) {
				return 0;
			}
			else {
				return +1;
			}
		}
		
		else {
			
			if ( (tweetInfo_2 == null) || (tweetInfo_2.m_time == null) ) {
				return -1;
			}
			else {
				return tweetInfo_1.m_time.compare(tweetInfo_2.m_time);
			}
		}
	}

}
