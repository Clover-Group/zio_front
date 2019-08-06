val Http4sVersion     = "0.21.0-SNAPSHOT"
val CirceVersion      = "0.12.0-M4"
val LogbackVersion    = "1.2.3"
val ScalaLogVersion   = "3.9.2"
val PureConfigVersion = "0.11.1"
val ZioVersion        = "1.0.0-RC11-1"
val ZioCatsVersion    = "2.0.0.0-RC2"
val DoobieVersion     = "0.8.0-M3"
val H2Version         = "1.4.199"
val FlywayVersion     = "5.2.4"
val Specs2Version     = "4.7.0"
val ParadiseVersion   = "2.1.1"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file("."))
  .settings(
    organization := "CloverGroup",
    name := "front",
    version := "0.1.0",
    scalaVersion := "2.12.8",
    maxErrors := 3,
    updateOptions := updateOptions.value.withLatestSnapshots(false),
    libraryDependencies ++= Seq(
      "org.http4s"                 %% "http4s-blaze-server"    % Http4sVersion,
      "org.http4s"                 %% "http4s-blaze-client"    % Http4sVersion,
      "org.http4s"                 %% "http4s-circe"           % Http4sVersion,
      "org.http4s"                 %% "http4s-dsl"             % Http4sVersion,
      "io.circe"                   %% "circe-generic"          % CirceVersion,
      "io.circe"                   %% "circe-literal"          % CirceVersion,
      "io.circe"                   %% "circe-parser"           % CirceVersion,
      "org.tpolecat"               %% "doobie-core"            % DoobieVersion,
      "org.tpolecat"               %% "doobie-h2"              % DoobieVersion,
      "org.tpolecat"               %% "doobie-hikari"          % DoobieVersion,
      "com.h2database"             % "h2"                      % H2Version,
      "org.flywaydb"               % "flyway-core"             % FlywayVersion,
      "ch.qos.logback"             % "logback-classic"         % LogbackVersion,
      "com.typesafe.scala-logging" %% "scala-logging"          % ScalaLogVersion,
      "com.github.pureconfig"      %% "pureconfig"             % PureConfigVersion,
      "com.github.pureconfig"      %% "pureconfig-cats-effect" % PureConfigVersion,
      "dev.zio"                    %% "zio"                    % ZioVersion,
      "dev.zio"                    %% "zio-interop-cats"       % ZioCatsVersion,
      "org.specs2"                 %% "specs2-core"            % Specs2Version % Test
    )
  )

scalacOptions --= Seq(
  "-Xfatal-warnings"
)

//scalacOptions := Seq(
//  "-Xsource:2.13",
//  "-Xlint",
//  "-Xverify",
//  "-feature",
//  "-deprecation",
//  "-explaintypes",
//  "-unchecked",
//  "-Xfuture",
//  "-encoding",
//  "UTF-8",
//  "-Yrangepos",
//  "-Xlint:_,-type-parameter-shadow",
//  "-Ywarn-numeric-widen",
//  "-Ywarn-unused",
//  "-Ywarn-value-discard",
//  "-language:higherKinds",
//  "-language:existentials",
//  "-Yno-adapted-args",
//  "-Ypartial-unification",
//  "-Xlint:-infer-any,_",
//  "-Ywarn-value-discard",
//  "-Ywarn-numeric-widen",
//  "-Ywarn-extra-implicit",
//  "-Ywarn-unused:_",
//  "-Ywarn-inaccessible",
//  "-Ywarn-nullary-override",
//  "-Ywarn-nullary-unit",
//  "-opt-inline-from:<source>",
//  "-opt-warnings",
//  "-opt:l:inline"
//)

addCompilerPlugin("org.scalamacros" % "paradise" % ParadiseVersion cross CrossVersion.full)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("chk", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
