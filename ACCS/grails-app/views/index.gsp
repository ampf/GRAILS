<html>
    <head>
        <title>Welcome to Appscio Compute Cloud Scheduler (ACCS)</title>
		<meta name="layout" content="main" />
    </head>
    <body>
        <h1 style="margin-left:20px;">Welcome to Appscio Compute Cloud Scheduler (ACCS -- pronounced AXE)</h1>
        <div class="body" style="margin-left:20px; width:80%;">
        You can use this application to create pools of AMI nodes on the Amazon EC2 cloud, schedule nodes in a pool, monitor them using JMX, and
        monitor SQS queues associated with them.
        <br/>
        <br/>
        <table>
          <tr>
            <th>Pools</th>
            <th>Nodes</th>
            <th>Scheduling</th>
            <th>Accounts</th>
            <th>JMX</th>
            <th>SQS</th>
          </tr>
          <tr>
            <td>
              <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                <% if (c.name.contains("Pool") && !c.name.contains("JMX") && !c.name.contains("Account"))  { %>
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","").replace("Compute","")}</g:link></li>
                <% } %>
              </g:each>
              </ul>
            </td>
            <td>
              <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                <% if (c.name.contains("Node") && !c.name.contains("JMX") && !c.name.contains("Account"))  { %>
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","").replace("Compute","")}</g:link></li>
                <% } %>
              </g:each>
              </ul>
            </td>
            <td>
              <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                <% if (c.name.contains("Schedule") || c.name.contains("Weekly"))  { %>
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","").replace("Compute","")}</g:link></li>
                <% } %>
              </g:each>
              </ul>
            </td>
            <td>
              <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                <% if (c.name.contains("Account"))  { %>
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","").replace("Compute","")}</g:link></li>
                <% } %>
              </g:each>
              </ul>
            </td>
            <td>
              <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                <% if (c.name.contains("JMX"))  { %>
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","").replace("Compute","")}</g:link></li>
                <% } %>
              </g:each>
              </ul>
            </td>
            <td>
              <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                <% if (c.name.contains("SQS"))  { %>
                <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","").replace("Compute","")}</g:link></li>
                <% } %>
              </g:each>
              </ul>
            </td>
          </tr>
        </table>
        <h1>Quick Start</h1>
        <div class="dialog" style="margin-left:20px;width:60%;">
        <ul>
            <li>
            Create a pool <g:link controller="computePoolAccount">Account</g:link> with your Amazon EC2 credentials.
            </li>
            <li>
            Select the <g:link controller="activeAccount">Active Account</g:link> to use when creating Pools
            </li>
            <li>
            Create a <g:link controller="computePool">Pool</g:link>, assign either a fixed number of nodes to it, or enable scheduling and assign a <g:link controller="computeWeekly">Weekly</g:link> schedule
            </li>
            <li>
            Monitor the aggregate nodes in your <g:link controller="computePoolJMX">Pool</g:link> using JMX, or monitor individual <g:link controller="computeNodeJMX">Nodes</g:link>
            </li>
            <li>
            Manage the <g:link controller="computeSQS">SQS</g:link> (Simple Queue Services) in your account
            </li>
            <li>
            Advanced Feature: enlist <g:link controller="computeNode">Nodes</g:link> into a pool.  This can be used to
            build a pool out of instances which were not started using this manager. Make sure not to enlist a node
            into more than one pool.
            <br/>Note: you should probably create a new <g:link controller="computePool">Pool</g:link> to hold these nodes to avoid confusion.
            <br/>Note: when creating a new pool, make sure the active account is the one which contains the node.
            </li>
        </ul>
        <br/>Note: the "E" symbol in the list views let you edit records.
        </div>
        <br/>
        <h1>All Application Controllers (including those above)</h1>
        <div class="dialog" style="margin-left:20px;width:60%;">
            <ul>
              <g:each var="c" in="${grailsApplication.controllerClasses.sort{it.toString()}}">
                    <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.name.replace("Controller","")}</g:link></li>
              </g:each>
            </ul>
        </div>
        </div>

    </body>
</html>