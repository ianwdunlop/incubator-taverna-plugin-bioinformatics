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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class BioMobyObjectTreeCustomRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    private Color leafForeground = Color.blue;

    private Color rootColor = Color.black;

    @SuppressWarnings("unused")
	private Color feedsIntoColor = Color.gray;

    @SuppressWarnings("unused")
	private Color producedColor = Color.lightGray;

    @SuppressWarnings("unused")
	private Color authorityColor = Color.orange;

    private Color serviceColor = Color.magenta;

    private Color objectColor = Color.green;

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        // Allow the original renderer to set up the label
        Component c = super.getTreeCellRendererComponent(tree, value, selected,
                expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof MobyServiceTreeNode) {
                // service node
                c.setForeground(serviceColor);
                ((JComponent) c).setToolTipText(((MobyServiceTreeNode) node
                        .getUserObject()).getDescription());
                setIcon(MobyPanel.getIcon("/service.png"));
            } else if (node.getUserObject() instanceof MobyObjectTreeNode) {
                // object node
                c.setForeground(objectColor);
                ((JComponent) c).setToolTipText(((MobyObjectTreeNode) node
                        .getUserObject()).getDescription());
            } else if (node.isRoot()) {
                // root node
                setIcon(MobyPanel.getIcon("/moby.png"));
                ((JComponent) c).setToolTipText(" Description of "
                        + node.getUserObject());
                c.setForeground(rootColor);
            } else if (node.getUserObject() instanceof String) {
                // check for feeds into and produced by nodes
                String string = (String) node.getUserObject();
                if (string.equalsIgnoreCase("feeds into")) {
                    setIcon(MobyPanel.getIcon("/input.png"));
                    ((JComponent) c).setToolTipText(null);
                } else if (string.equalsIgnoreCase("produced by")) {
                    setIcon(MobyPanel.getIcon("/output.png"));
                    ((JComponent) c).setToolTipText(null);
                } else if (string.equalsIgnoreCase("produces")) {
                    ((JComponent) c).setToolTipText(null);
                } else {

                    ((JComponent) c).setToolTipText(null);

                    if (!leaf) {
                        if (string.startsWith("Collection('")) {
                            setIcon(MobyPanel.getIcon("/collection.png"));
                        } else {
                            setIcon(MobyPanel.getIcon("/authority.png"));
                        }
                    }
                }

            } else {
                ((JComponent) c).setToolTipText("nothing node");
            }
        }
        if (selected)
            c.setBackground(Color.lightGray);
        if (leaf)
            c.setForeground(this.leafForeground);
        return c;
    }
}
