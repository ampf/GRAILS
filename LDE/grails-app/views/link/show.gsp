<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'videoResult.label', default: 'VideoResult')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <h1>Playing Video</h1>
        ${linkInstance} play/${linkInstance?.id}
        <br/>
        <iframe frameborder="1" width="320" height="240" src="../play/${linkInstance?.id}"/>
    </body>
</html>