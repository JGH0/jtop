package jtop.system.mac;

import java.util.EnumSet;
import jtop.system.Feature;

public final class MacFeatures {

	public static final EnumSet<Feature> SUPPORTED = EnumSet.of(
		Feature.PROCESS
	);

	private MacFeatures() {}
}