// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/benny/Desktop/new1/ITSD-DT2023-24-Template/conf/routes
// @DATE:Sat Mar 01 20:11:24 GMT 2025


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
