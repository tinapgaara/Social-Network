package yt2443.aProject.entity;

import java.util.Comparator;

public class TestResultComparator_ByScore implements
		Comparator<UserIdWithScore> {

	@Override
	public int compare(UserIdWithScore uiws_1, UserIdWithScore uiws_2) {
		
		if (uiws_1.m_fScore < uiws_2.m_fScore) {
			return +1;
		}
		else if (uiws_1.m_fScore > uiws_2.m_fScore) {
			return -1;
		}
		
		return 0;
	}

}
