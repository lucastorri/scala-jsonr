import co.torri.jsonr._

object Example {
    def main(args: Array[String]) = {
        
        var json = $(
          "key"   -> "value",
          "other" -> 2,
          "array" -> %(1,2,3,4,5),
          "strgs" -> %("a", "b", "c"),
          "more"  -> %(
            $(
              "foo" -> "bar"
            ),
            $(
              "bar" -> "baz"
            )
          ),
          "good"  -> "bye",
          "bool"  -> true,
          "doubl" -> 5.67
        )
        
        println(json)
        
        
        case class Person(firstname: String, lastname: String)
        val people = List(Person("Lucas", "Torri"), Person("Guilherme", "Tramontina"))

        val other = $(
          "people" -> {
            for (p <- people) yield $(
              "firstname" -> p.firstname,
              "lastname" -> p.lastname
            )
          }
        )
        
        println(other)
        
    }
}


