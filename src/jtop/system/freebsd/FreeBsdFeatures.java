package jtop.system.freebsd;

import java.util.EnumSet;
import jtop.system.Feature;

public final class FreeBsdFeatures {

	public static final EnumSet<Feature> SUPPORTED = EnumSet.of(
		Feature.PROCESS
	);

	private FreeBsdFeatures() {}
}
