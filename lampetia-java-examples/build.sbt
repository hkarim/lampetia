import dependencies._

name := "lampetia-java-examples"

val bootable = "lampetia.examples.java.security.SecurityService"

mainClass in Compile := Some(bootable)

lazy val service = taskKey[Unit](s"Start HTTP Service $bootable")

fullRunTask(service, Runtime, bootable)

fork in service := true

libraryDependencies ++= Seq(
  ficus,
  jodaConvert,
  jodaTime,
  logback,
  akkaActor,
  playFunctional,
  postgresql,
  h2,
  hikari,
  vertxCore,
  vertxCodegen,
  vertxWeb,
  vertxJdbcClient,
  scalaTest)

