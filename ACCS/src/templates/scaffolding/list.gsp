<% import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events %>
<%=packageName%>
<% def COLS=26 %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>${className} List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="\${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New ${className}</g:link></span>
        </div>
        <div class="body">
            <h1>${className} List</h1>
            <g:if test="\${flash.message}">
            <div class="message">\${flash.message}</div>
            </g:if>
            <g:if test="\${flash.error}">
                <div class="errors">
                    <ul><li>\${flash.error}</li></ul>
                 </div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        <%
                            excludedProps = ['version',
                                               Events.ONLOAD_EVENT,
                                               Events.BEFORE_DELETE_EVENT,
                                               Events.BEFORE_INSERT_EVENT,
                                               Events.BEFORE_UPDATE_EVENT]
                            
                            props = domainClass.properties.findAll { !excludedProps.contains(it.name) && it.type != Set.class }
                            Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
                            props.eachWithIndex { p,i ->
                   	            if(i < COLS) {
                   	                if(p.isAssociation()) { %>
                   	        <th>
                   	           <g:link controller="${p.referencedDomainClass?.propertyName}" action="list"><span class="th">${p.naturalName}</span></g:link>
                   	        </th>
                   	    <%          } else { %>
                   	        <g:sortableColumn property="${p.name}" title="${p.naturalName}" />
                        <%  }   }   } %>
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="\${${propertyName}List}" status="i" var="${propertyName}">
                        <tr class="\${(i % 2) == 0 ? 'odd' : 'even'}">
                        <%  props.eachWithIndex { p,i ->
                                if(i == 0) { %>
                            <td>
                               <g:link action="show" id="\${${propertyName}.id}">\${fieldValue(bean:${propertyName}, field:'${p.name}')}</g:link>
                               <g:link action="edit" id="\${${propertyName}.id}">E</g:link>
                             </td>
                        <%      } else if(i < COLS) { %>
                            <td>
                            <% if (p.isAssociation() && !p.isOneToMany()) { %>
                                <g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${propertyName}?.${p.name}?.id}">\${fieldValue(bean:${propertyName}, field:'${p.name}')}</g:link>
                            <% } else { 
                                def cp = domainClass.constrainedProperties[p.name]
                                if (cp?.password) {
                            %> \${fieldValue(bean:${propertyName}, field:'${p.name}')?.replaceAll(/./,'*')} <%
                                } else {
                            %>
                                \${fieldValue(bean:${propertyName}, field:'${p.name}')}
                            <%  } } %>
                            </td>
                        <%  }   } %>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="\${${propertyName}Total}" />
            </div>
        </div>
    </body>
</html>
