# Contributing to CliWrapper4J

Use the [project facilities on GitHub](https://github.com/scambon/CliWrapper4J):

- [Git repository](https://github.com/scambon/CliWrapper4J.git)
- [Issues](https://github.com/scambon/CliWrapper4J/issues)
- [Pull Requests](https://github.com/scambon/CliWrapper4J/pulls)

Contributions are welcome!



## Development practices

### Coding

Try to stick with relevant Clean Code practices.

Maximize the amount of code located in the <code>*.internal.*</code> packages, as this is off-contract. 

### Configuration   

The project uses the following configurations:

- Google's CheckStyle configuration
- Default SpotBugs configuration
- Eclipse formatter aligned with the above CheckStyle configuration

As you may have noticed, I'm an Eclipse user and I was so lazy that I included all configuration files in the project. 

### Documentation

JavaDocs for all source code located in <code>src/main</code> should be complete.
In my opinion, this is not incompatible with using meaningful names and other clean code practices.

### Language

Source code, tests and JavaDoc should be written in English to ease adoption (but I will welcome I18N efforts).
As I am not a native speaker, let me know if you find some mistakes in method names, ill-formed sentences or anything similar.

### Tests

Code coverage should not decrease.
However, I think it is OK not to spend too much time testing some highly unlikely exceptions.
Let me know if the "unlikely" hypothesis is no more valid.



## Continuous integration

The project is uses the following services

- [Travis CI](https://travis-ci.com/scambon/CliWrapper4J)
- [SonarCloud](https://sonarcloud.io/dashboard?id=scambon_CliWrapper4J)
- [Sonatype hosting](TODO)