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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.activities.testutils.ActivityInvoker;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for SoaplabActivity.
 *
 * @author David Withers
 */
public class SoaplabActivityTest {

	private SoaplabActivity activity;

	private ObjectNode configurationBean;

	@Ignore("Integration test")
	@Before
	public void setUp() throws Exception {
		activity = new SoaplabActivity();
		configurationBean = JsonNodeFactory.instance.objectNode();
		configurationBean.put("endpoint", "http://www.ebi.ac.uk/soaplab/emboss4/services/utils_misc.embossversion");
	}

	@Ignore("Integration test")
	@Test
	public void testExecuteAsynch() throws Exception {
		Map<String, Object> inputs = new HashMap<String, Object>();
		// inputs.put("full", "true");
		Map<String, Class<?>> expectedOutputs = new HashMap<String, Class<?>>();
		expectedOutputs.put("report", String.class);
		expectedOutputs.put("outfile", String.class);

		activity.configure(configurationBean);

		Map<String, Object> outputs = ActivityInvoker.invokeAsyncActivity(
				activity, inputs, expectedOutputs);
		assertTrue(outputs.containsKey("report"));
		// assertTrue(outputs.get("report") instanceof String);
		assertTrue(outputs.containsKey("outfile"));
		assertTrue(outputs.get("outfile") instanceof String);
		System.out.println(outputs.get("outfile"));

		// test with polling
		configurationBean.put("pollingInterval", 5);
		configurationBean.put("PollingIntervalMax", 6);
		configurationBean.put("PollingBackoff", 1.2);
		activity.configure(configurationBean);

		outputs = ActivityInvoker.invokeAsyncActivity(activity, inputs,
				expectedOutputs);
		assertTrue(outputs.containsKey("report"));
		assertTrue(outputs.containsKey("outfile"));
	}

	@Ignore("Integration test")
	@Test
	public void testSoaplabActivity() {
		assertNotNull(new SoaplabActivity());
	}

	@Ignore("Integration test")
	@Test
	public void testConfigureSoaplabActivityConfigurationBean()
			throws Exception {
		Set<String> expectedOutputs = new HashSet<String>();
		expectedOutputs.add("report");
		expectedOutputs.add("outfile");

		activity.configure(configurationBean);
		Set<ActivityOutputPort> ports = activity.getOutputPorts();
		assertEquals(expectedOutputs.size(), ports.size());
		for (ActivityOutputPort outputPort : ports) {
			assertTrue("Wrong output : " + outputPort.getName(),
					expectedOutputs.remove(outputPort.getName()));
		}
	}

	@Ignore("Integration test")
	@Test
	public void testIsPollingDefined() throws Exception {
		assertFalse(activity.isPollingDefined());
		activity.configure(configurationBean);
		assertFalse(activity.isPollingDefined());
		configurationBean.put("pollingInterval", 1000);
		activity.configure(configurationBean);
		assertTrue(activity.isPollingDefined());
	}

}
