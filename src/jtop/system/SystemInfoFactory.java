package jtop.system;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory to provide system information implementations dynamically
 * based on the current OS and available features.
 * <p>
 * Fully enum-driven: no need to manually add getters for each feature.
 */
public final class SystemInfoFactory {

	private static final OperatingSystem OS = OperatingSystem.detect();
	private static final Set<Feature> SUPPORTED_FEATURES = FeatureResolver.supported(OS);

	private SystemInfoFactory() {}

	/**
	 * Returns an implementation of the requested feature if available for this OS.
	 *
	 * @param feature the feature to request
	 * @return Optional containing the implementation, empty if not supported
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getFeature(Feature feature) {
		if (!SUPPORTED_FEATURES.contains(feature)) return Optional.empty();

		String className = String.format(
				"jtop.system.%s.%s",
				OS.name().toLowerCase(),
				feature.getImplementationClassName()
		);

		try {
			Class<?> clazz = Class.forName(className);
			return Optional.of((T) clazz.getDeclaredConstructor().newInstance());
		} catch (Exception e) {
			System.err.println("Failed to load " + className + ": " + e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * Returns all supported features for this OS.
	 * Can be used to dynamically display available features in UI or CLI.
	 */
	public static Set<Feature> supportedFeatures() {
		return Collections.unmodifiableSet(SUPPORTED_FEATURES);
	}

	/**
	 * Returns a map of all available features to their implementations.
	 * Features not supported on this OS are skipped.
	 */
	public static Map<Feature, Object> allAvailableFeatures() {
		return SUPPORTED_FEATURES.stream()
				.flatMap(f -> getFeature(f).map(inst -> Map.entry(f, inst)).stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
