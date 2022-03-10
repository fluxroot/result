/*
 * Copyright 2022 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package dev.nonava.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public final class Result<R, F> {
	private final R result;
	private final F failure;

	private Result(R result, F failure) {
		requireEitherResultOrFailure(result, failure);
		this.result = result;
		this.failure = failure;
	}

	public static <R, F> Result<R, F> of(R result) {
		return new Result<>(result, null);
	}

	public static <R, F> Result<R, F> fail(F failure) {
		return new Result<>(null, failure);
	}

	public boolean isPresent() {
		return result != null;
	}

	public boolean isFailure() {
		return failure != null;
	}

	public boolean contains(R expectedResult) {
		return isPresent() && Objects.equals(result, expectedResult);
	}

	public boolean containsFailure(F expectedFailure) {
		return isFailure() && Objects.equals(failure, expectedFailure);
	}

	public R get() {
		if (result == null) {
			throw new NoSuchElementException("No result present");
		}
		return result;
	}

	public F getFailure() {
		if (failure == null) {
			throw new NoSuchElementException("No failure present");
		}
		return failure;
	}

	public void ifPresent(Consumer<R> consumer) {
		requireNonNull(consumer);
		if (isPresent()) {
			consumer.accept(result);
		}
	}

	public void ifFailure(Consumer<F> failureConsumer) {
		requireNonNull(failureConsumer);
		if (isFailure()) {
			failureConsumer.accept(failure);
		}
	}

	public Result<R, F> peek(Consumer<R> consumer) {
		requireNonNull(consumer);
		if (isPresent()) {
			consumer.accept(result);
		}
		return this;
	}

	public Result<R, F> peekFailure(Consumer<F> failureConsumer) {
		requireNonNull(failureConsumer);
		if (isFailure()) {
			failureConsumer.accept(failure);
		}
		return this;
	}

	public Result<R, F> filter(Predicate<R> predicate, F newFailure) {
		requireNonNull(predicate);
		requireNonNull(newFailure);
		if (isPresent()) {
			return predicate.test(result) ? this : fail(newFailure);
		} else {
			return this;
		}
	}

	public <S> Result<S, F> map(Function<R, S> mapper) {
		requireNonNull(mapper);
		if (isPresent()) {
			return of(mapper.apply(result));
		} else {
			return fail(failure);
		}
	}

	public <S> Result<S, F> flatMap(Function<R, Result<S, F>> mapper) {
		requireNonNull(mapper);
		if (isPresent()) {
			return mapper.apply(result);
		} else {
			return fail(failure);
		}
	}

	public <G> Result<R, G> or(Function<F, Result<R, G>> failureMapper) {
		requireNonNull(failureMapper);
		if (isPresent()) {
			return of(result);
		} else {
			return failureMapper.apply(failure);
		}
	}

	public R orElseGet(Function<F, R> failureMapper) {
		requireNonNull(failureMapper);
		if (isPresent()) {
			return result;
		} else {
			return failureMapper.apply(failure);
		}
	}

	public <E extends Throwable> R orElseThrow(Function<F, E> failureMapper) throws E {
		requireNonNull(failureMapper);
		if (isPresent()) {
			return result;
		} else {
			throw failureMapper.apply(failure);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Result<?, ?> other = (Result<?, ?>) obj;
		return Objects.equals(result, other.result) && Objects.equals(failure, other.failure);
	}

	@Override
	public int hashCode() {
		return Objects.hash(result, failure);
	}

	@Override
	public String toString() {
		if (isPresent()) {
			return String.format("Result[%s]", result);
		} else {
			return String.format("Failure[%s]", failure);
		}
	}

	private static <R, F> void requireEitherResultOrFailure(R result, F failure) {
		if ((result == null) == (failure == null)) {
			throw new IllegalArgumentException("Either result or failure must be set");
		}
	}
}
