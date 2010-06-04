/*******************************************************************************
 * Copyright (c) 2010 Appscio Dual License. All rights reserved. This program and the accompanying materials are made available under your choice of the GNU Lesser Public License v2.1 or the Apache License, Version 2.0. See the included License-dual file.
 ******************************************************************************/
import org.codehaus.groovy.grails.commons.ApplicationHolder;

// Place your Spring DSL code here
beans = {
  def config = ApplicationHolder.application.config

  if (config.cloud.provider == 'AMAZON') {
      schedulingService(AmazonEC2SchedulingService) {timer = RealtimeQuartzJob}
        providerService(AmazonEC2ProviderService)
  } else {
        schedulingService(AmazonEC2SchedulingService) {timer = MockQuartzJob}
        providerService(MockProviderService)

  }
}

