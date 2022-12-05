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
package com.github.manikmagar.semver4j.internal;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

	@ParameterizedTest
	@MethodSource("emptyStringProvider")
	void isEmpty(String value, boolean result) {
		assertThat(Util.isNotEmpty(value)).isEqualTo(result);
	}

	private static Stream<Arguments> emptyStringProvider() {
		return Stream.of(Arguments.of(null, false), Arguments.of("", false), Arguments.of("  ", false),
				Arguments.of("something", true));
	}

	@ParameterizedTest
	@CsvSource({"1,true", "0,true", "-1, false"})
	void mustBePositive(Integer value, boolean valid) {
		IllegalArgumentException ex = catchThrowableOfType(() -> Util.mustBePositive(value),
				IllegalArgumentException.class);
		if (valid) {
			assertThat(ex).isNull();
		} else {
			assertThat(ex).isNotNull().hasMessage(String.format("Number %s must be positive", value));
		}
	}
}
