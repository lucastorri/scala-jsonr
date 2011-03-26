package co.torri.jsonr

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import co.torri.jsonr._

class JSONrTest extends FlatSpec with ShouldMatchers {
    
    behavior of "JSONr"

    it should "create a JSON String from Strings" in {
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
    
    class Someone(val name: String)
    class Something(val param1: String, val param2: Int)
    class SomethingElse(val arg1: Something, val arg2: Int)
    
    it should "enable any class to call the toJSON method" in {
        val json = new Something("hello", 7).toJSON.toString
        json should be === """{"param1": "hello", "param2": 7}"""
    }
    
    it should "allow nested objects in toJSON calls" in {
        val json = new SomethingElse(new Something("a", 1), 2).toJSON.toString
        json should be === """{"arg1": {"param1": "a", "param2": 1}, "arg2": 2}"""
    }
    
    it should "allow nested null objects in toJSON" in {
        val json = new SomethingElse(null, 2).toJSON.toString
        json should be === """{"arg1": "null", "arg2": 2}"""
    }
    
    it should "create groups for lists of classes other than basic types" in {
        val json = $(
            "objs" -> List(new Someone("Lucas"), new Someone("Isa"))
        ).toString
        
        json should be === """{"objs": [{"name": "Lucas"}, {"name": "Isa"}]}"""
    }
    
}