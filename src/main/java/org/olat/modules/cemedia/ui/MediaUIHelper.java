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
package org.olat.modules.cemedia.ui;

import java.util.ArrayList;
import java.util.List;

import org.olat.NewControllerFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.FormUIFactory;
import org.olat.core.gui.components.form.flexible.elements.FileElement;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.StaticTextElement;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.tabbedpane.TabbedPaneItem;
import org.olat.core.gui.components.util.SelectionValues;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.Formatter;
import org.olat.core.util.StringHelper;
import org.olat.core.util.WebappHelper;
import org.olat.core.util.vfs.Quota;
import org.olat.modules.ceditor.model.BlockLayoutSettings;
import org.olat.modules.ceditor.model.BlockLayoutSpacing;
import org.olat.modules.ceditor.model.ImageElement;
import org.olat.modules.ceditor.model.jpa.MediaPart;
import org.olat.modules.cemedia.MediaVersion;
import org.olat.modules.cemedia.model.MediaUsage;

/**
 * 
 * Initial date: 15 juin 2023<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class MediaUIHelper {
	
	private MediaUIHelper() {
		//
	}
	
	public static boolean showBusinessPath(String businessPath) {
		return StringHelper.containsNonWhitespace(businessPath) && !businessPath.contains("[MediaCenter:0]");
	}
	
	public static String toMediaCenterBusinessPath(ImageElement imageElement) {
		String businessPath = "[HomeSite:0][MediaCenter:0]";
		if(imageElement instanceof MediaPart part) {
			businessPath += "[Media:" + part.getMedia().getKey() + "]";
		}
		return businessPath;
	}
	
	public static MediaTabComponents addMediaVersionTab(FormItemContainer formLayout, TabbedPaneItem tabbedPane,
			MediaPart mediaPart, List<MediaVersion> versions,
			FormUIFactory uifactory, Translator translator) {
		
		FormLayoutContainer layoutCont = FormLayoutContainer.createVerticalFormLayout("versions", translator);
		formLayout.add(layoutCont);
		tabbedPane.addTab(translator.translate("tab.media"), layoutCont);
		
		StaticTextElement nameEl = null;
		SingleSelection versionEl = null;
		if(mediaPart.getStoredData() != null) {
			String url = mediaPart.getMediaVersionUrl();
			String filename = mediaPart.getStoredData().getRootFilename();
		    if(StringHelper.containsNonWhitespace(url)) {
				nameEl = uifactory.addStaticTextElement("media.name", "media.name", url, layoutCont);
				nameEl.setElementCssClass("o_ceditor_inspector_wrap");
		    } else if(StringHelper.containsNonWhitespace(filename)) {
				nameEl = uifactory.addStaticTextElement("media.name", "media.name", filename, layoutCont);
				nameEl.setElementCssClass("o_ceditor_inspector_wrap");
			}
			
			if(versions != null && !versions.isEmpty()) {
				List<MediaVersion> versionList = new ArrayList<>(versions);
				
				String selectedKey = null;
				SelectionValues versionsVK = new SelectionValues();
				for(int i=0; i<versionList.size(); i++) {
					MediaVersion version = versionList.get(i);
					String value;
					if(i == 0) {
						value = translator.translate("last.version");
					} else {
						value = translator.translate("version", version.getVersionName());
					}
					
					String versionKey = version.getKey().toString();
					versionsVK.add(SelectionValues.entry(versionKey, value));
					if(mediaPart.getStoredData().equals(version)) {
						selectedKey = versionKey;
					}
				}
				
				versionEl = uifactory.addDropdownSingleselect("image.versions", layoutCont,
						versionsVK.keys(), versionsVK.values());
				versionEl.addActionListener(FormEvent.ONCHANGE);
				if(selectedKey == null && !versionsVK.isEmpty()) {
					versionEl.select(versionsVK.keys()[0], true);
				} else if(selectedKey != null && versionsVK.containsKey(selectedKey)) {
					versionEl.select(selectedKey, true);
				}
			}
		}
		
		FormLink mediaCenterLink = null;
		if(mediaPart.getMedia() != null) {
			mediaCenterLink = uifactory.addFormLink("goto.media.center", layoutCont, Link.LINK);
			mediaCenterLink.setIconLeftCSS("o_icon o_icon_external_link");
		}
		
		return new MediaTabComponents(nameEl, versionEl, mediaCenterLink);
	}
	
	public static MediaVersion getVersion(List<MediaVersion> versions, String selectedKey) {
		for(MediaVersion version:versions) {
			if(selectedKey.equals(version.getKey().toString())) {
				return version;
			}
		}
		return null;
	}
	
	public record MediaTabComponents (StaticTextElement nameEl, SingleSelection versionEl, FormLink mediaCenterLink) {
		//
	}
	
	public static void open(UserRequest ureq, WindowControl wControl, MediaUsage mediaUsage) {
		open(ureq, wControl, mediaUsage.binderKey(), mediaUsage.pageKey(), mediaUsage.repositoryEntryKey(), mediaUsage.subIdent());
	}
	
	public static String businessPath(Long binderKey, Long pageKey, Long repositoryEntryKey, String subIdent) {
		String businessPath = null;
		if(binderKey != null) {
			businessPath = "[HomeSite:0][PortfolioV2:0][MyBinders:0][Binder:" + binderKey + "][Entries:0][Entry:" + pageKey + "]";
		} else if(repositoryEntryKey != null) {
			businessPath = "[RepositoryEntry:" + repositoryEntryKey + "]";
			if(StringHelper.containsNonWhitespace(subIdent)) {
				businessPath += "[CourseNode:" + subIdent + "]";
			}
		} else if(pageKey != null) {
			//http://localhost:8081/auth/HomeSite/720898/PortfolioV2/0/MyPages/0/Entry/89
			businessPath = "[HomeSite:0][PortfolioV2:0][MyPages:0][Entry:" + pageKey + "]";
		} else  {
			businessPath = "[HomeSite:0][PortfolioV2:0]";
		}
		return businessPath;
	}
	
	public static void open(UserRequest ureq, WindowControl wControl, Long binderKey, Long pageKey, Long repositoryEntryKey, String subIdent) {
		String businessPath = businessPath(binderKey, pageKey, repositoryEntryKey, subIdent);
		if(StringHelper.containsNonWhitespace(businessPath)) {
			NewControllerFactory.getInstance().launch(businessPath, ureq, wControl);
		}
	}
	
	public static void setQuota(Quota quota, FileElement fileEl) {
		long uploadLimitKB = quota.getUlLimitKB().longValue();
		long remainingKB = quota.getRemainingSpace().longValue();
		if(uploadLimitKB != Quota.UNLIMITED && remainingKB != Quota.UNLIMITED) {
			long limitKB = Math.min(uploadLimitKB, remainingKB);
			String supportAddr = WebappHelper.getMailConfig("mailQuota");
			fileEl.setMaxUploadSizeKB(limitKB, "ULLimitExceeded", new String[] { Formatter.formatKBytes(limitKB), supportAddr });
			
			if(uploadLimitKB > remainingKB) {
				fileEl.setWarningKey("warning.upload.quota", Formatter.formatKBytes(remainingKB));
			}
		}
	}

	public static LayoutTabComponents addLayoutTab(FormItemContainer formLayout, TabbedPaneItem tabbedPane,
												   Translator translator, FormUIFactory uifactory,
												   BlockLayoutSettings layoutSettings, String velocity_root) {
		FormLayoutContainer layoutCont = FormLayoutContainer.createVerticalFormLayout("layout", translator);
		formLayout.add(layoutCont);
		tabbedPane.addTab(translator.translate("tab.layout"), layoutCont);

		SelectionValues spacingKV = new SelectionValues();
		SelectionValues spacingWithoutCustomKV = new SelectionValues();
		for (BlockLayoutSpacing spacing : BlockLayoutSpacing.values()) {
			SelectionValues.SelectionValue selectionValue = SelectionValues.entry(spacing.name(), translator.translate(spacing.getI18nKey()));
			spacingKV.add(selectionValue);
			if (spacing.isInCustomSubset()) {
				spacingWithoutCustomKV.add(selectionValue);
			}
		}
		SingleSelection spacingEl = uifactory.addDropdownSingleselect("spacing", "layout.spacing",
				layoutCont, spacingKV.keys(), spacingKV.values());
		spacingEl.addActionListener(FormEvent.ONCHANGE);
		if (layoutSettings != null && layoutSettings.getSpacing() != null) {
			spacingEl.select(layoutSettings.getSpacing().name(), true);
		} else {
			spacingEl.select(BlockLayoutSpacing.normal.name(), true);
		}

		String page = velocity_root + "/spacings.html";
		FormLayoutContainer spacingsCont = FormLayoutContainer.createCustomFormLayout("spacings", translator, page);
		layoutCont.setRootForm(formLayout.getRootForm());
		layoutCont.add(spacingsCont);

		SingleSelection topEl = uifactory.addDropdownSingleselect("top", "layout.spacing.custom.top",
				spacingsCont, spacingWithoutCustomKV.keys(), spacingWithoutCustomKV.values());
		topEl.addActionListener(FormEvent.ONCHANGE);

		SingleSelection rightEl = uifactory.addDropdownSingleselect("right", "layout.spacing.custom.right",
				spacingsCont, spacingWithoutCustomKV.keys(), spacingWithoutCustomKV.values());
		rightEl.addActionListener(FormEvent.ONCHANGE);

		SingleSelection bottomEl = uifactory.addDropdownSingleselect("bottom", "layout.spacing.custom.bottom",
				spacingsCont, spacingWithoutCustomKV.keys(), spacingWithoutCustomKV.values());
		bottomEl.addActionListener(FormEvent.ONCHANGE);

		SingleSelection leftEl = uifactory.addDropdownSingleselect("left", "layout.spacing.custom.left",
				spacingsCont, spacingWithoutCustomKV.keys(), spacingWithoutCustomKV.values());
		leftEl.addActionListener(FormEvent.ONCHANGE);

		spacingsCont.setVisible(false);
		if (layoutSettings != null && layoutSettings.getSpacing() != null) {
			if (layoutSettings.getCustomTopSpacing() != null && topEl.containsKey(layoutSettings.getCustomTopSpacing().name())) {
				topEl.select(layoutSettings.getCustomTopSpacing().name(), true);
			} else {
				topEl.select(topEl.getKeys()[0], true);
			}
			if (layoutSettings.getCustomRightSpacing() != null && rightEl.containsKey(layoutSettings.getCustomRightSpacing().name())) {
				rightEl.select(layoutSettings.getCustomRightSpacing().name(), true);
			} else {
				rightEl.select(rightEl.getKeys()[0], true);
			}
			if (layoutSettings.getCustomBottomSpacing() != null && bottomEl.containsKey(layoutSettings.getCustomBottomSpacing().name())) {
				bottomEl.select(layoutSettings.getCustomBottomSpacing().name(), true);
			} else {
				bottomEl.select(bottomEl.getKeys()[0], true);
			}
			if (layoutSettings.getCustomLeftSpacing() != null && leftEl.containsKey(layoutSettings.getCustomLeftSpacing().name())) {
				leftEl.select(layoutSettings.getCustomLeftSpacing().name(), true);
			} else {
				leftEl.select(leftEl.getKeys()[0], true);
			}
			if (layoutSettings.getSpacing().equals(BlockLayoutSpacing.custom)) {
				spacingsCont.setVisible(true);
			}
		}

		return new LayoutTabComponents(spacingEl, spacingsCont, topEl, rightEl, bottomEl, leftEl);
	}

	public record LayoutTabComponents (SingleSelection spacingEl, FormLayoutContainer spacingsCont, SingleSelection topEl,
									   SingleSelection rightEl, SingleSelection bottomEl, SingleSelection leftEl) {
		public boolean matches(FormItem source) {
			return source == spacingEl() || source == topEl() || source == rightEl() || source == bottomEl() || source == leftEl();
		}

		public void sync(BlockLayoutSettings layoutSettings) {
			BlockLayoutSpacing layoutSpacing = BlockLayoutSpacing.valueOf(spacingEl().getSelectedKey());
			layoutSettings.setSpacing(layoutSpacing);

			if (layoutSpacing.equals(BlockLayoutSpacing.custom)) {
				if (!topEl().isOneSelected()) {
					topEl().select(BlockLayoutSpacing.zero.name(), true);
				}
				layoutSettings.setCustomTopSpacing(BlockLayoutSpacing.valueOf(topEl().getSelectedKey()));
				if (!rightEl().isOneSelected()) {
					rightEl().select(BlockLayoutSpacing.zero.name(), true);
				}
				layoutSettings.setCustomRightSpacing(BlockLayoutSpacing.valueOf(rightEl().getSelectedKey()));
				if (!bottomEl().isOneSelected()) {
					bottomEl().select(BlockLayoutSpacing.zero.name(), true);
				}
				layoutSettings.setCustomBottomSpacing(BlockLayoutSpacing.valueOf(bottomEl().getSelectedKey()));
				if (!leftEl().isOneSelected()) {
					leftEl().select(BlockLayoutSpacing.zero.name(), true);
				}
				layoutSettings.setCustomLeftSpacing(BlockLayoutSpacing.valueOf(leftEl().getSelectedKey()));
			}

			updateVisibility();
		}

		private void updateVisibility() {
			BlockLayoutSpacing layoutSpacing = BlockLayoutSpacing.valueOf(spacingEl.getSelectedKey());
			if (layoutSpacing.equals(BlockLayoutSpacing.custom)) {
				spacingsCont().setVisible(true);
			} else {
				spacingsCont().setVisible(false);
			}
		}
	}
}
