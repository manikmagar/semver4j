/*
 *  Copyright 2022 Manik Magar
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.manikmagar.semver4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.manikmagar.semver4j.internal.Util;

import static com.github.manikmagar.semver4j.internal.Util.isNotEmpty;

/**
 * Create a valid Semantic Version. See
 * <a href="https://semver.org/">SemVer.org</a> for specification details.
 *
 * @author manikmagar
 */
public class SemVer {
	private final AtomicInteger major;
	private final AtomicInteger minor;
	private final AtomicInteger patch;
	private List<Prerelease> prereleases = Collections.synchronizedList(new ArrayList<>());
	private List<BuildMetadata> buildMetadata = Collections.synchronizedList(new ArrayList<>());

	public static final SemVer ZERO = new SemVer(0, 0, 0);

	/**
	 * Create a SemVer for given version components
	 * 
	 * @param major
	 *            int
	 * @param minor
	 *            int
	 * @param patch
	 *            int
	 */
	public SemVer(int major, int minor, int patch) {
		Util.mustBePositive(major);
		Util.mustBePositive(minor);
		Util.mustBePositive(patch);
		this.major = new AtomicInteger(major);
		this.minor = new AtomicInteger(minor);
		this.patch = new AtomicInteger(patch);
	}

	/**
	 * A default SemVer 0.0.0
	 */
	public SemVer() {
		this(0, 0, 0);
	}

	public int getMajor() {
		return major.get();
	}

	public int getMinor() {
		return minor.get();
	}

	public int getPatch() {
		return patch.get();
	}

	public void validate() {
		Util.mustBePositive(getMajor());
		Util.mustBePositive(getMinor());
		Util.mustBePositive(getPatch());
	}

	/**
	 * Create SemVer representing the version with provided version component
	 * numbers See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-2">v2.0.0.html#spec-item-2</a>
	 * 
	 * @param major
	 *            version of the SemVer
	 * @param minor
	 *            version of the SemVer
	 * @param patch
	 *            version of the SemVer
	 * @return SemVer with current version
	 */
	public static SemVer of(int major, int minor, int patch) {
		return new SemVer(major, minor, patch);
	}

	/**
	 * Returns {@link SemVer} for version 0.0.0
	 *
	 * @return SemVer
	 */
	public static SemVer zero() {
		return new SemVer(0, 0, 0);
	}
	/**
	 * Add a prerelease identifier to the version. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-9">v2.0.0.html#spec-item-9</a>
	 * 
	 * @param prerelease
	 *            {@link Prerelease} Identifier
	 * @return SemVer with current version
	 */
	public SemVer with(Prerelease prerelease) {
		this.prereleases.add(prerelease);
		return this;
	}

	/**
	 * Resets any existing prerelease identifiers in this version and adds a
	 * provided prerelease as a new. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-9">v2.0.0.html#spec-item-9</a>
	 *
	 * @param prerelease
	 *            {@link Prerelease} Identifier
	 * @return SemVer with current version
	 */
	public SemVer withNew(Prerelease prerelease) {
		resetPrereleasesIfNeeded();
		this.prereleases.add(prerelease);
		return this;
	}

	/**
	 * Create {@link Prerelease} for given identifier
	 * 
	 * @param identifier
	 *            {@link String}
	 * @return Prerelease
	 */
	public static Prerelease prerelease(String identifier) {
		return Prerelease.of(identifier);
	}

	/**
	 * Create {@link BuildMetadata} for given identifier
	 * 
	 * @param identifier
	 *            {@link String}
	 * @return BuildMetadata
	 */
	public static BuildMetadata build(String identifier) {
		return BuildMetadata.of(identifier);
	}

	/**
	 * Add a Build metadata identifier to the version. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-9">v2.0.0.html#spec-item-9</a>
	 * 
	 * @param buildMetadata
	 *            {@link BuildMetadata} Identifier
	 * @return SemVer with current version
	 */
	public SemVer with(BuildMetadata buildMetadata) {
		this.buildMetadata.add(buildMetadata);
		return this;
	}

	/**
	 * Resets any existing Build metadata identifiers in this version and adds a
	 * provided build metadata as a new . See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-9">v2.0.0.html#spec-item-9</a>
	 *
	 * @param buildMetadata
	 *            {@link BuildMetadata} Identifier
	 * @return SemVer with current version
	 */
	public SemVer withNew(BuildMetadata buildMetadata) {
		resetBuildMetadataIfNeeded();
		this.buildMetadata.add(buildMetadata);
		return this;
	}

	/**
	 * Increment major number of this version. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-8">v2.0.0.html#spec-item-8</a>.
	 * 
	 * @return SemVer with new version
	 */
	public SemVer incrementMajor() {
		major.incrementAndGet();
		minor.set(0);
		patch.set(0);
		resetMetadata();
		return this;
	}

	/**
	 * Increment minor number of this version. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-7">v2.0.0.html#spec-item-7</a>.
	 * 
	 * @return SemVer with new version
	 */
	public SemVer incrementMinor() {
		minor.incrementAndGet();
		patch.set(0);
		resetMetadata();
		return this;
	}

	/**
	 * Increment patch number of this version. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-6">v2.0.0.html#spec-item-6</a>.
	 * 
	 * @return SemVer with new version
	 */
	public SemVer incrementPatch() {
		patch.incrementAndGet();
		resetMetadata();
		return this;
	}

	/**
	 * Reset release identifiers and build metadata
	 */
	private void resetMetadata() {
		resetPrereleasesIfNeeded();
		resetBuildMetadataIfNeeded();
	}

	private void resetPrereleasesIfNeeded() {
		if (!prereleases.isEmpty())
			this.prereleases = Collections.synchronizedList(new ArrayList<>());
	}

	private void resetBuildMetadataIfNeeded() {
		if (!buildMetadata.isEmpty())
			this.buildMetadata = Collections.synchronizedList(new ArrayList<>());
	}
	/**
	 * Major version zero is for initial development. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-4">v2.0.0.html#spec-item-4</a>.
	 *
	 * @return true if this is an initial development version.
	 */
	public boolean isInitialDevelopment() {
		return getMajor() == 0;
	}

	public boolean isPrerelease() {
		return !this.prereleases.isEmpty();
	}

	@Override
	public String toString() {
		String version = String.format("%d.%d.%d", getMajor(), getMinor(), getPatch());
		String releaseIdentifier = prereleases.stream().map(Prerelease::getLabel).collect(Collectors.joining("."));
		if (isNotEmpty(releaseIdentifier))
			version = version + "-" + releaseIdentifier;
		String buildIdentifier = buildMetadata.stream().map(BuildMetadata::getLabel).collect(Collectors.joining("."));
		if (isNotEmpty(buildIdentifier)) {
			version = version + "+" + buildIdentifier;
		}
		return version;
	}

	public static class Identifier {
		static Pattern PATTERN = Pattern.compile("^[0-9A-Za-z-]*$");
		static Pattern NUMERIC_VALUE = Pattern.compile("^[0-9]*$");
		private final String label;
		private Identifier(String label) {
			this.label = label;
		}
		public static Identifier of(String label) {
			return new Identifier(validated(label));
		}

		public static String validated(String label) {
			Objects.requireNonNull(label, "Identifier must not be null");
			if (!PATTERN.matcher(label).matches()) {
				throw new IllegalArgumentException(
						String.format("Identifier '%s' does not match with pattern '%s'", label, PATTERN.pattern()));
			}
			return label;
		}

		public String getLabel() {
			return label;
		}
	}

	/**
	 * SemVer Prerelease Identifier. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-9">v2.0.0.html#spec-item-9</a>
	 */
	public static class Prerelease extends Identifier {
		private Prerelease(String label) {
			super(label);
		}

		/**
		 * Get {@link Prerelease} for given label
		 * 
		 * @param label
		 *            {@link String} for prerelease identifier
		 * @return Prerelease
		 */
		public static Prerelease of(String label) {
			return new Prerelease(validated(label));
		}

		/**
		 * Validates that label contains allowed characters only. When using numeric
		 * identifier, leading zeros are not allowed.
		 * 
		 * @param label
		 *            {@link String} of the identifier
		 * @return String input label
		 * @throws IllegalArgumentException
		 *             if validation fails
		 */
		public static String validated(String label) {
			Identifier.validated(label);
			if (NUMERIC_VALUE.matcher(label).matches() && label.startsWith("0")) {
				throw new IllegalArgumentException(
						String.format("Leading zeros are not allowed for numerical identifier '%s'", label));
			}
			return label;
		}
	}

	/**
	 * SemVer Build Metadata. See <a href=
	 * "https://semver.org/spec/v2.0.0.html#spec-item-10">v2.0.0.html#spec-item-10</a>
	 */
	public static class BuildMetadata extends Identifier {

		private BuildMetadata(String label) {
			super(label);
		}

		public static BuildMetadata of(String label) {
			return new BuildMetadata(validated(label));
		}

	}
}
