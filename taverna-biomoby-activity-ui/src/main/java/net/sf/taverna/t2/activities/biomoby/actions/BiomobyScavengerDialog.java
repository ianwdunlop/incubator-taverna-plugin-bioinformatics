package net.sf.taverna.t2.activities.biomoby.actions;
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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.biomoby.registry.meta.Registries;
import org.biomoby.registry.meta.RegistriesList;
import org.biomoby.shared.MobyException;

import net.sf.taverna.t2.lang.ui.ShadedLabel;

/**
 * a dialog for helping create scavengers for BioMoby registries that are not
 * the default registry.
 *
 */
public class BiomobyScavengerDialog extends JPanel {

	private static final String CUSTOM = "Custom";
	private static final long serialVersionUID = -57047613557546674L;
	private JTextField registryEndpoint = new JTextField(
			"http://moby.ucalgary.ca/moby/MOBY-Central.pl");
	private JTextField registryURI = new JTextField(
			"http://moby.ucalgary.ca/MOBY/Central");

	/**
	 * Default constructor.
	 *
	 */
	/**
	 * Default constructor.
	 *
	 */
	public BiomobyScavengerDialog() {
		super();
		GridLayout layout = new GridLayout(5, 2);
		setLayout(layout);

		registryEndpoint.setEnabled(false);
		registryURI.setEnabled(false);

		// a combo box showing known registries
		final Registries regs = RegistriesList.getInstance();
		List<String> choices = new ArrayList<String>(Arrays.asList(regs.list()));
		choices.add(CUSTOM);

		JComboBox regList = new JComboBox(choices.toArray());
		regList.setToolTipText("A selection will fill text fields below");
		regList.setSelectedItem(Registries.DEFAULT_REGISTRY_SYNONYM);
		regList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String contents = (String) ((JComboBox) e.getSource())
						.getSelectedItem();

				if (contents.equals(CUSTOM)) {
					registryEndpoint.setEnabled(true);
					registryURI.setEnabled(true);
					return;
				} else {
					registryEndpoint.setEnabled(false);
					registryURI.setEnabled(false);
				}
 				org.biomoby.registry.meta.Registry theReg = null;
				try {
					theReg = regs.get(contents);
				} catch (MobyException ee) {
					try {
						theReg = regs.get(null);
					} catch (MobyException ee2) {

					}
				}
				if (theReg != null) {
					registryEndpoint.setText(theReg.getEndpoint());
					registryURI.setText(theReg.getNamespace());

				}
			}
		});
		add(new ShadedLabel("Choose a registry from the list: ",
				ShadedLabel.BLUE, true));
		add(regList);
		add(new ShadedLabel("Or select '" + CUSTOM + "' to enter your own below,", ShadedLabel.BLUE, true));
		add(new ShadedLabel("", ShadedLabel.BLUE, true));
		add(new ShadedLabel(
				"Location (URL) of your BioMoby central registry: ",
				ShadedLabel.BLUE, true));
		registryEndpoint
				.setToolTipText("BioMoby Services will be retrieved from the endpoint that you specify here!");
		add(registryEndpoint);
		add(new ShadedLabel(
				"Namespace (URI) of your BioMoby central registry: ",
				ShadedLabel.BLUE, true));
		registryURI
				.setToolTipText("BioMoby Services will be retrieved from the endpoint/URI that you specify here!");
		add(registryURI);
		// add(Box.createHorizontalGlue());add(Box.createHorizontalGlue());
		setPreferredSize(this.getPreferredSize());
		setMinimumSize(this.getPreferredSize());
		setMaximumSize(this.getPreferredSize());

	}

	/**
	 *
	 * @return the string representation of the BioMoby Registry endpoint
	 */
	public String getRegistryEndpoint() {
		return registryEndpoint.getText();
	}

	/**
	 *
	 * @return the string representation of the BioMoby Registry endpoint
	 */
	public String getRegistryURI() {
		return registryURI.getText();
	}
}
