<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
    </head>
    <body>
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
        </div>
        <div class="logo"><img src="${resource(dir:'images',file:'vedTestLogo.png')}" alt="VedTest" /></div>
                <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" controller="mpfTask" action="create">New MpfTask</g:link></span>
            <span class="menuButton"><g:link class="create" controller="mpfExecTgt" action="create">New Exec Tgt</g:link></span>
            <span class="menuButton"><g:link class="list" controller="mpfEvent" action="list">List Events</g:link></span>
            <span class="menuButton"><g:link class="list" controller="mpfReport" action="list">List Reports</g:link></span>
            <span class="menuButton"><g:link class="list" controller="mpfTask" action="list">List Tasks</g:link></span>
            <span class="menuButton"><g:link class="list" controller="mpfExecTgt" action="list">List ExecTgts</g:link></span>
        </div>
        <g:layoutBody />
    </body>
</html>