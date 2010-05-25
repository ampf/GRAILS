/*******************************************************************************
 * Copyright (c) 2010 Appscio, Inc. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or  the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/

import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.springframework.core.io.Resource

includeTargets << grailsScript("Init")

target(main: "Creates a new MPF Detector definition.") {
  typeName = "MpfDetector"
  artifactName = "MpfDetector"
  artifactPath = "grails-app/mpfDetectors"

  createArtifact()
}

target(createSupport: "Creates a runtime support directory tree"){
    depends(main)

/* this first pasrt is copid from the createArtifact task - surely there's a way to runesue?? */
    // Extract the package name if one is given.
    def name = args
    def pkg = null
    def pos = args.lastIndexOf('.')
    if (pos != -1) {
        pkg = name[0..<pos]
        name = name[(pos + 1)..-1]
    }

    // Convert the package into a file path.
    def pkgPath = ''
    if (pkg) {
        pkgPath = pkg.replace('.' as char, '/' as char)

        // Make sure that the package path exists! Otherwise we won't
        // be able to create a file there.
        Ant.mkdir(dir: "${basedir}/${artifactPath}/${pkgPath}")

        // Future use of 'pkgPath' requires a trailing slash.
        pkgPath += '/'
    }

    // Convert the given name into class name and property name
    // representations.
    className = GCU.getClassNameRepresentation(name)
    propertyName = GCU.getPropertyNameRepresentation(name)



    // create the runtime support directory structure

    def runtime_dir = "${basedir}/${artifactPath}/${pkgPath}${className}${typeName}_runtime"
    Ant.mkdir(dir:runtime_dir + "/scripts")
    Ant.mkdir(dir:runtime_dir + "/jars")
    Ant.mkdir(dir:runtime_dir + "/plugins")
    Ant.mkdir(dir:runtime_dir + "/lib")

    // now we make the two templated files in scripts/
    // scripts/detect.sh is a simple detector example
    // scripts/test.sh is a simple test-driver

/* detect.sh */
    // first check for presence of template in application
    templateFile = "${basedir}/src/templates/artifacts/${artifactName}_detect.sh"
    if (!new File(templateFile).exists()) {
        // now check for template provided by plugins
        Resource[] pluginDirs = getPluginDirectories()
        List pluginTemplateFiles = []
        pluginDirs.each {
            File template = new File(it.file, "src/templates/artifacts/${artifactName}_detect.sh")
            if (template.exists()) {
                pluginTemplateFiles << template
            }
        }

        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0].path
        } else {
            // template not found in application, maybe they put it under grails itself? Gotta hope ...
            templateFile = "${grailsHome}/src/grails/templates/artifacts/${artifactName}_detect.sh"
        }
    }

    artifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${typeName}_runtime/scripts/detect.sh"

    Ant.copy(file: templateFile, tofile: artifactFile, overwrite: true)
    Ant.replace(file: artifactFile,
            token: "@artifact.name@", value: "${className}${typeName}")
    if (pkg) {
        Ant.replace(file: artifactFile, token: "@artifact.package@", value: "package ${pkg}\n\n")
    }
    else {
        Ant.replace(file: artifactFile, token: "@artifact.package@", value: "")
    }

    ant.chmod(file: artifactFile, perm:"744")
    event("CreatedFile", [artifactFile])

/* test.sh */

    templateFile = "${basedir}/src/templates/artifacts/${artifactName}_test.sh"
    if (!new File(templateFile).exists()) {
        // now check for template provided by plugins
        Resource[] pluginDirs = getPluginDirectories()
        List pluginTemplateFiles = []
        pluginDirs.each {
            File template = new File(it.file, "src/templates/artifacts/${artifactName}_test.sh")
            if (template.exists()) {
                pluginTemplateFiles << template
            }
        }

        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0].path
        } else {
            // template not found in application, use default template
            templateFile = "${grailsHome}/src/grails/templates/artifacts/${artifactName}_test.sh"
        }
    }

    artifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${typeName}_runtime/scripts/test.sh"

    Ant.copy(file: templateFile, tofile: artifactFile, overwrite: true)
    Ant.replace(file: artifactFile,
            token: "@artifact.name@", value: "${className}${typeName}")
    if (pkg) {
        Ant.replace(file: artifactFile, token: "@artifact.package@", value: "package ${pkg}\n\n")
    }
    else {
        Ant.replace(file: artifactFile, token: "@artifact.package@", value: "")
    }

    ant.chmod(file: artifactFile, perm:"744")
    event("CreatedFile", [artifactFile])
}

setDefaultTarget(createSupport)
