class BootStrap {

	def mpfDetectorService
	
     def init = { servletContext ->
     	mpfDetectorService.dispatchJobs()
     }
     def destroy = {
     }
} 