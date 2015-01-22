package yt2443.aProject.entity;

public class TweetDataset {

	private TweetSet m_tweetSet;
	private FollowerNet m_followerNet;
	private UserTweetRelationSet m_utRelationSet;
	private TweetRelationSet m_ttRelationSet;
	
	public TweetDataset() {
		
	    m_tweetSet = new TweetSet();
	    m_followerNet = new FollowerNet();
	    m_utRelationSet = new UserTweetRelationSet();
	    m_ttRelationSet = new TweetRelationSet();
	}

	public TweetSet getTweetSet() {
		
		return m_tweetSet;
	}

	public FollowerNet getFollowerNet() {
		
		return m_followerNet;
	}

	public UserTweetRelationSet getUserTweetRelationSet() {
		
		return m_utRelationSet;
	}

	public TweetRelationSet getTweetRelationSet() {
		
		return m_ttRelationSet;
	}

	public void release() {
		
		if (m_tweetSet != null) {
			m_tweetSet.release();
			m_tweetSet = null;
		}
		
		if (m_followerNet != null) {
			m_followerNet.release();
			m_followerNet = null;
		}
		
		if (m_utRelationSet != null) {
			m_utRelationSet.release();
			m_utRelationSet = null;
		}
		
		if (m_ttRelationSet != null) {
			m_ttRelationSet.release();
			m_ttRelationSet = null;
		}
	}
	
	
}
