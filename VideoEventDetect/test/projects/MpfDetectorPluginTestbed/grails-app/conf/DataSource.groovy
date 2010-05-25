def useP6Spy = false // use this to enable p6spy logging
dataSource {
    pooled = true
    driverClassName = (useP6Spy) ? "com.p6spy.engine.spy.P6SpyDriver" : "org.postgis.DriverWrapper"
    username = "postgres"
    password = "postgres"
    // dialect = "PostGISDialect"
    // dialect = "org.ossim.postgis.PostGISDialectNG"
    dialect = "org.ossim.postgis.PostGISDialect"
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
    development_in_memory {
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
    development {
        def db = "local"
        if (db == "local") {
            dataSource {
                dbCreate = "update"
                driverClassName = "org.hsqldb.jdbcDriver"
                username = "sa"
                password = ""
                dialect = ""
                url = "jdbc:hsqldb:file:hsqlProdDB"
                // logSql = true
            }
        } else {
            dataSource {
                dbCreate = "update"
                driverClassName = "com.mysql.jdbc.Driver"
                username = "mpp"
                password = "mppProd"
                dialect =  "org.hibernate.dialect.MySQL5InnoDBDialect"
                if (db == "gpubox") {
                    url = "jdbc:mysql://gpubox/mpp_test_prod"
                } else {
                    url = "jdbc:mysql://percival/mpp_test_prod"
                }
            }
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop"
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
            dialect = ""
            url = "jdbc:hsqldb:file:hsqlTestDB"
//            url = "jdbc:postgresql_postGIS:test"
        }
    }
    testwayne {
        dataSource {
            dbCreate = "create-drop" //"create-drop" //"update"
            //url = "jdbc:postgresql_postGIS:wayne-test"
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:file:hsqlTestWayneDB"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
            dialect = ""
            url = "jdbc:hsqldb:file:hsqlProdDB"
        }
    }
}
