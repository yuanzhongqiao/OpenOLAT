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
package org.olat.modules.ceditor.model;

/**
 * 
 * Initial date: 19 sept. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TableSettings {
	
	private boolean rowHeaders;
	private boolean columnHeaders;
	
	private boolean striped;
	private boolean bordered;
	
	private String tableStyle;

	private BlockLayoutSettings layoutSettings;

	public boolean isRowHeaders() {
		return rowHeaders;
	}
	
	public void setRowHeaders(boolean rowHeaders) {
		this.rowHeaders = rowHeaders;
	}
	
	public boolean isColumnHeaders() {
		return columnHeaders;
	}
	
	public void setColumnHeaders(boolean columnHeaders) {
		this.columnHeaders = columnHeaders;
	}

	public boolean isStriped() {
		return striped;
	}

	public void setStriped(boolean striped) {
		this.striped = striped;
	}

	public boolean isBordered() {
		return bordered;
	}

	public void setBordered(boolean bordered) {
		this.bordered = bordered;
	}

	public String getTableStyle() {
		return tableStyle;
	}

	public void setTableStyle(String tableStyle) {
		this.tableStyle = tableStyle;
	}

	public BlockLayoutSettings getLayoutSettings() {
		return layoutSettings;
	}

	public void setLayoutSettings(BlockLayoutSettings layoutSettings) {
		this.layoutSettings = layoutSettings;
	}
}
