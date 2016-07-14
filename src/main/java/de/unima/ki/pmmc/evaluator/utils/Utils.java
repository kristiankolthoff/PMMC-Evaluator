package de.unima.ki.pmmc.evaluator.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

	public static <T> Set<T> toSet(List<T> list) {
		Set<T> set = new HashSet<>();
		for(T t : list) {
			set.add(t);
		}
		return set;
	}
}
