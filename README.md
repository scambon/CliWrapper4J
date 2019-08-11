# CliWrapper4J

A library to quickly build Java-friendly APIs to call command line applications.

Get it from Maven Central:

```xml
<dependency>
  <groupId>io.github.scambon</groupId>
  <artifactId>io.github.scambon.cliwrapper4j</artifactId>
  <version>1.0.2</version>
</dependency>
```

[![Maven Central](https://img.shields.io/maven-central/v/io.github.scambon/io.github.scambon.cliwrapper4j.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.scambon%22%20AND%20a:%22io.github.scambon.cliwrapper4j%22)
[![JavaDoc.io](http://www.javadoc.io/badge/io.github.scambon/io.github.scambon.cliwrapper4j.svg)](http://www.javadoc.io/doc/io.github.scambon/io.github.scambon.cliwrapper4j)
![Built on Java8](https://img.shields.io/badge/Built%20on-Java%208-brightgreen)
[![Tested on Java 8, 9, 11, 12, 13](https://img.shields.io/badge/Tested%20with-Java%208%2C%209%2C%2011%2C%2012%2C%2013-brightgreen)](https://travis-ci.org/scambon/CliWrapper4J)
[![Travis CI](https://travis-ci.org/scambon/CliWrapper4J.svg?branch=master)](https://travis-ci.org/scambon/CliWrapper4J)
[![SonarCloud Maintainability](https://sonarcloud.io/api/project_badges/measure?project=scambon_CliWrapper4J&metric=sqale_rating&branch=master)](https://sonarcloud.io/dashboard?id=scambon_CliWrapper4J)




## Example
Here is an example of wrapping the `java` executable:

```java
@Executable("java")
public interface IJavaCommandLine extends IExecutable {

  @Switch("--help")
  @ExecuteNow
  int help();

  @Switch("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorParameterConverter.class) File... classpath);

  @Switch("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorParameterConverter.class) Collection<File> classpath);

  @Switch("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemProperty(String property, Object value);

  @Switch("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemPropertyAsStringLength(String property,
      @Converter(StringLengthConverter.class) Object value);

  @Switch("")
  @ExecuteNow
  Result main(String mainQualifiedName);

  @Switch("-version")
  @ExecuteNow
  @Converter(VersionResultConverter.class)
  Version version();

  @Switch("-version")
  @ExecuteNow
  @ReturnCode({})
  @Converter(VersionResultConverter.class)
  Version versionWithoutReturnCodeCheck();

  @Switch("-version")
  @ExecuteNow
  @ReturnCode(1)
  @Converter(VersionResultConverter.class)
  Version versionWithCustomReturnCodeCheck();

  default int getMajorVersion() {
    return version().getMajorVersion();
  }
}
```

To use this executable wrapper, use the following code:

```java
  IExecutableFactory<IJavaCommandLine> javaFactory = new ReflectiveExecutableFactory<>(IJavaCommandLine.class);
  IJavaCommandLine java = javaFactory.create();
  Version version = java.version();
```

There is more in this library than this example.
See below for further details.


## Concepts

### @Executable and IExecutable

The base interface that defines the command line.

Your sub-interface must be annotated with `@Executable` to provide the executable name.
Then, define your methods and annotate them with `@Switch`; some of them will be executable using an additional `@ExecuteNow` or `@ExecuteLater`.

There is no need to implement this interface yourself, use a `IExecutableFactory` instead.
You can still define and implement static methods, default methods or even private methods (JDK9+) in your sub-interface.


### @Switch

An annotation that describes a switch, i.e. a tag and values to be added to the command line.

#### Parameters

The method can have parameters:
- Annotated with `@Converter` to convert the parameters to strings to be passed to command lines (this is the default, implicit behavior, using a `StringQuotedIfNeededConverter`)
- Annotated with `@Extra` to be provided (unconverted) to the framework instead of the command line

Parameters then need to be processed with:
- Annotating the method with `@Flattener` to flatten multiple parameter value strings  into a single one (defaults to `JoiningOnDelimiterFlattener` with <code>" "</code> if annotation omitted)
- Annotating the method with `@Aggregator` to aggregate the command name and the flattened parameter values (defaults to `SymbolAggregator` with <code>" "</code> if annotation omitted)

#### Return type
If the method is not annotated with `@ExecuteNow`, the method must return its interface type.

#### Execution
To trigger execution, use `@ExecuteNow` or `@ExecuteLater` in addition to this annotation.


### @Converter and IConverter
An annotation that describes what `IConverter` is to be used to transform from one type to another.
This must be placed only on `@Switch`-annotated methods.

Depending on the need, the expectations differ:

| Case                                               | Location  | Input type | Output type | Default if not set                       | Comments                                                         |
| -------------------------------------------------- | --------- | -----------| ----------- | ---------------------------------------- | -----------------------------------------------------------------|
| Convert execution result                           | Method    | `Result` | (anything)  | `ResultConverter`                   | Only on `@ExecuteNow`- or `@ExecuteLater`-annotated methods |
| Convert parameter to be passed to the command line | Parameter | (anything) | `String`  | `StringQuotedIfNeededConverter` | Not compatible with `@Extra`                                    |


### @Flattener and IFlattener
An annotation that describes how to flatten a method parameter values, using the `#flattener()` class configured with the `#value()` as its parameter.
This should only defined along with a `@Switch` annotation.


### @Aggregator and IAggregator
An annotation that describes how to aggregate a switch and its flattened parameter values, using the `#aggregator()` class configured with the `#value()` as its parameter.
This should only defined along with a `@Switch` annotation.


### Execution

#### @ExecuteNow

An annotation that makes a `@Switch`-annotated method run the execution of the command line now.

##### Execution
The execution is handled as defined by the `@Executor` on the method.
If none is specified, a `ProcessExecutor` is implicitly used.

##### Command line return code
The return code is checked against the expectations defined by the `@ReturnCode` on the method.
If none is specified, <code>0</code> is implicitly expected.

##### Return value
The return type must be compatible with the `@Converter` on the method.
If none is  specified, a `ResultConverter` is implicitly used.

#### @ExecuteLater
An annotation that makes a `@Switch`-annotated method run the execution of the command line when the `IExecutable#execute()` method is called.
This is used when the method is not the last segment of the command line.

The semantics (and rules) around this annotation are the same as with `@ExecuteNow`, but:
- The `@Switch` method must return its interface
- The execution return type is defined here in `#value()` and must be compatible with the `@Converter`

#### @Executor and IExecutor
An annotation for `@Switch` methods that defines the executor to use to run the command line.
By default, a `ProcessExecutor` is used, which is suitable for non-interactive, short running command lines.
Interactive command lines can be executed using subclasses of `AbstractInteractiveProcessExecutor` or even custom implementations of `IExecutor`.

#### @ReturnCode
An annotation that checks that an `@ExecuteNow` or `@ExecuteLater` execution return codes are as expected.

By default, the return code is checked to be `0`.
This is also the case if this annotation is not explicitly used.
You can customize the expected returns code with `#value()`.
You can also disable the checking by setting `#value()` to `{}`.

### @Extra
An annotation for `@Switch` method parameters that are to be passed to the framework instead of directly to the command line.

Usage include:
- `IConverter#convert(Object, Class, Map)`
- `IFlattener#flatten(java.util.List, String, Map)`
- `IAggregator#aggregate(String, String, String, Map)`
- `IExecutor#execute(List, IExecutionEnvironment, Map)`

The meaning of the parameter values passed as `@Extra` arguments is left to the implementation of the above interfaces.

This annotation is not compatible with `@Converter`: values are passed without conversion.


## Contributing

See [Contributing](CONTRIBUTING.md).