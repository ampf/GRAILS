def useP6Spy = false // use this to enable p6spy logging
dataSource {
    pooled = true
    driverClassName = (useP6Spy) ? "com.p6spy.engine.spy.P6SpyDriver" : "org.postgis.DriverWrapper"
    username = "postgres"
    password = "postgres"
    dialect = "PostGISDialect"
    //loggingSql = true
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
    jdbc.batch_size = 20
}

// environment specific settings
environments {
    development {
        dataSource {
            pooled = true
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
            dialect = ""
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:mem:devDB"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql_postGIS:test"
        }
    }
    testwayne {
        dataSource {
        dbCreate = "update"
        url = "jdbc:postgresql_postGIS:wayne-test"
        /* use this block for in-memory testing
         pooled = true
         driverClassName = "org.hsqldb.jdbcDriver"
         username = "sa"
         password = ""
         dbCreate = "create-drop" // one of 'create', 'create-drop','update'
         url = "jdbc:hsqldb:mem:devDB"
         */
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql_postGIS:prod"
        }
    }
}
