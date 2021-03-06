package org.biomart.martservice.config.ui;
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * A component that when collapsed only shows a title and when expanded shows
 * the title and contained components.
 *
 * @author David Withers
 *
 */
public class ExpandableBox extends JPanel {
	private static final long serialVersionUID = -5678399632577606442L;

	private JButton expandButton;

	private JPanel labelBox;

	private boolean expanded = true;

	private boolean animated = false;

	private Timer timer = new Timer(1, null);

	private Dimension minSize;

	private Dimension maxSize;

	private int height;

	private final int increment = 10;

	private ActionListener openAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (height <= maxSize.height) {
				setPreferredSize(new Dimension(maxSize.width, height));
				revalidate();
				repaint();
				height += increment;
			} else {
				timer.removeActionListener(this);
				timer.stop();
				setPreferredSize(new Dimension(maxSize.width, maxSize.height));
				revalidate();
				repaint();
			}
		}
	};

	private ActionListener closeAction = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (height >= minSize.height) {
				setPreferredSize(new Dimension(minSize.width, height));
				revalidate();
				repaint();
				height -= increment;
			} else {
				timer.removeActionListener(this);
				timer.stop();
				height = minSize.height;
				setPreferredSize(new Dimension(minSize.width, height));
				revalidate();
				repaint();
			}
		}
	};

	public ExpandableBox(Component titleComponent, Color backgroundColor,
			Color borderColor) {
		this(titleComponent, backgroundColor, borderColor, new Insets(10, 10,
				10, 10), false);
	}

	public ExpandableBox(Component titleComponent, Color backgroundColor,
			Color borderColor, Insets borderInsets) {
		this(titleComponent, backgroundColor, borderColor, borderInsets, false);
	}

	public ExpandableBox(Component titleComponent, Color backgroundColor,
			Color borderColor, boolean animated) {
		this(titleComponent, backgroundColor, borderColor, new Insets(10, 10,
				10, 10), animated);
	}

	public ExpandableBox(Component titleComponent, Color backgroundColor,
			Color borderColor, Insets borderInsets, boolean animated) {
		this.animated = animated;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(backgroundColor);
		setBorder(new CompoundBorder(new LineBorder(borderColor, 1),
				new EmptyBorder(borderInsets)));

		labelBox = new JPanel();
		labelBox.setLayout(new BoxLayout(labelBox, BoxLayout.X_AXIS));
		labelBox.setBackground(backgroundColor);

		expandButton = new JButton(MartServiceIcons.getIcon("contract"));
		expandButton.setActionCommand("contract");
		expandButton.setBackground(backgroundColor);
		expandButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		expandButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if ("contract".equals(e.getActionCommand())) {
					setExpanded(false);
				} else {
					setExpanded(true);
				}
			}

		});
		labelBox.add(expandButton);
		labelBox.add(Box.createHorizontalStrut(5));
		labelBox.add(titleComponent);
		labelBox.add(Box.createHorizontalGlue());
		add(labelBox);
		minSize = getPreferredSize();
	}

	public void setExpanded(boolean expanded) {
		if (maxSize == null || maxSize.height <= minSize.height) {
			maxSize = getLayout().preferredLayoutSize(this);
		}
		if (this.expanded != expanded) {
			this.expanded = expanded;
			if (expanded) {
				expandButton.setIcon(MartServiceIcons.getIcon("contract"));
				expandButton.setActionCommand("contract");
				if (animated) {
					timer.stop();
					timer.removeActionListener(closeAction);
					timer.addActionListener(openAction);
					timer.start();
				} else {
					setPreferredSize(new Dimension(maxSize.width,
							maxSize.height));
				}
			} else {
				expandButton.setIcon(MartServiceIcons.getIcon("expand"));
				expandButton.setActionCommand("expand");
				if (animated) {
					timer.stop();
					timer.removeActionListener(openAction);
					timer.addActionListener(closeAction);
					timer.start();
				} else {
					setPreferredSize(new Dimension(minSize.width,
							minSize.height));
				}
			}
			revalidate();
			repaint();
		}
		expandButton.setSelected(expanded);
	}

}
