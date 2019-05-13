
val FS2Version = "1.0.4"
val Http4sVersion = "0.20.0"
val CirceVersion = "0.11.1"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val PureConfigVersion = "0.10.2"
val CatsEffectVersion="1.3.0"
val CatsCoreVersion="2.0.0-M1"
//val KindProjVersion = "0.10.0"
val KindProjVersion = "0.9.3"
val BetterMonadicForVersion = "0.3.0"
val ZioVersion = "1.0-RC4"


val DoobieVersion = "0.7.0-M5"
val H2Version = "1.4.199"
val FlywayVersion = "5.2.4"
//resolvers += Resolver.sonatypeRepo("releases")

lazy val root = (project in file("."))
  .settings(
    organization := "CloverGroup",
    name := "front",
    version := "0.1.0",
    scalaVersion := "2.12.8",
    maxErrors := 5,
    libraryDependencies ++= Seq(
      //"co.fs2"          %% "fs2-core"             % FS2Version,
      "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"      %% "http4s-circe"         % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"           % Http4sVersion,

      "org.tpolecat"    %% "doobie-core"          % DoobieVersion,
      "org.tpolecat"    %% "doobie-h2"            % DoobieVersion,
      "org.tpolecat"    %% "doobie-hikari"        % DoobieVersion,

      "com.h2database"  %  "h2"                   % H2Version,

      "org.flywaydb"    %  "flyway-core"          % FlywayVersion,

      "io.circe"        %% "circe-generic"        % CirceVersion,

      //"org.specs2"      %% "specs2-core"          % Specs2Version % "test",
      //"ch.qos.logback"   % "logback-classic"      % LogbackVersion, 

      //"org.typelevel"   %% "cats-core"            % CatsCoreVersion, 
      //"org.typelevel"   %% "cats-effect"          % CatsEffectVersion,

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
  "-language:postfixOps",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)
