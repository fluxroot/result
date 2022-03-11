/*
 * Copyright 2022 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package dev.nonava.result;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static dev.nonava.result.ResultAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ResultTest {

	private static final SomeResult SOME_RESULT = new SomeResult();
	private static final SomeResult SECOND_RESULT = new SomeResult();
	private static final AnotherResult ANOTHER_RESULT = new AnotherResult();
	private static final SomeFailure SOME_FAILURE = new SomeFailure();
	private static final SomeFailure SECOND_FAILURE = new SomeFailure();
	private static final AnotherFailure ANOTHER_FAILURE = new AnotherFailure();
	private static final SomeException SOME_EXCEPTION = new SomeException();

	@Test
	void shouldReturnResult() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		assertThat(result)
				.contains(SOME_RESULT)
				.hasToString("Result[SomeResult]");
	}

	@Test
	void shouldReturnFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		assertThat(result)
				.containsFailure(SOME_FAILURE)
				.hasToString("Failure[SomeFailure]");
	}

	@Test
	void shouldConsumeResultIfResult() {
		SomeResultConsumer consumer = mock(SomeResultConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		result.ifPresent(consumer);
		verify(consumer).accept(SOME_RESULT);
	}

	@Test
	void shouldNotConsumeResultIfFailure() {
		SomeResultConsumer consumer = mock(SomeResultConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		result.ifPresent(consumer);
		verify(consumer, never()).accept(any());
	}

	@Test
	void shouldConsumeFailureIfFailure() {
		SomeFailureConsumer consumer = mock(SomeFailureConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		result.ifFailure(consumer);
		verify(consumer).accept(SOME_FAILURE);
	}

	@Test
	void shouldNotConsumeFailureIfResult() {
		SomeFailureConsumer consumer = mock(SomeFailureConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		result.ifFailure(consumer);
		verify(consumer, never()).accept(any());
	}

	@Test
	void shouldPeekIntoResultIfResult() {
		SomeResultConsumer consumer = mock(SomeResultConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		result.peek(consumer);
		verify(consumer).accept(SOME_RESULT);
	}

	@Test
	void shouldNotPeekIntoResultIfFailure() {
		SomeResultConsumer consumer = mock(SomeResultConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		result.peek(consumer);
		verify(consumer, never()).accept(any());
	}

	@Test
	void shouldPeekIntoFailureIfFailure() {
		SomeFailureConsumer consumer = mock(SomeFailureConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		result.peekFailure(consumer);
		verify(consumer).accept(SOME_FAILURE);
	}

	@Test
	void shouldNotPeekIntoFailureIfResult() {
		SomeFailureConsumer consumer = mock(SomeFailureConsumer.class);
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		result.peekFailure(consumer);
		verify(consumer, never()).accept(any());
	}

	@Test
	void shouldPassOnResultIfFilterMatches() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		Result<SomeResult, SomeFailure> filteredResult = result.filter(someResult -> true, SOME_FAILURE);
		assertThat(filteredResult).contains(SOME_RESULT);
	}

	@Test
	void shouldFilterOutResultIfFilterDoesNotMatch() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		Result<SomeResult, SomeFailure> filteredResult = result.filter(someResult -> false, SOME_FAILURE);
		assertThat(filteredResult).containsFailure(SOME_FAILURE);
	}

	@Test
	void shouldNotFilterFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		Result<SomeResult, SomeFailure> filteredResult = result.filter(someResult -> true, SECOND_FAILURE);
		assertThat(filteredResult).containsFailure(SOME_FAILURE);
	}

	@Test
	void shouldMapResult() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		Result<AnotherResult, SomeFailure> mappedResult = result.map(someResult -> ANOTHER_RESULT);
		assertThat(mappedResult).contains(ANOTHER_RESULT);
	}

	@Test
	void shouldNotMapFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		Result<AnotherResult, SomeFailure> mappedResult = result.map(someResult -> ANOTHER_RESULT);
		assertThat(mappedResult).containsFailure(SOME_FAILURE);
	}

	@Test
	void shouldFlatMapResult() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		Result<AnotherResult, SomeFailure> mappedResult = result.flatMap(someResult -> Result.of(ANOTHER_RESULT));
		assertThat(mappedResult).contains(ANOTHER_RESULT);
	}

	@Test
	void shouldNotFlatMapFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		Result<AnotherResult, SomeFailure> mappedResult = result.flatMap(someResult -> Result.of(ANOTHER_RESULT));
		assertThat(mappedResult).containsFailure(SOME_FAILURE);
	}

	@Test
	void shouldReturnResultFromOrIfPresent() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		Result<SomeResult, AnotherFailure> anotherResult = result.or(someFailure -> Result.fail(ANOTHER_FAILURE));
		assertThat(anotherResult).contains(SOME_RESULT);
	}

	@Test
	void shouldReturnAnotherFailureFromOrIfFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		Result<SomeResult, AnotherFailure> anotherResult = result.or(someFailure -> Result.fail(ANOTHER_FAILURE));
		assertThat(anotherResult).containsFailure(ANOTHER_FAILURE);
	}

	@Test
	void shouldReturnResultFromOrElseGetIfPresent() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		SomeResult someResult = result.orElseGet(someFailure -> SECOND_RESULT);
		assertThat(someResult).isSameAs(SOME_RESULT);
	}

	@Test
	void shouldReturnAnotherResultFromOrElseGetIfFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		SomeResult someResult = result.orElseGet(someFailure -> SECOND_RESULT);
		assertThat(someResult).isSameAs(SECOND_RESULT);
	}

	@Test
	void shouldReturnResultFromOrElseThrowIfPresent() {
		Result<SomeResult, SomeFailure> result = Result.of(SOME_RESULT);
		SomeResult someResult = result.orElseThrow(someFailure -> SOME_EXCEPTION);
		assertThat(someResult).isSameAs(SOME_RESULT);
	}

	@Test
	void shouldThrowFromOrElseThrowIfFailure() {
		Result<SomeResult, SomeFailure> result = Result.fail(SOME_FAILURE);
		Throwable throwable = catchThrowable(() -> result.orElseThrow(someFailure -> SOME_EXCEPTION));
		assertThat(throwable).isInstanceOf(SomeException.class);
	}

	private static final class SomeResult extends BaseResult {
		@Override
		public String toString() {
			return "SomeResult";
		}
	}

	private static final class AnotherResult extends BaseResult {
		@Override
		public String toString() {
			return "AnotherResult";
		}
	}

	private static class BaseResult {
		@Override
		public String toString() {
			return "BaseResult";
		}
	}

	private static final class SomeFailure extends BaseFailure {
		@Override
		public String toString() {
			return "SomeFailure";
		}
	}

	private static final class AnotherFailure extends BaseFailure {
		@Override
		public String toString() {
			return "AnotherFailure";
		}
	}

	private static class BaseFailure {
		@Override
		public String toString() {
			return "BaseFailure";
		}
	}

	private interface SomeResultConsumer extends Consumer<SomeResult> {
	}

	private interface SomeFailureConsumer extends Consumer<SomeFailure> {
	}

	private static final class SomeException extends RuntimeException {
	}
}
