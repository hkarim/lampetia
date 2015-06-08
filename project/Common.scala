package io.github.lampetia

import sbt._

object Common {

  val organization = "io.lampetia"
  val version = "0.1-SNAPSHOT"

  object Versions {
    val scalaVersion = "2.11.6"
    val jodaTimeVersion = "2.8"
    val jodaConvertVersion = "1.7"
    val ficusVersion = "1.1.1"
    val shapelessVersion = "2.2.0"
    val akkaVersion = "2.3.9"
    val playVersion = "2.4.0"
    val logbackVersion = "1.1.2"
    val postgresqlJdbcVersion = "9.4-1201-jdbc41"
    val h2Version = "1.4.187"
    val hikariVersion = "2.3.7"
  }

  object Resolvers {
    val typesafe = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
    val sonatype = Resolver.sonatypeRepo("releases")
    val all = Seq(typesafe,sonatype)
  }

  object Settings {
    //val scalacOptions = Seq("-unchecked", "-deprecation", "-feature", "-Xlint")
    val scalacOptions = Seq("-unchecked", "-deprecation", "-feature")
  }

  object Dependencies {
    import Versions._

    val ficus = "net.ceedubs" %% "ficus" % ficusVersion
    val jodaTime = "joda-time" % "joda-time" % jodaTimeVersion
    val jodaConvert = "org.joda" % "joda-convert" % jodaConvertVersion
    val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion
    val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
    val playFunctional = "com.typesafe.play" %% "play-functional" % playVersion
    val postgresql = "org.postgresql" % "postgresql" % postgresqlJdbcVersion
    val h2 = "com.h2database" % "h2" % h2Version
    val hikari = "com.zaxxer" % "HikariCP" % hikariVersion

    /*val lampetiaDependencies =
      Seq(
        ficus,
        logback,
        akkaActor,
        playFunctional,
        postgresql,
        hikari)*/
  }

}
