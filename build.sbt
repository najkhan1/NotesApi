val Http4sVersion = "0.23.23"
val CirceVersion = "0.14.6"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.4.11"
val MunitCatsEffectVersion = "1.0.7"
val doobieVersion = "1.0.0-RC4"


enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

//Compile / mainClass := Some("com.najkhan.notesapi.Main")
lazy val root = (project in file("."))
  .settings(
    organization := "com.najkhan",
    name := "notesapi",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.11",
    dockerBaseImage := "adoptopenjdk:11-jre-hotspot",
    dockerExposedPorts := Seq(8080),
    dockerRepository := Some("personal"),
    dockerUsername := Some("najkhan1"),
    dockerAlias := DockerAlias(Some("najkhan1"),None,"personal",None),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion         % Runtime,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "mysql"           % "mysql-connector-java" % "8.0.33",
      "org.tpolecat"    %% "doobie-core"         % doobieVersion,
      "org.tpolecat"    %% "doobie-specs2"       % doobieVersion,
      "org.tpolecat"    %% "doobie-hikari"       % doobieVersion,
      "com.typesafe"    %  "config"              % "1.4.2",
      "com.github.pureconfig" %% "pureconfig"    % "0.17.4",
      "org.scalactic"      %% "scalactic"          % "3.2.17",
      "org.scalatest"  %% "scalatest" % "3.2.17" % "test",
      "eu.timepit" %% "refined" % "0.10.3"
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )
assembly / assemblyJarName := "notes-api-1.0.jar"
Docker / version := version.value

