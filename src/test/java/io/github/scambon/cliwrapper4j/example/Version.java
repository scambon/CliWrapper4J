package io.github.scambon.cliwrapper4j.example;

public class Version {

  private final int majorVersion;

  public Version(int majorVersion) {
    this.majorVersion = majorVersion;
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  public static Version parse(String versionString) {
    String[] versionSegments = versionString.split("\\.");
    String relevantVersion = versionSegments[0];
    if (relevantVersion.equals("1")) {
      relevantVersion = versionSegments[1];
    }
    int javaVersion = Integer.valueOf(relevantVersion);
    return new Version(javaVersion);
  }
}