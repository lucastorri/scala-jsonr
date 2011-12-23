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
    
    private def jsonize(a: Any): String = a match {
		case None				  => "\"\""
		case s: Some[_]           => any2jsonable(s.get).toJSON.toString
        case s: String            => format("\"%s\"", s)
        case s: Symbol            => format("\"%s\"", s.toString)
        case u: Unit              => ""
        case i: Iterable[_]       => new JSONArray(i.toList).toString
        case null                 => "\"null\""
        case IsABasicScalaType(a) => a.toString
        case b: JSONBlock         => b.toString
        case _                    => any2jsonable(a).toJSON.toString
    }

    trait JSONBlock
    class FakeJSONBlock(a: Any) extends JSONBlock {
        override def toString = jsonize(a)
    }
    class RealJSONBlock(inner: List[(String, Any)]) extends JSONBlock {
        override def toString = {
            "{" + inner.map { case (k,v) =>
                format(""""%s": %s""", k, jsonize(v))
            }.mkString(", ") + "}"
        }
    }
    class JSONArray(el: List[Any]) extends JSONBlock {
        override def toString = {
            "[" + el.map(jsonize).mkString(", ") + "]"
        }
    }

    def $(all: (String, Any)*) = new RealJSONBlock(List(all: _*))
    def %(all: Any*) = new JSONArray(List(all: _*))
    
    
    class JSONable(a: AnyRef) {
        
        def toJSON: JSONBlock = {
            if (isABasicScalaType(a)) new FakeJSONBlock(a)
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