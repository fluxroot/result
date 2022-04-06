Result
======

Copyright 2022 Phokham Nonava

![Build](https://github.com/fluxroot/result/actions/workflows/build.yml/badge.svg)


Installation
------------
Result 1.x requires Java 8 or higher. All artifacts are available in the Maven Central repository.

Use the following code snippet in Maven:

```xml
<dependency>
    <groupId>io.github.fluxroot</groupId>
    <artifactId>result</artifactId>
    <version>1.0.0</version>
</dependency>
```

Use the following code snippet in Gradle:

```kotlin
dependencies {
    implementation("io.github.fluxroot:result:1.0.0")
}
```


Usage
-----
Create a `Result` using one of the two static factory methods.

```java
// Create a result
Result<String, String> result = Result.of("Success!");
Result<MySuccessType, MyFailureType> result = Result.of(new MySuccessType());

// Create a failure
Result<String, String> result = Result.fail("Something's wrong here!");
Result<MySuccessType, MyFailureType> result = Result.fail(new MyFailureType());

// Hint: use the var keyword to make the code more readable
var result = Result.of(new MySuccessType());
var result = Result.fail(new MyFailureType());
```

Process a `Result` using the fluent API.

```java
String result = Result.of("Success!")
    .map(r -> "What a " + r)
    .filter(r -> r.equals("Hello World!"), "Did not match")
    .peekFailure(System.out::println)
    .orElseGet(f -> f + ", but still a success!");
assertEquals(result, "Did not match, but still a success!");
```


License
-------
Result is released under the MIT License.
