// mappings for the MpfDetectorPluginTestbed application
class UrlMappings {
    static mappings = {
      //"/rest/mpfTask/$id?" (resource:"mpfTask")
      //"/rest/mpfTask/$id?/events" (controller:"mpfTask")
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
      "/"(view:"/index")
	  //"500"(view:'/error')
	}
}
