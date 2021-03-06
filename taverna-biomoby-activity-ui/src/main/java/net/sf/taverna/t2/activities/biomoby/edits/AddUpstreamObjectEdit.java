package net.sf.taverna.t2.activities.biomoby.edits;
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

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.activities.biomoby.BiomobyObjectActivity;
import net.sf.taverna.t2.activities.biomoby.BiomobyObjectActivityConfigurationBean;
import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.InputPort;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.impl.AbstractDataflowEdit;
import net.sf.taverna.t2.workflowmodel.impl.DataflowImpl;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.utils.Tools;

import org.apache.log4j.Logger;

/**
 * @author Stuart Owen
 *
 */
public class AddUpstreamObjectEdit extends AbstractDataflowEdit {

	private static Logger logger = Logger.getLogger(AddUpstreamObjectEdit.class);

	private final Processor sinkProcessor;
	private final BiomobyObjectActivity activity;
	private Edits edits;

	private List<Edit<?>> subEdits = new ArrayList<Edit<?>>();

	/**
	 * @param dataflow
	 */
	public AddUpstreamObjectEdit(Dataflow dataflow, Processor sinkProcessor,
			BiomobyObjectActivity activity, Edits edits) {
		super(dataflow);
		this.sinkProcessor = sinkProcessor;
		this.activity = activity;
		this.edits = edits;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sf.taverna.t2.workflowmodel.impl.AbstractDataflowEdit#doEditAction
	 * (net.sf.taverna.t2.workflowmodel.impl.DataflowImpl)
	 */
	@Override
	protected void doEditAction(DataflowImpl dataflow) throws EditException {
		subEdits.clear();

		for (InputPort inputPort : activity.getInputPorts()) {
			// ignore article name, id, namespace, value
			if (inputPort.getName().equals("namespace")
					|| inputPort.getName().equals("id")
					|| inputPort.getName().equals("article name")
					|| inputPort.getName().equals("value")) {
				continue;
			}
			List<Edit<?>> editList = new ArrayList<Edit<?>>();
			String defaultName = inputPort.getName().split("\\(")[0];

			String name = Tools
					.uniqueProcessorName(inputPort.getName(), dataflow);

			BiomobyObjectActivityConfigurationBean configBean = new BiomobyObjectActivityConfigurationBean();
			configBean.setMobyEndpoint(activity.getConfiguration()
					.getMobyEndpoint());
			configBean.setAuthorityName("");
			configBean.setServiceName(defaultName);

			net.sf.taverna.t2.workflowmodel.Processor sourceProcessor = edits
					.createProcessor(name);
			BiomobyObjectActivity boActivity = new BiomobyObjectActivity();
			Edit<?> configureActivityEdit = edits.getConfigureActivityEdit(
					boActivity, configBean);
			editList.add(configureActivityEdit);

			editList.add(edits.getDefaultDispatchStackEdit(sourceProcessor));

			Edit<?> addActivityToProcessorEdit = edits.getAddActivityEdit(
					sourceProcessor, boActivity);
			editList.add(addActivityToProcessorEdit);



			editList.add(edits.getAddProcessorEdit(dataflow, sourceProcessor));

			CompoundEdit compoundEdit = new CompoundEdit(editList);
			subEdits.add(compoundEdit);
			compoundEdit.doEdit();


			List<Edit<?>> linkEditList = new ArrayList<Edit<?>>();

			EventForwardingOutputPort sourcePort = getSourcePort(
					sourceProcessor, boActivity, "mobyData", linkEditList);
			EventHandlingInputPort sinkPort = getSinkPort(sinkProcessor, activity, inputPort.getName(), linkEditList);
			linkEditList.add(Tools.getCreateAndConnectDatalinkEdit(dataflow,
					sourcePort, sinkPort, edits));
			CompoundEdit linkEdit = new CompoundEdit(linkEditList);
			subEdits.add(linkEdit);
			linkEdit.doEdit();

			if (!(defaultName.equalsIgnoreCase("Object")
					|| name.equalsIgnoreCase("String")
					|| name.equalsIgnoreCase("Integer") || name
					.equalsIgnoreCase("DateTime"))) {
				Edit upstreamObjectEdit = new AddUpstreamObjectEdit(dataflow,
						sourceProcessor, boActivity, edits);
				subEdits.add(upstreamObjectEdit);
				upstreamObjectEdit.doEdit();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sf.taverna.t2.workflowmodel.impl.AbstractDataflowEdit#undoEditAction
	 * (net.sf.taverna.t2.workflowmodel.impl.DataflowImpl)
	 */
	@Override
	protected void undoEditAction(DataflowImpl dataflow) {
		if (subEdits != null && subEdits.size() > 0) {
			for (int i = subEdits.size() - 1; i >= 0; i--) {
				Edit<?> edit = subEdits.get(i);
				if (edit.isApplied())
					edit.undo();
			}
		}


	}

	private EventHandlingInputPort getSinkPort(
			net.sf.taverna.t2.workflowmodel.Processor processor,
			Activity<?> activity, String portName, List<Edit<?>> editList) {
		InputPort activityPort = net.sf.taverna.t2.workflowmodel.utils.Tools
				.getActivityInputPort(activity, portName);
		// check if processor port exists
		EventHandlingInputPort input = net.sf.taverna.t2.workflowmodel.utils.Tools
				.getProcessorInputPort(processor, activity, activityPort);
		if (input == null) {
			// port doesn't exist so create a processor port and map it
			ProcessorInputPort processorInputPort = edits
					.createProcessorInputPort(processor,
							activityPort.getName(), activityPort.getDepth());
			editList.add(edits.getAddProcessorInputPortEdit(processor,
					processorInputPort));
			editList.add(edits.getAddActivityInputPortMappingEdit(activity,
					activityPort.getName(), activityPort.getName()));
			input = processorInputPort;
		}
		return input;
	}

	private EventForwardingOutputPort getSourcePort(
			net.sf.taverna.t2.workflowmodel.Processor processor,
			Activity<?> activity, String portName, List<Edit<?>> editList) {
		OutputPort activityPort = net.sf.taverna.t2.workflowmodel.utils.Tools
				.getActivityOutputPort(activity, portName);
		// check if processor port exists
		EventForwardingOutputPort output = net.sf.taverna.t2.workflowmodel.utils.Tools
				.getProcessorOutputPort(processor, activity, activityPort);
		if (output == null) {
			// port doesn't exist so create a processor port and map it
			ProcessorOutputPort processorOutputPort = edits
					.createProcessorOutputPort(processor, activityPort
							.getName(), activityPort.getDepth(), activityPort
							.getGranularDepth());
			editList.add(edits.getAddProcessorOutputPortEdit(processor,
					processorOutputPort));
			editList.add(edits.getAddActivityOutputPortMappingEdit(activity,
					activityPort.getName(), activityPort.getName()));
			output = processorOutputPort;
		}
		return output;
	}

}
