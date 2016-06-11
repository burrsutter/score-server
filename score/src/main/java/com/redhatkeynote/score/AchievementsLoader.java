package com.redhatkeynote.score;

import java.util.ArrayList;
import java.util.List;

import com.redhatkeynote.score.Achievement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AchievementsLoader {
	private static final Logger LOG = LoggerFactory.getLogger( AchievementsLoader.class );

	public static List<Achievement> getListOfAchievements() {
		ArrayList<Achievement> list = new ArrayList<Achievement>();

		list.add( new Achievement( "FIVE_CONSEQ", "5 consecutive points" ) );
		list.add( new Achievement( "TEN_CONSEQ", "10 consecutive points" ) );
		list.add( new Achievement( "FIFTY_CONSEQ", "50 consecutive points" ) );
		list.add( new Achievement( "100_POINTS", "100 points" ) );
		list.add( new Achievement( "500_POINTS", "500 points" ) );
		list.add( new Achievement( "TOP_SCORER", "Top scorer" ) );

		return list;
	}

	public static List<String> getListOfAchievementDescr() {
		List<String> descr = new ArrayList<String>(  );
		for( Achievement a : getListOfAchievements() ) {
			descr.add( a.getDesc() );
		}
		return descr;
	}
	
}
