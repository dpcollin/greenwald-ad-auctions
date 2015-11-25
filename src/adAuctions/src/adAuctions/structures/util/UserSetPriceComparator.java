package adAuctions.structures.util;

import java.util.Comparator;

import adAuctions.structures.UserSet;

public class UserSetPriceComparator implements Comparator<UserSet>{

	@Override
	public int compare(UserSet o1, UserSet o2) {
		/*
		 * Order objects by ascending price
		 */
		return (int) (o1.getPrice() - o2.getPrice());
	}

}
