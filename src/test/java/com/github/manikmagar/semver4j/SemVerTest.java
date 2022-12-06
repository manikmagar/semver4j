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

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.github.manikmagar.semver4j.SemVer.build;
import static com.github.manikmagar.semver4j.SemVer.prerelease;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class SemVerTest {

	@Test
	@DisplayName("Default SemVer version value")
	void defaultVersion() {
		assertThat(new SemVer()).as("Semver Default").extracting(SemVer::getMajor, SemVer::getMinor, SemVer::getPatch)
				.containsExactly(0, 0, 0);
	}

	@Test
	@DisplayName("Core version components extracted")
	void coreSemVerComponents() {
		assertThat(new SemVer(1, 2, 3)).as("Semver 1.2.3")
				.extracting(SemVer::getMajor, SemVer::getMinor, SemVer::getPatch).containsExactly(1, 2, 3);
	}

	@Test
	@DisplayName("Core version to String")
	void coreSemVerToString() {
		assertThat(new SemVer(1, 2, 3)).as("Semver 1.2.3").asString().isEqualTo("1.2.3");
	}

	@ParameterizedTest
	@CsvSource({"-1,1,1,-1", "2,-2,2,-2", "3,3,-3,-3"})
	@DisplayName("Negative version component value")
	void negativeVersionTest(int major, int minor, int patch, int negativeValue) {
		IllegalArgumentException exception = Assertions.catchThrowableOfType(() -> new SemVer(major, minor, patch),
				IllegalArgumentException.class);
		assertThat(exception).isNotNull().hasMessage(String.format("Number %s must be positive", negativeValue));
	}

	@Test
	void validate() {
	}

	@Test
	void withVersions() {
		assertThat(SemVer.of(1, 2, 3)).as("Semver 1.2.3").asString().isEqualTo("1.2.3");
	}

	@Test
	@DisplayName("Increment Major version")
	void incrementMajor() {
		assertThat(new SemVer(1, 2, 3).incrementMajor()).as("Semver 1.2.3 incremented major").asString()
				.isEqualTo("2.0.0");
	}

	@Test
	@DisplayName("Increment Major version with Prerelease")
	void incrementMajorWithPrerelease() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("alpha")).incrementMajor())
				.as("Semver 1.2.3-aplha incremented major").asString().isEqualTo("2.0.0");
	}

	@Test
	@DisplayName("Increment Minor version")
	void incrementMinor() {
		assertThat(new SemVer(1, 2, 3).incrementMinor()).as("Semver 1.2.3 incremented minor").asString()
				.isEqualTo("1.3.0");
	}
	@Test
	@DisplayName("Increment Minor version with Prerelease")
	void incrementMinorWithPrerelease() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("alpha")).incrementMinor())
				.as("Semver 1.2.3-alpha incremented minor").asString().isEqualTo("1.3.0");
	}

	@Test
	@DisplayName("Increment Patch version")
	void incrementPatch() {
		assertThat(new SemVer(1, 2, 3).incrementPatch()).as("Semver 1.2.3 incremented patch").asString()
				.isEqualTo("1.2.4");
	}

	@Test
	@DisplayName("Increment Patch version with Prerelease")
	void incrementPatchWithPrerelease() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("alpha")).incrementPatch())
				.as("Semver 1.2.3-alphas incremented patch").asString().isEqualTo("1.2.4");
	}

	@Test
	@DisplayName("Null prerelease indicator")
	void withNullPrereleaseIndicator() {
		NullPointerException npe = catchThrowableOfType(() -> new SemVer(1, 2, 3).with(prerelease(null)),
				NullPointerException.class);
		assertThat(npe).isNotNull().hasMessage("Identifier must not be null");
	}

	@Test
	@DisplayName("Single prerelease identifier with allowed characters")
	void withSinglePrereleaseIdentifier() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("Al-pha01")))
				.as("Prerelease Version with all allowed characters").asString().isEqualTo("1.2.3-Al-pha01");
	}

	@Test
	@DisplayName("Numeric prerelease identifier without leading zeros")
	void withNumericWithoutLeadingZerosPrereleaseIdentifier() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("1000234"))).as("Prerelease Version with all allowed characters")
				.asString().isEqualTo("1.2.3-1000234");
	}

	@Test
	@DisplayName("Numeric prerelease identifier with leading zeros")
	void withNumericWithLeadingZerosPrereleaseIdentifier() {
		IllegalArgumentException ex = catchThrowableOfType(() -> new SemVer(1, 2, 3).with(prerelease("0001234")),
				IllegalArgumentException.class);
		assertThat(ex).isNotNull().hasMessage("Leading zeros are not allowed for numerical identifier '0001234'");
	}

	@Test
	@DisplayName("Prerelease indicator")
	void isPrerelease() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("Al-pha01")))
				.as("Prerelease Version with all allowed characters").extracting(SemVer::isPrerelease).isEqualTo(true);
	}

	@Test
	@DisplayName("Not Prerelease indicator")
	void isNotPrerelease() {
		assertThat(new SemVer(1, 2, 3).isPrerelease()).as("Not a prerelease").isFalse();
	}

	@Test
	@DisplayName("Single prerelease identifier with disallowed characters")
	void withPrereleaseInvalidIdentifier() {
		IllegalArgumentException exception = catchThrowableOfType(
				() -> new SemVer(1, 2, 3).with(prerelease("Al-pha01#")), IllegalArgumentException.class);
		assertThat(exception).isNotNull()
				.hasMessage("Identifier 'Al-pha01#' does not match with pattern '^[0-9A-Za-z-]*$'");
	}

	@Test
	@DisplayName("Multiple prerelease identifiers")
	void withMultiplePrereleaseIdentifier() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("alpha")).with(prerelease("1")).with(prerelease("2")))
				.as("Prerelease Version").asString().isEqualTo("1.2.3-alpha.1.2");
	}

	@Test
	@DisplayName("Is initial development version")
	void isInitialDevelopment() {
		assertThat(Arrays.asList(new SemVer(), new SemVer(0, 2, 3))).as("Semver Zero Version")
				.allSatisfy(semVer -> assertThat(semVer.isInitialDevelopment()).isTrue());
	}
	@Test
	@DisplayName("Is not initial development version")
	void isNotInitialDevelopment() {
		assertThat(new SemVer(1, 2, 3).isInitialDevelopment()).as("Semver non-Zero Version").isFalse();
	}

	@Test
	@DisplayName("Single Build identifier with allowed characters")
	void withSingleBuildIdentifier() {
		assertThat(new SemVer(1, 2, 3).with(build("Some-01"))).as("Build Version with all allowed characters")
				.asString().isEqualTo("1.2.3+Some-01");
	}

	@Test
	@DisplayName("Build Identifier with prerelease identifier")
	void buildWithPrereleaseIdentifier() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("Alpha-01")).with(build("Some-01")))
				.as("Build Identifier with prerelease identifier").asString().isEqualTo("1.2.3-Alpha-01+Some-01");
	}

	@Test
	@DisplayName("Multiple build and prerelease identifiers")
	void multipleBuildWithPrereleaseIdentifier() {
		assertThat(new SemVer(1, 2, 3).with(prerelease("Alpha")).with(prerelease("1")).with(build("Some"))
				.with(build("001"))).as("Multiple build and prerelease identifiers").asString()
						.isEqualTo("1.2.3-Alpha.1+Some.001");
	}

	@Test
	@DisplayName("Single Build identifier with disallowed characters")
	void withBuildInvalidIdentifier() {
		IllegalArgumentException exception = catchThrowableOfType(() -> new SemVer(1, 2, 3).with(build("Some-01#")),
				IllegalArgumentException.class);
		assertThat(exception).isNotNull()
				.hasMessage("Identifier 'Some-01#' does not match with pattern '^[0-9A-Za-z-]*$'");
	}

	@Test
	@DisplayName("SemVer Zero")
	void zero() {
		assertThat(SemVer.zero()).asString().isEqualTo("0.0.0");
	}
}
