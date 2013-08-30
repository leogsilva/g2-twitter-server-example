import sbt._
import Keys._
import Tests._
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.site.SphinxSupport.Sphinx
import com.typesafe.sbt.SbtStartScript

object G2Server extends Build {
  val gdataVersion = "1.47.1"
   
  def gdata(which: String) = "com.google.gdata.gdata-java-client" % ("gdata-"+which+"-2.0") % gdataVersion
  
  val sharedSettings = Seq(
    version := "0.0.1",
    organization := "com.g2",
    crossScalaVersions := Seq("2.9.2"),

    libraryDependencies ++= Seq(
      "com.g2" %% "g2-finagle-server" % "0.0.1" % "compile",
      "junit" % "junit" % "4.8.1" % "test",
      "org.mockito" % "mockito-all" % "1.8.5" % "test"
    ),
    resolvers += "burtsev-net-maven" at "http://maven.burtsev.net",

    ivyXML :=
      <dependencies>
        <exclude org="com.sun.jmx" module="jmxri" />
        <exclude org="com.sun.jdmk" module="jmxtools" />
        <exclude org="javax.jms" module="jms" />
      </dependencies>,

    scalacOptions ++= Seq("-encoding", "utf8"),
    scalacOptions += "-deprecation",
    javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
    javacOptions in doc := Seq("-source", "1.6"),

    // This is bad news for things like com.twitter.util.Time
    parallelExecution in Test := false,

    // Sonatype publishing
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    publishMavenStyle := true,
    pomExtra := (
      <url>https://github.com/twitter/twitter-server</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:twitter/twitter-server.git</url>
        <connection>scm:git:git@github.com:twitter/twitter-server.git</connection>
      </scm>
      <developers>
        <developer>
          <id>twitter</id>
          <name>Twitter Inc.</name>
          <url>https://www.twitter.com/</url>
        </developer>
      </developers>),
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  )

  lazy val g2Server = Project(
    id = "g2-scala-server",
    base = file("."),
    settings = Project.defaultSettings ++
      sharedSettings ++
      Unidoc.settings ++
      SbtStartScript.startScriptForClassesSettings
  ).settings(
    name := "g2-scala-server",
    autoScalaLibrary := false,
    libraryDependencies ++= Seq(
      gdata("youtube"),
      gdata("youtube-meta")
  ))

  lazy val g2ServerDoc = Project(
    id = "g2-scala-doc",
    base = file("doc"),
    settings =
      Project.defaultSettings ++
      sharedSettings ++
      site.settings ++
      site.sphinxSupport() ++
      Seq(
        scalacOptions in doc <++= (version).map(v => Seq("-doc-title", "Twitter-server", "-doc-version", v)),
        includeFilter in Sphinx := ("*.html" | "*.png" | "*.js" | "*.css" | "*.gif" | "*.txt")
      )
    ).configs(DocTest).settings(
      inConfig(DocTest)(Defaults.testSettings): _*
    ).settings(
      unmanagedSourceDirectories in DocTest <+= baseDirectory { _ / "src/sphinx/code" },

      // Make the "test" command run both, test and doctest:test
      test <<= Seq(test in Test, test in DocTest).dependOn
    ).dependsOn(g2Server)

  /* Test Configuration for running tests on doc sources */
  lazy val DocTest = config("testdoc") extend(Test)
}

