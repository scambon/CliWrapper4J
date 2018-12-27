# CliWrapper4J

A library to quickly build Java-friendly APIs to call command line applications.

[![build_status](https://travis-ci.org/scambon/CliWrapper4J.svg?branch=master)](https://travis-ci.org/scambon/CliWrapper4J)



## Concepts

Here are the most common concepts in this library:

- A Command line application is described in an interface that extends <code>ICommandLineWrapper</code> and is annotated with <code>@Exectuable("myApp")</code>
- You create an instance of your application using a <code>ICommandLineWrapperFactory</code> such as <code>CommandLineWrapperFactory</code>
- Application switches are described as methods, either <code>@Option</code> (non-executing) and <code>@Command</code> (executing, now or later)
- Those methods use parameters that need to be converted to <code>String</code>s using <code>@Converter</code>s
- These parameters are flattened into a single element using an <code>@Flattener</code> and aggregated with the switch name using an <code>@Aggregator</code> on the method
- Execution returns a <code>Result</code>, i.e. a return code + standard output + error output, or a custom object of your own using another <code>IConverter</code>

Most annotations have sensible defaults.
Framework expectations are explained in the JavaDoc; some of them are checked by the compiler, other at runtime.

Note that the above only describes the classical behavior.
Advanced behaviors are described on annotations JavaDoc.



## Simple example

A simple example is worth a 1000 words.
Here is a simple one which describes an API for calling the Java executable:

```java
@Executable("java")
public interface IJavaCommandLine extends ICommandLineWrapper {

  @Command("--help")
  int help();

  @Option("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorConverter.class) File... classpathElements);

  @Option("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorConverter.class) Collection<File> classpathElements);

  @Option("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemProperty(String property, Object value);

  @Command("")
  Result main(String mainQualifiedName);

  @Command(value = "-version", converter = VersionResultConverter.class)
  Version version();  

  default int getMajorVersion() {
    return version().getMajorVersion();
  }
}
```

## Advanced capabilities

Some advanced capabilities are described in the JavaDoc for annotations:

- Check different (or no) return codes
- Convert collections, arrays and varargs
- Use an explicit encoding
- Mock execution
- Pass parameters to custom command line executors
- Run interactive command lines