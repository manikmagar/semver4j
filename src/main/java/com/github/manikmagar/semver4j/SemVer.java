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

import static com.github.manikmagar.semver4j.SemVer.Patterns.PRE_RELEASE;

/**
 * Create a valid Semantic Version.
 *
 * See <a href="https://semver.org/">SemVer.org</a> for specification details.
 *
 * @author manikmagar
 */
public class SemVer {
	private AtomicInteger major = new AtomicInteger(0);
	private AtomicInteger minor = new AtomicInteger(0);
	private AtomicInteger patch = new AtomicInteger(0);
	private List<String> releaseIdentifiers = Collections.synchronizedList(new ArrayList<>());

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

	public static SemVer with(int major, int minor, int patch) {
		return new SemVer(major, minor, patch);
	}

	public SemVer withReleaseIdentifier(String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		if (!PRE_RELEASE.matcher(identifier).matches()) {
			throw new IllegalArgumentException(String.format("Release identifier '%s' does not match with pattern '%s'",
					identifier, PRE_RELEASE.pattern()));
		}
		this.releaseIdentifiers.add(identifier);
		return this;
	}

	public SemVer incrementMajor() {
		major.incrementAndGet();
		minor.set(0);
		patch.set(0);
		return this;
	}

	public SemVer incrementMinor() {
		minor.incrementAndGet();
		patch.set(0);
		return this;
	}

	public SemVer incrementPatch() {
		patch.incrementAndGet();
		return this;
	}

	/**
	 * Reset release identifiers and build metadata
	 */
	private void resetMetadata() {
		if (!releaseIdentifiers.isEmpty())
			this.releaseIdentifiers = Collections.synchronizedList(new ArrayList<>());
	}
	/**
	 * Major version zero is for initial development.
	 * 
	 * @return true if this is an initial development version.
	 */
	public boolean isInitialDevelopment() {
		return getMajor() == 0;
	}

	public boolean isPrerelease() {
		return !this.releaseIdentifiers.isEmpty();
	}

	@Override
	public String toString() {
		String version = String.format("%d.%d.%d", getMajor(), getMinor(), getPatch());
		String releaseIdentifier = releaseIdentifiers.stream().collect(Collectors.joining("."));
		if (!releaseIdentifier.isEmpty())
			version = version + "-" + releaseIdentifier;
		return version;
	}

	public interface Patterns {
		Pattern PRE_RELEASE = Pattern.compile("^[0-9A-Za-z-.]*$");
	}
}
