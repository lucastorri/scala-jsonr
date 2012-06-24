# what

`jsonr` is a very simple library to create JSON strings in Scala.



# repo

Version are available on the [Sonatype repository](https://oss.sonatype.org/content/repositories/releases/co/torri/). To use with [SBT](https://github.com/harrah/xsbt/wiki/), add the following line to your `build.sbt`:

    libraryDependencies += "co.torri" %% "scala-jsonr" % "0.5"



# using

`jsonr` can be used in two forms: json description or object serialization. Both forms are described on the following sections. For a full list on how to use it, please check the [available unit tests](https://github.com/lucastorri/scala-jsonr/blob/master/src/test/scala/co/torri/jsonr/jsonrTest.scala).

In both cases it is necessary to import the lib on where it will be used, like:

    import co.torri.jsonr._


## json description

In this form JSON objects are described using the special method `$`. It receive a sequence of key/pairs, which are separated by an arrow symbol (`->`). All the basic types are supported. An example of this form of usage is:

    var json = $(
      "key"   -    "value",
      "other" -    2,
      "array" -    List(1,2,3,4,5),
      "more"  -    List(
        $(
          "foo" -    "bar"
        ),
        $(
          "bar" -    "baz"
        )
      ),
      "good"  -    "bye"
    )
    println(json) // {"key": "value", "other": 2, "array": [1, 2, 3, 4, 5], "more": [{"foo": "bar"}, {"bar": "baz"}], "good": "bye"}


## object serialization

Any existent object can be automatically converted to JSON using the special method `.toJson`:

    class Something(val param1: String, val param2: Int)
    class SomethingElse(val arg1: Something, val arg2: Int)
    val json = new SomethingElse(new Something("a", 1), 2).toJson
    println(json) // {"arg1": {"param1": "a", "param2": 1}, "arg2": 2}

The same apply for Maps:

    val json = Map("one" -    1, "two" -    2).toJson
    println(json) // """{"one": 1, "two": 2}"""