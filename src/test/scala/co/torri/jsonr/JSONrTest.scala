package co.torri.jsonr

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import co.torri.jsonr._

class JSONrTest extends FlatSpec with ShouldMatchers {

    "JSONr" should "create a JSON String from Strings" in {
        val json = $(
            "key" -> "value"
        ).toString
        
        json should be === """{"key": "value"}"""
    }
    
    it should "create a JSON String from Ints, Floats and other numbers" in {
        val json = $(
            "double" -> 3.2,
            "int"    -> 13
        ).toString
        
        json should be === """{"double": 3.2, "int": 13}"""
    }
    
    it should "create a JSON String from Booleans" in {
        val json = $(
            "bool1" -> true,
            "bool2" -> false
        ).toString
        
        json should be === """{"bool1": true, "bool2": false}"""
    }
    
    it should "create a JSON String for simple inners lists" in {
        val json = $(
            "list" -> %(1, 3.2, true, "str")
        ).toString
        
        json should be === """{"list": [1, 3.2, true, "str"]}"""
    }
    
    it should "create a JSON String for lists of blocks" in {
        val json = $(
            "people" -> %(
                $("name" -> "Lucas"),
                $("name" -> "Bruno")
            )
        ).toString
        
        json should be === """{"people": [{"name": "Lucas"}, {"name": "Bruno"}]}"""
    }
    
    it should "create JSON with for comprehensions" in {
        case class Person(firstname: String, lastname: String)
        val people = List(Person("A", "B"), Person("C", "D"))
        
        val json = $(
          "people" -> {
            for (p <- people) yield $(
              "fn" -> p.firstname,
              "ln" -> p.lastname
            )
          }
        ).toString
        
        json should be === """{"people": [{"fn": "A", "ln": "B"}, {"fn": "C", "ln": "D"}]}"""
    }
    
    it should "allow the use of a Iterable instead of %(...)" in {
        val json = $(
            "list" -> List(1,2,3)
        ).toString
        
        json should be === """{"list": [1, 2, 3]}"""
    }
    
}