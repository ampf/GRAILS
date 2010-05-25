
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title>Mpf Plugin Testbed Home</title>
</head>
<body>

  <div class="body">
    <h1>MPF Detector Plugin Testbed</h1>
    Below is a list of controllers that are currently deployed in this
    application, click on each to execute its default action:
    </p>
    <div class="dialog" style="margin-left: 20px; width: 60%;">
      <ul>
        <g:each var="c" in="${grailsApplication.controllerClasses}">
          <li class="controller"><g:link
            controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
        </g:each>
      </ul>
    </div>
  </div>
</body>
</html>
