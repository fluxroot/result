/*
 * Copyright 2022 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package dev.nonava.result;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.function.Consumer;

public final class ResultAssert<R, F> extends AbstractAssert<ResultAssert<R, F>, Result<R, F>> {
	private ResultAssert(Result<R, F> actual) {
		super(actual, ResultAssert.class);
	}

	public static <R, F> ResultAssert<R, F> assertThat(Result<R, F> actual) {
		return new ResultAssert<>(actual);
	}

	public ResultAssert<R, F> isPresent() {
		isNotNull();
		Assertions.assertThat(actual.isPresent()).isTrue();
		return this;
	}

	public ResultAssert<R, F> isFailure() {
		isNotNull();
		Assertions.assertThat(actual.isFailure()).isTrue();
		return this;
	}

	public ResultAssert<R, F> contains(R result) {
		isPresent();
		Assertions.assertThat(actual.contains(result)).isTrue();
		return this;
	}

	public ResultAssert<R, F> containsFailure(F failure) {
		isFailure();
		Assertions.assertThat(actual.containsFailure(failure)).isTrue();
		return this;
	}

	public ResultAssert<R, F> hasValueSatisfying(Consumer<R> requirements) {
		isPresent();
		Assertions.assertThat(actual.get()).satisfies(requirements);
		return this;
	}

	public ResultAssert<R, F> hasFailureSatisfying(Consumer<F> requirements) {
		isFailure();
		Assertions.assertThat(actual.getFailure()).satisfies(requirements);
		return this;
	}
}
