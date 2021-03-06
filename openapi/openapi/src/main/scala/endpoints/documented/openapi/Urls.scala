package endpoints
package documented
package openapi

/**
  * Interpreter for [[algebra.Urls]]
  */
trait Urls extends algebra.Urls {

  type QueryString[A] = DocumentedQueryString

  /**
    * @param parameters List of query string parameters
    */
  case class DocumentedQueryString(parameters: List[DocumentedParameter])

  /**
    * @param name Name of the parameter
    * @param required Whether this parameter is required or not (MUST be true for path parameters)
    */
  // TODO Add documentation about the type of the parameter
  case class DocumentedParameter(name: String, required: Boolean)

  def combineQueryStrings[A, B](first: QueryString[A], second: QueryString[B])(implicit tupler: Tupler[A, B]): QueryString[tupler.Out] =
    DocumentedQueryString(first.parameters ++ second.parameters)

  def qs[A](name: String)(implicit value: QueryStringParam[A]): QueryString[A] =
    DocumentedQueryString(List(DocumentedParameter(name, required = true)))

  def optQs[A](name: String)(implicit value: QueryStringParam[A]): QueryString[Option[A]] =
    DocumentedQueryString(List(DocumentedParameter(name, required = false)))

  type QueryStringParam[A] = Unit

  def stringQueryString: QueryStringParam[String] = ()

  def intQueryString: QueryStringParam[Int] = ()

  def longQueryString: QueryStringParam[Long] = ()

  type Segment[A] = Unit

  def stringSegment: Segment[String] = ()

  def intSegment: Segment[Int] = ()

  def longSegment: Segment[Long] = ()

  type Path[A] = DocumentedUrl

  def staticPathSegment(segment: String): Path[Unit] = DocumentedUrl(segment, Nil, Nil)

  def chainPaths[A, B](first: Path[A], second: Path[B])(implicit tupler: Tupler[A, B]): Path[tupler.Out] =
    DocumentedUrl(
      first.path ++ "/" ++ second.path,
      first.pathParameters ++ second.pathParameters,
      first.queryParameters ++ second.queryParameters // (In practice this should be empty…)
    )

  def segment[A](name: String)(implicit A: Segment[A]): Path[A] =
    DocumentedUrl(s"{$name}", List(DocumentedParameter(name, required = true)), Nil)

  type Url[A] = DocumentedUrl

  /**
    * @param path Path template (e.g. “/user/{id}”)
    * @param pathParameters Path parameters
    * @param queryParameters Query string parameters
    */
  case class DocumentedUrl(path: String, pathParameters: List[DocumentedParameter], queryParameters: List[DocumentedParameter])

  def urlWithQueryString[A, B](path: Path[A], qs: QueryString[B])(implicit tupler: Tupler[A, B]): Url[tupler.Out] =
    path.copy(queryParameters = path.queryParameters ++ qs.parameters)

}
