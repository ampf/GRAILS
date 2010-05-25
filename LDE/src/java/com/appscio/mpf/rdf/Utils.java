/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 *******************************************************************************/
package com.appscio.mpf.rdf;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// RDF: <subject,predicate,object> where object can be a literal, or another subject.
// Map this onto a data model:
//
//   Map<subject, Map<predicate, object>>
//
// If an object is a subject which has not yet been defined, add an empty entry in the map, then check the
// map for existing subject before adding a new one.
//
// Split predicates into hashes based on "/" delimiter to allow freemarker to traverse them using dotted notation, so
// predicate/object a/b/c = val becomes {a:{b:{c:val}} which support the notation ${a.b.c} to get to val.
//
// root
//   + rdf
//       Map<subject, Map<predicate, object>>
//   + appscio
//       Map<subject, Map<Map<...<Map<predicate, object>>>>> where predicate is split into maps by "/"
//       Supports short-form access to predicates e.g. ${appscio.track0.id}
//   + subjects
//       List of subject hashes based on type.  So subjects[0] will be a subject (e.g track, obs, etc).

public class Utils {

    public static Map<String, Object> parseN3(BufferedReader br) throws IOException {
        String line = br.readLine();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Map<String, Map<?,?>> amap = new HashMap<String, Map<?,?>>();
        while (line != null) {
            String split[] = line.split(" ");
            if (split.length == 3) {
	            // Ignore subject, put predicate and object into rdf.
	            String subj = split[0];
	            String pred = split[1].substring(1, split[1].length()-1);
	            String obj;
	            if (split[2].startsWith("_")) {
	                obj = split[2].substring(0, split[2].length()-1);
	            } else {
	                obj = split[2].substring(1, split[2].length()-2);
	            }
	            Map<String, Object> s = map.get(subj);
	            if (s == null) {
	                s = new HashMap<String, Object>();
	                map.put(subj, s);
	            }
	            if (obj.startsWith("_:")) {
	                // Object is a subject, either reuse or create a new one.
	                Map<String, Object> po = map.get(obj);
	                if (po == null) {
	                    po = new HashMap<String, Object>();
	                    map.put(obj, po);
	                }
	            }
	            s.put(pred, obj);

	            // unchecked ok
	            Map<String, Object> appscio = (Map<String, Object>)s.get("appscio");
	            if (appscio == null) {
	                appscio = new HashMap<String, Object>();
	                appscio.put("_subj", subj);
	                s.put("appscio", appscio);
	                amap.put(subj.substring(2), appscio);
	            }

	            predicateToMap(appscio, pred, obj);
            } else {
            	System.out.println("Badly formed N3: '" + line + "' ignored");
            }
            // System.out.println("<" + subj + "," + pred + "," + obj + ">");
            line = br.readLine();
        }
        // Invert
        Map<String, List<Map>> subjects = new HashMap<String, List<Map>>();
        for (String key: amap.keySet()) {
            Map<?,?> m = amap.get(key);
            String k = (String)m.keySet().toArray()[0];
            // System.out.println(m);
            List<Map> l = subjects.get(k);
            if (l == null) {
                l = new ArrayList<Map>();
                subjects.put(k, l);
            }
            l.add(amap.get(key));
        }
        for (String subj: map.keySet()) {
            for (String pred: map.get(subj).keySet()) {
                Object obj = map.get(subj).get(pred);
                // System.out.println("<" + subj + ", " + pred + ", " + obj + ">");
            }
        }

        Map<String, Object> root = new HashMap<String,Object>();
        root.put("rdf", map);
        root.put("appscio", amap);
        root.put("subjects", subjects);
        return root;
    }

    // Splits a slash separated predicate into a map for easier access in freemarker.
    public static void predicateToMap(Map<String, Object> appscio, String pred, String obj) {
        String split[] = pred.split("/");
        Map<String, Object> m = appscio;
        int i = 0;
        for (String s: split) {
            if (i > 1) {
                if (i == split.length-1) {
                    m.put(s, obj);
                } else {
                    if (m.get(s) == null) {
                        m.put(s, new HashMap<String, Object>());
                    }
                    // unchecked ok.
                    m = (Map<String, Object>)m.get(s);
                }
            }
            i++;
        }
    }

}
