package co.torri.jsonr.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import co.torri.jsonr._

class jsonrTest extends FlatSpec with ShouldMatchers {
    
    behavior of "jsonr"

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
    
    case class Someone(val name: String)
    case class Something(val param1: String, val param2: Int)
    case class SomethingElse(val arg1: Something, val arg2: Int)
    
    it should "enable any class to call the toJson method" in {
        val json = Something("hello", 7).toJson.toString
        json should be === """{"param1": "hello", "param2": 7}"""
    }
    
    it should "allow nested objects in toJson calls" in {
        val json = SomethingElse(Something("a", 1), 2).toJson.toString
        json should be === """{"arg1": {"param1": "a", "param2": 1}, "arg2": 2}"""
    }
    
    it should "allow nested null objects in toJson" in {
        val json = SomethingElse(null, 2).toJson.toString
        json should be === """{"arg1": null, "arg2": 2}"""
    }
    
    it should "create groups for lists of classes other than basic types" in {
        val json = $(
            "objs" -> List(Someone("Lucas"), Someone("Isa"))
        ).toString
        
        json should be === """{"objs": [{"name": "Lucas"}, {"name": "Isa"}]}"""
    }

    case class ObjWithList(val i: Int, val l: List[Int])

    it should "handle Iterable objects inside other objects" in {
      val json = ObjWithList(3, List(1,2,3)).toJson.toString

      json should be === """{"i": 3, "l": [1, 2, 3]}"""
    }
	
	it should "convert 'None' to empty string" in {
		val json = $(
            "none" -> None
        ).toString
		
		json should be === """{"none": ""}"""
	}
	
	it should "convert the content of 'Some'" in {
		val json = $(
            "some" -> Some(1)
        ).toString
		
		json should be === """{"some": 1}"""
	}
	
	it should "convert maps to objects" in {
		val json = Map("one" -> 1, "two" -> 2).toJson.toString
		
		json should be === """{"one": 1, "two": 2}"""
	}
	
	it should "convert other type of maps to objects" in {
		val json = scala.collection.mutable.HashMap("two" -> 2).toJson.toString
		
		json should be === """{"two": 2}"""
	}
	
	case class ObjWithMap(theMap: Map[String, String])
	
	it should "convert inner maps to objects" in {
		val json = ObjWithMap(Map("a" -> "b")).toJson.toString
		
		json should be === """{"theMap": {"a": "b"}}"""
	}
	
	it should "get the element of size 1 tuples" in {
		val json = $(
            "tuple1" -> Tuple1(1)
        ).toString
		
		json should be === """{"tuple1": 1}"""
	}
	
	it should "convert tuples of size 3+ to lists" in {
		val json = $(
			"tuple3" -> (3.2, "a", 0),
			"tuple4" -> (1, null, 0, "oi")
        ).toString
		
		json should be === """{"tuple3": [3.2, "a", 0], "tuple4": [1, null, 0, "oi"]}"""
	}
	
	it should "escape characters" in {
		val json = %("\"", "\\", "\b", "\n", "\r", "\t").toString
		
		json should be === """["\"", "\\", "\b", "\n", "\r", "\t"]"""
	}
	
}
