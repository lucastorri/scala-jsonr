package co.torri

package object jsonr {
    
    private def isABasicScalaType(a: AnyRef) = {
        a == null                 ||
        a.isInstanceOf[Unit]      ||
        a.isInstanceOf[String]    ||
        a.isInstanceOf[Symbol]    ||
        a.isInstanceOf[Char]      ||
        a.isInstanceOf[Byte]      ||
        a.isInstanceOf[Short]     ||
        a.isInstanceOf[Int]       ||
        a.isInstanceOf[Long]      ||
        a.isInstanceOf[Float]     ||
        a.isInstanceOf[Double]    ||
        a.isInstanceOf[Boolean]
    }
    
    object IsABasicScalaType {
        def unapply(a: AnyRef) = {
            if (isABasicScalaType(a)) Some(a)
            else None
        }
    }
	
	private type TupleType = { def productIterator: Iterator[Any] }
    
    private def jsonize(a: Any): String = a match {
		case None				                                                => "\"\""
		case s: Some[_]                                                         => any2jsonable(s.get).toJSON.toString
        case s: String            			                                    => "\"%s\"".format(s)
        case s: Symbol            			                                    => "\"%s\"".format(s.toString)
        case u: Unit              			                                    => ""
		case m: scala.collection.Map[_, _]	                                    => new JSONMap(m).toString
        case i: Iterable[_]       					                            => new JSONArray(i.toList).toString
        case null                                                               => "\"null\""
        case IsABasicScalaType(a)                                               => a.toString
        case b: JSONBlock         												=> b.toString
		case t: TupleType if t.getClass.getName.matches(".*scala.Tuple\\d+")	=> new JSONArray(t.productIterator.toList).toString
        case _                    												=> any2jsonable(a).toJSON.toString
    }

    trait JSONBlock
    class FakeJSONBlock(a: Any) extends JSONBlock {
        override def toString = jsonize(a)
    }
    class RealJSONBlock(inner: List[(String, Any)]) extends JSONBlock {
        override def toString = {
            "{" + inner.map { case (k,v) =>
                """"%s": %s""".format(k, jsonize(v))
            }.mkString(", ") + "}"
        }
    }
    class JSONArray(el: List[Any]) extends JSONBlock {
        override def toString = {
            "[" + el.map(jsonize).mkString(", ") + "]"
        }
    }
	class JSONMap(m: scala.collection.Map[_,_]) extends JSONBlock {
		override def toString = {
			new RealJSONBlock(m.toList.map {case (k,v) => (if (k.isInstanceOf[String]) k.toString else jsonize(k), v)}).toString
		}
	}

    def $(all: (String, Any)*) = new RealJSONBlock(List(all: _*))
    def %(all: Any*) = new JSONArray(List(all: _*))
    
    
    class JSONable(a: AnyRef) {
        
        def toJSON: JSONBlock = {
            if (isABasicScalaType(a)) new FakeJSONBlock(a)
			else if (a.isInstanceOf[scala.collection.Map[_,_]]) new JSONMap(a.asInstanceOf[scala.collection.Map[_,_]])
            else if (a.isInstanceOf[Iterable[_]]) new JSONArray(a.asInstanceOf[Iterable[_]].toList)
            else classJSON
        }
        
        private def classJSON: JSONBlock = {
            val c = a.getClass
            val fields = c.getDeclaredFields.filterNot(_.getName.contains("$")).toList
            new RealJSONBlock(
              fields.filter{f => 
                try {
                  c.getDeclaredMethod(f.getName) != null
                } catch {
                  case _ => false
                }
              }.
              map{ f =>
                (f.getName, any2jsonable(c.getDeclaredMethod(f.getName).invoke(a)).toJSON)
              }
            )
        }
    }
    
    implicit def any2jsonable(a: Any) = new JSONable(a.asInstanceOf[AnyRef])
}