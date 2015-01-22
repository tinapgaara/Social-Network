package yt2443.ch23;

public interface IModel {

	public IModelParams getParams();
	
	public Similarity calcSimilarity(UserInfo userInfo_test, UserInfo userInfo_train);

	public void release();
	
}
