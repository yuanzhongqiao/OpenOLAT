/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.forms.model.xml;

import org.olat.modules.ceditor.model.BlockLayoutSettings;

/**
 * 
 * Initial date: 02.02.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class FileUpload extends AbstractElement {

	private static final long serialVersionUID = 7057962558556618266L;
	
	public static final String TYPE = "formfileupload";

	private boolean mandatory;
	private long maxUploadSizeKB;
	private String mimeTypeSetKey;
	private BlockLayoutSettings layoutSettings;

	@Override
	public String getType() {
		return TYPE;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Long getMaxUploadSizeKB() {
		return maxUploadSizeKB;
	}

	public void setMaxUploadSizeKB(long maxUploadSizeKB) {
		this.maxUploadSizeKB = maxUploadSizeKB;
	}

	public String getMimeTypeSetKey() {
		return mimeTypeSetKey;
	}

	public void setMimeTypeSetKey(String mimeTypeSetKey) {
		this.mimeTypeSetKey = mimeTypeSetKey;
	}

	public BlockLayoutSettings getLayoutSettings() {
		return layoutSettings;
	}

	public void setLayoutSettings(BlockLayoutSettings layoutSettings) {
		this.layoutSettings = layoutSettings;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj instanceof FileUpload) {
			FileUpload fileUpload = (FileUpload)obj;
			return getId() != null && getId().equals(fileUpload.getId());
		}
		return super.equals(obj);
	}

}
