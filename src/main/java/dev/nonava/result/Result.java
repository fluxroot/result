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

/**
 * A container object which contains either a result or a failure. If a result is present, {@link #isPresent()} returns
 * {@code true}. Otherwise, a failure is present and {@link #isFailure()} returns {@code true}.
 *
 * @param <R> the type of the result
 * @param <F> the type of the failure
 */
public final class Result<R, F> {
	private final R result;
	private final F failure;

	private Result(R result, F failure) {
		requireEitherResultOrFailure(result, failure);
		this.result = result;
		this.failure = failure;
	}

	/**
	 * Returns a {@code Result} containing the given non-{@code null} result.
	 *
	 * @param result the result, which must be non-{@code null}
	 * @param <R>    the type of the result
	 * @param <F>    the type of the failure
	 * @return a {@code Result} containing the given non-{@code null} result
	 * @throws IllegalArgumentException if the given result is {@code null}
	 */
	public static <R, F> Result<R, F> of(R result) {
		return new Result<>(result, null);
	}

	/**
	 * Returns a {@code Result} containing the given non-{@code null} failure.
	 *
	 * @param failure the failure, which must be non-{@code null}
	 * @param <R>     the type of the result
	 * @param <F>     the type of the failure
	 * @return a {@code Result} containing the given non-{@code null} failure
	 * @throws IllegalArgumentException if the given failure is {@code null}
	 */
	public static <R, F> Result<R, F> fail(F failure) {
		return new Result<>(null, failure);
	}

	/**
	 * If a result is present, returns {@code true}, otherwise {@code false}.
	 *
	 * @return {@code true} if a result is present, otherwise {@code false}
	 */
	public boolean isPresent() {
		return result != null;
	}

	/**
	 * If a failure is present, returns {@code true}, otherwise {@code false}.
	 *
	 * @return {@code true} if a failure is present, otherwise {@code false}
	 */
	public boolean isFailure() {
		return failure != null;
	}

	/**
	 * If this {@code Result} contains the given result, returns {@code true}, otherwise {@code false}.
	 *
	 * @param result the result expected to be present
	 * @return {@code true} if this {@code Result} contains the given result, otherwise {@code false}
	 */
	public boolean contains(R result) {
		return isPresent() && Objects.equals(this.result, result);
	}

	/**
	 * If this {@code Result} contains the given failure, returns {@code true}, otherwise {@code false}.
	 *
	 * @param failure the failure expected to be present
	 * @return {@code true} if this {@code Result} contains the given failure, otherwise {@code false}
	 */
	public boolean containsFailure(F failure) {
		return isFailure() && Objects.equals(this.failure, failure);
	}

	/**
	 * If a result is present, returns the result, otherwise throws {@code NoSuchElementException}.
	 *
	 * @return the result contained in this {@code Result}
	 * @throws NoSuchElementException if no result is present
	 */
	public R get() {
		if (result == null) {
			throw new NoSuchElementException("No result present");
		}
		return result;
	}

	/**
	 * If a failure is present, returns the failure, otherwise throws {@code NoSuchElementException}.
	 *
	 * @return the failure contained in this {@code Result}
	 * @throws NoSuchElementException if no failure is present
	 */
	public F getFailure() {
		if (failure == null) {
			throw new NoSuchElementException("No failure present");
		}
		return failure;
	}

	/**
	 * If a result is present, performs the given action with the result, otherwise does nothing.
	 *
	 * @param action the action to be performed, if a result is present
	 * @throws NullPointerException if the given action is {@code null}
	 */
	public void ifPresent(Consumer<? super R> action) {
		requireNonNull(action);
		if (isPresent()) {
			action.accept(result);
		}
	}

	/**
	 * If a failure is present, performs the given failure action with the failure, otherwise does nothing.
	 *
	 * @param failureAction the failure action to be performed, if a failure is present
	 * @throws NullPointerException if the given failure action is {@code null}
	 */
	public void ifFailure(Consumer<? super F> failureAction) {
		requireNonNull(failureAction);
		if (isFailure()) {
			failureAction.accept(failure);
		}
	}

	/**
	 * Returns a {@code Result} containing either the result or failure of this {@code Result}. If a result is present,
	 * performs the given action with the result, otherwise does nothing.
	 *
	 * @param action the action to be performed, if a result is present
	 * @return a {@code Result} containing either the result or failure of this {@code Result}
	 * @throws NullPointerException if the given action is {@code null}
	 */
	public Result<R, F> peek(Consumer<? super R> action) {
		requireNonNull(action);
		if (isPresent()) {
			action.accept(result);
		}
		return this;
	}

	/**
	 * Returns a {@code Result} containing either the result or failure of this {@code Result}. If a failure is present,
	 * performs the given failure action with the failure, otherwise does nothing.
	 *
	 * @param failureAction the failure action to be performed, if a failure is present
	 * @return a {@code Result} containing either the result or failure of this {@code Result}
	 * @throws NullPointerException if the given failure action is {@code null}
	 */
	public Result<R, F> peekFailure(Consumer<? super F> failureAction) {
		requireNonNull(failureAction);
		if (isFailure()) {
			failureAction.accept(failure);
		}
		return this;
	}

	/**
	 * If a result is present, and the result matches the given predicate, returns a {@code Result} containing the
	 * result. Otherwise, if the result does not match the given predicate, returns a {@code Result} containing the
	 * given new failure.
	 * <p>
	 * If a failure is present, does nothing.
	 *
	 * @param predicate  the predicate to apply to the result, if present
	 * @param newFailure the new failure
	 * @return a {@code Result} containing the result, if a result is present and the result matches the given
	 * predicate. Otherwise, a {@code Result} containing the given new failure, if the result does not match the given
	 * predicate.
	 * @throws NullPointerException if the given predicate or the given new failure is {@code null}
	 */
	public Result<R, F> filter(Predicate<? super R> predicate, F newFailure) {
		requireNonNull(predicate);
		requireNonNull(newFailure);
		if (isPresent()) {
			return predicate.test(result) ? this : fail(newFailure);
		} else {
			return this;
		}
	}

	/**
	 * If a result is present, returns a {@code Result} containing the result produced by applying the given mapping
	 * function to the result.
	 * <p>
	 * If a failure is present, does nothing.
	 *
	 * @param mapper the mapping function to apply to the result, if present
	 * @param <S>    the type of the result produced by the mapping function
	 * @return a {@code Result} containing the result produced by applying the given mapping function to the result, if
	 * a result is present
	 * @throws NullPointerException if the given mapping function is {@code null}
	 */
	public <S> Result<S, F> map(Function<? super R, ? extends S> mapper) {
		requireNonNull(mapper);
		if (isPresent()) {
			return of(mapper.apply(result));
		} else {
			return fail(failure);
		}
	}

	/**
	 * If a result is present, returns the {@code Result} produced by applying the given mapping function to the result.
	 * <p>
	 * If a failure is present, does nothing.
	 *
	 * @param mapper the mapping function to apply to the result, if present
	 * @param <S>    the type of result of the {@code Result} produced by the mapping function
	 * @return the {@code Result} produced by applying the given mapping function to the result, if a result is present
	 * @throws NullPointerException if the given mapping function is {@code null}
	 */
	public <S> Result<S, F> flatMap(Function<? super R, ? extends Result<? extends S, ? extends F>> mapper) {
		requireNonNull(mapper);
		if (isPresent()) {
			@SuppressWarnings("unchecked")
			Result<S, F> r = (Result<S, F>) mapper.apply(result);
			return r;
		} else {
			return fail(failure);
		}
	}

	/**
	 * If a result is present, returns a {@code Result} containing the result.
	 * <p>
	 * If a failure is present, returns the {@code Result} produced by applying the given failure mapping function to
	 * the failure.
	 *
	 * @param failureMapper the failure mapping function to apply to the failure, if present
	 * @param <G>           the type of the failure of the {@code Result} produced by the failure mapping function
	 * @return a {@code Result} containing the result, otherwise the {@code Result} produced by applying the given
	 * failure mapping function to the failure
	 * @throws NullPointerException if the given failure mapping function is {@code null}
	 */
	public <G> Result<R, G> or(Function<? super F, ? extends Result<? extends R, ? extends G>> failureMapper) {
		requireNonNull(failureMapper);
		if (isPresent()) {
			return of(result);
		} else {
			@SuppressWarnings("unchecked")
			Result<R, G> r = (Result<R, G>) failureMapper.apply(failure);
			return r;
		}
	}

	/**
	 * If a result is present, returns the result.
	 * <p>
	 * If a failure is present, returns the result produced by applying the given failure mapping function to the
	 * failure.
	 *
	 * @param failureMapper the failure mapping function to apply to the failure, if present
	 * @return the result contained in this {@code Result}, otherwise the result produced by applying the given failure
	 * mapping function to the failure
	 * @throws NullPointerException if the given failure mapping function is {@code null}
	 */
	public R orElseGet(Function<? super F, ? extends R> failureMapper) {
		requireNonNull(failureMapper);
		if (isPresent()) {
			return result;
		} else {
			return failureMapper.apply(failure);
		}
	}

	/**
	 * If a result is present, returns the result.
	 * <p>
	 * If a failure is present, throws an exception produced by applying the given failure mapping function to the
	 * failure.
	 *
	 * @param failureMapper the failure mapping function to apply to the failure, if present
	 * @param <E>           the type of the exception produced by the failure mapping function
	 * @return the result contained in this {@code Result}, otherwise throws an exception produced by applying the given
	 * failure mapping function to the failure
	 * @throws E                    if a failure is present
	 * @throws NullPointerException if the given failure mapping function is {@code null}
	 */
	public <E extends Throwable> R orElseThrow(Function<? super F, ? extends E> failureMapper) throws E {
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
