package net.sf.taverna.t2.activities.biomoby.servicedescriptions;
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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.biomoby.BiomobyActivity;
import net.sf.taverna.t2.activities.biomoby.BiomobyActivityConfigurationBean;
import net.sf.taverna.t2.activities.biomoby.query.BiomobyActivityIcon;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public class BiomobyServiceDescription extends
		ServiceDescription<BiomobyActivityConfigurationBean> {

	private String authorityName;
	private String category;
	private String emailContact;
	private URI endpoint;

	private URI lsid;

	private URI namespace;
	private String serviceName;
	private String serviceType;
	private URI signatureURI;

	@Override
	public Class<? extends Activity<BiomobyActivityConfigurationBean>> getActivityClass() {
		return BiomobyActivity.class;
	}

	@Override
	public BiomobyActivityConfigurationBean getActivityConfiguration() {
		BiomobyActivityConfigurationBean bean = new BiomobyActivityConfigurationBean();
		bean.setAuthorityName(getAuthorityName());
		bean.setServiceName(getServiceName());
		bean.setMobyEndpoint(getEndpoint().toASCIIString());
		return bean;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public String getCategory() {
		return category;
	}

	public String getEmailContact() {
		return emailContact;
	}

	public URI getEndpoint() {
		return endpoint;
	}

	@Override
	public Icon getIcon() {
		return BiomobyActivityIcon.getBiomobyIcon();
	}

	public URI getLSID() {
		return lsid;
	}

	@Override
	public String getName() {
		return getServiceName();
	}

	public URI getNamespace() {
		return namespace;
	}

	@Override
	public List<String> getPath() {
		return Arrays.asList("Biomoby @ " + getEndpoint(), getServiceType(), getAuthorityName());
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceType() {
		return serviceType;
	}

	public URI getSignatureURI() {
		return signatureURI;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setEmailContact(String emailContact) {
		this.emailContact = emailContact;
	}

	public void setEndpoint(URI endpoint) {
		this.endpoint = endpoint;
	}

	public void setLSID(URI lsid) {
		this.lsid = lsid;
	}

	public void setNamespace(URI namespace) {
		this.namespace = namespace;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public void setSignatureURI(URI signatureURI) {
		this.signatureURI = signatureURI;
	}

	@Override
	protected List<Object> getIdentifyingData() {
		return Arrays.<Object>asList(getNamespace(), getEndpoint(), getAuthorityName(), getServiceName());
	}


}
