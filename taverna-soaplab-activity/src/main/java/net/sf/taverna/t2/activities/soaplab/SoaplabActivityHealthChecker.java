package net.sf.taverna.t2.activities.soaplab;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.health.HealthCheck;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;
import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;

import net.sf.taverna.t2.workflowmodel.health.RemoteHealthChecker;
import net.sf.taverna.t2.workflowmodel.processor.activity.DisabledActivity;

public class SoaplabActivityHealthChecker extends RemoteHealthChecker {

	public boolean canVisit(Object subject) {
		if (subject == null) {
			return false;
		}
		if (subject instanceof SoaplabActivity) {
			return true;
		}
		if (subject instanceof DisabledActivity) {
			return (((DisabledActivity) subject).getActivity() instanceof SoaplabActivity);
		}
		return false;
	}

	public VisitReport visit(Object o, List<Object> ancestors) {
		SoaplabActivityConfigurationBean configuration = null;
		Activity activity = (Activity) o;
		if (activity instanceof SoaplabActivity) {
			configuration = (SoaplabActivityConfigurationBean) activity.getConfiguration();
		} else if (activity instanceof DisabledActivity) {
			configuration = (SoaplabActivityConfigurationBean) ((DisabledActivity) activity).getActivityConfiguration();
		}
		return contactEndpoint(activity, configuration.getEndpoint());
	}
}
