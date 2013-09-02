resolvers ++= Seq(Resolver.url("sbt-plugin-releases", new URL("http://repo.typesafe.com"))(Resolver.ivyStylePatterns)
,"twitter-repo" at "http://maven.twttr.com")

addSbtPlugin("com.typesafe.sbteclipse" %% "sbteclipse-plugin" % "2.2.0-RC2")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.6.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.9.0")

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "3.3.2")