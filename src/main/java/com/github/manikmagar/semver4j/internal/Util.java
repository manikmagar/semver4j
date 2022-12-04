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

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Util {

	public static <T> void mustPass(T value, Predicate<T> condition, Supplier<String> message) {
		if (!condition.test(value)) {
			throw new IllegalArgumentException(message.get());
		}
	}

	public static void mustBePositive(Integer value) {
		mustPass(value, v -> v >= 0, () -> String.format("Number %s must be positive", value));
	}
}
