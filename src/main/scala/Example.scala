import _root_.co.torri.jsonr._

object Example {
    def main(args: Array[String]) = {
        
        var json = $(
          "key"   -> "value",
          "other" -> 2,
          "array" -> %(1,2,3,4,5),
          "more"  -> %(
            $(
              "foo" -> "bar"
            ),
            $(
              "bar" -> "baz"
            )
          ),
          "good"  -> "bye"
        )
        
        println(json)
        
    }
}
