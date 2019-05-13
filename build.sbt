
val FS2Version        = "1.0.4"
val Http4sVersion     = "0.20.0"
val CirceVersion      = "0.11.1"
val Specs2Version     = "4.1.0"
val LogbackVersion    = "1.2.3"
val ScalaLogVersion   = "3.9.2"
val PureConfigVersion = "0.10.2"
val ZioVersion        = "1.0-RC4"
val ScalaTestVersion  = "3.0.5"
val DoobieVersion     = "0.7.0-M5"
val H2Version         = "1.4.199"
val FlywayVersion     = "5.2.4"

//val KindProjVersion = "0.10.0"
//val BetterMonadicForVersion = "0.3.0"
//resolvers += Resolver.sonatypeRepo("releases")

lazy val root = (project in file("."))
  .settings(
    organization := "CloverGroup",
    name := "front",
    version := "0.1.0",
    scalaVersion := "2.12.8",
    maxErrors := 5,
    libraryDependencies ++= Seq(
      "co.fs2"          %% "fs2-core"             % FS2Version,
      "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"         % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"           % Http4sVersion,
      "io.circe"        %% "circe-generic"        % CirceVersion,

      "org.tpolecat"    %% "doobie-core"          % DoobieVersion,
      "org.tpolecat"    %% "doobie-h2"            % DoobieVersion,
      "org.tpolecat"    %% "doobie-hikari"        % DoobieVersion,

      "com.h2database"  %  "h2"                   % H2Version,
      "org.flywaydb"    %  "flyway-core"          % FlywayVersion,

      "ch.qos.logback"   % "logback-classic"      % LogbackVersion, 
      "com.typesafe.scala-logging" %% "scala-logging" % ScalaLogVersion,

      "org.scalactic"   %% "scalactic"            % ScalaTestVersion,
      "org.scalatest"   %% "scalatest"            % ScalaTestVersion,

      "com.github.pureconfig" %% "pureconfig"     % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion, 

      "org.scalaz"      %% "scalaz-zio"           % ZioVersion,
      "org.scalaz"      %% "scalaz-zio-interop-cats"      % ZioVersion,

    )
    //addCompilerPlugin("org.spire-math" %% "kind-projector"     % KindProjVersion),
    //addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % BetterMonadicForVersion)
  )

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-explaintypes",
  "-unchecked",
  "-Xfuture",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:existentials",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)
