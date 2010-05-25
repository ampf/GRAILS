/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

/**
 *  Set up the grails doc and the user manual
 */
grails {
    doc {
        copyright = "Copyright Appscio, Inc (c) 2010"
        license = "Appscio License-dual (LGPLv2/Apache Lic v2, your choice)"
        authors = "Wayne Stidolph"
        footer = "DRAFT"
        alias.intro  ="1. Introduction"
        alias.domain ="2. Domain Overview"
        api.org.hibernate="http://www.hibernate.org/hib_docs/v3/api"
        packagenames="com.appscio.*,com.appscio.**,com.appscio.mpf.utility, com.appscio.mpf.grails.*,com.appscio.mpf.grails.core"
    }
}

// The following properties have been added by the Upgrade process...
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
