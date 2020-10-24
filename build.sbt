name := "tagless"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.2.0",
  "org.typelevel" %% "cats-effect" % "2.2.0",
  "org.typelevel" %% "cats-free" % "2.2.0",
  "org.typelevel" %% "cats-tagless-macros" % "0.12"
)
