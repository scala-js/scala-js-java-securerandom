import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.firefox.{FirefoxOptions, FirefoxProfile}
import org.openqa.selenium.remote.server.{DriverFactory, DriverProvider}

import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.jsenv.nodejs.NodeJSEnv
import org.scalajs.jsenv.selenium.SeleniumJSEnv

import java.util.concurrent.TimeUnit

import build.JSEnvKind

lazy val jsEnvKind =
  settingKey[JSEnvKind]("Use Node.js or a headless browser for running Scala.js tests")

inThisBuild(Def.settings(
  crossScalaVersions := Seq("2.12.15", "2.11.12", "2.13.8"),
  scalaVersion := crossScalaVersions.value.head,

  version := "1.0.0",
  organization := "org.scala-js",
  scalacOptions ++= Seq(
    "-encoding", "utf-8",
    "-deprecation",
    "-feature",
    "-Xfatal-warnings",
  ),

  // Licensing
  homepage := Some(url("https://github.com/scala-js/scala-js-java-securerandom")),
  startYear := Some(2022),
  licenses += (("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))),
  scmInfo := Some(ScmInfo(
      url("https://github.com/scala-js/scala-js-java-securerandom"),
      "scm:git:git@github.com:scala-js/scala-js-java-securerandom.git",
      Some("scm:git:git@github.com:scala-js/scala-js-java-securerandom.git"))),

  // Publishing
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.endsWith("-SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <developers>
      <developer>
        <id>sjrd</id>
        <name>Sébastien Doeraene</name>
        <url>https://github.com/sjrd/</url>
      </developer>
      <developer>
        <id>gzm0</id>
        <name>Tobias Schlatter</name>
        <url>https://github.com/gzm0/</url>
      </developer>
    </developers>
  ),
  pomIncludeRepository := { _ => false },

  // Environments

  jsEnvKind := JSEnvKind.NodeJS,

  Test / jsEnv := {
    import JSEnvKind._

    jsEnvKind.value match {
      case NodeJS =>
        new NodeJSEnv()
      case JSDOMNodeJS =>
        new JSDOMNodeJSEnv()
      case Firefox =>
        val profile = new FirefoxProfile()
        profile.setPreference("privacy.file_unique_origin", false)
        val options = new FirefoxOptions()
        options.setProfile(profile)
        options.setHeadless(true)
        new SeleniumJSEnv(options)
      case Chrome =>
        val options = new ChromeOptions()
        options.setHeadless(true)
        options.addArguments("--allow-file-access-from-files")
        val factory = new DriverFactory {
          val defaultFactory = SeleniumJSEnv.Config().driverFactory
          def newInstance(capabilities: org.openqa.selenium.Capabilities): WebDriver = {
            val driver = defaultFactory.newInstance(capabilities).asInstanceOf[ChromeDriver]
            driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.HOURS)
            driver.manage().timeouts().setScriptTimeout(1, TimeUnit.HOURS)
            driver
          }
          def registerDriverProvider(provider: DriverProvider): Unit =
            defaultFactory.registerDriverProvider(provider)
        }
        new SeleniumJSEnv(options, SeleniumJSEnv.Config().withDriverFactory(factory))
    }
  },
))

val commonSettings = Def.settings(
  // sbt-header configuration
  headerLicense := Some(HeaderLicense.Custom(
    s"""scalajs-java-securerandom (${homepage.value.get})
       |
       |Copyright EPFL.
       |
       |Licensed under Apache License 2.0
       |(https://www.apache.org/licenses/LICENSE-2.0).
       |
       |See the NOTICE file distributed with this work for
       |additional information regarding copyright ownership.
       |""".stripMargin
  )),
)

lazy val root: Project = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "scalajs-java-securerandom",
    Compile / packageBin / mappings ~= {
      _.filter(!_._2.endsWith(".class"))
    },
    exportJars := true,
  )

lazy val testSuite = crossProject(JSPlatform, JVMPlatform)
  .in(file("test-suite"))
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings: _*)
  .settings(
    testOptions += Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a"),
    buildInfoPackage := "org.scalajs.testsuite.utils",
  )
  .jsConfigure(_.dependsOn(root))
  .jsSettings(
    buildInfoKeys := Seq(BuildInfoKey("jsEnvKind" -> (jsEnvKind.value.toString().toLowerCase(java.util.Locale.ROOT)))),
  )
  .jvmSettings(
    buildInfoKeys := Seq(BuildInfoKey("jsEnvKind" -> "jvm")),
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )
