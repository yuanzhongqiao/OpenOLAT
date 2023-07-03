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
package org.olat.modules.openbadges.ui;

import java.util.Set;

import org.olat.core.util.FileUtils;
import org.olat.core.util.StringHelper;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.modules.openbadges.BadgeClass;
import org.olat.modules.openbadges.criteria.BadgeCriteria;
import org.olat.modules.openbadges.criteria.BadgeCriteriaXStream;
import org.olat.modules.openbadges.model.BadgeClassImpl;
import org.olat.modules.openbadges.v2.Profile;
import org.olat.repository.RepositoryEntry;

/**
 * Initial date: 2023-06-19<br>
 *
 * @author cpfranger, christoph.pfranger@frentix.com, <a href="https://www.frentix.com">https://www.frentix.com</a>
 */
public class CreateBadgeClassWizardContext {
	public enum Mode {
		create, edit
	}

	public static final String KEY = "createBadgeClassWizardContext";

	private final BadgeClass badgeClass;
	private final ICourse course;
	private Long selectedTemplateKey;
	private String selectedTemplateImage;
	private Set<String> templateVariables;
	private String backgroundColorId;
	private String title;
	private BadgeCriteria badgeCriteria;
	private Profile issuer;
	private Mode mode;

	public CreateBadgeClassWizardContext(RepositoryEntry entry) {
		mode = Mode.create;
		course = CourseFactory.loadCourse(entry);
		BadgeClassImpl badgeClassImpl = new BadgeClassImpl();
		badgeClassImpl.setUuid(OpenBadgesUIFactory.createIdentifier());
		badgeClassImpl.setStatus(BadgeClass.BadgeClassStatus.preparation);
		badgeClassImpl.setSalt("badgeClass" + Math.abs(badgeClassImpl.getUuid().hashCode()));
		badgeClassImpl.setIssuer("{}");
		badgeClassImpl.setVersion("1.0");
		badgeClassImpl.setLanguage("en");
		badgeClassImpl.setValidityEnabled(false);
		badgeClassImpl.setEntry(entry);
		backgroundColorId = "lightgray";
		title = course.getCourseTitle();
		initCriteria();
		issuer = new Profile(badgeClassImpl);
		badgeClass = badgeClassImpl;
	}

	public CreateBadgeClassWizardContext(BadgeClass badgeClass) {
		mode = Mode.edit;
		course = CourseFactory.loadCourse(badgeClass.getEntry());
		backgroundColorId = null;
		title = null;
		badgeCriteria = BadgeCriteriaXStream.fromXml(badgeClass.getCriteria());
		this.badgeClass = badgeClass;
		issuer = new Profile(badgeClass);
	}

	private void initCriteria() {
		badgeCriteria = new BadgeCriteria();
		badgeCriteria.setAwardAutomatically(false);
	}

	public BadgeClass getBadgeClass() {
		return badgeClass;
	}

	public Long getSelectedTemplateKey() {
		return selectedTemplateKey;
	}

	public void setSelectedTemplateKey(Long selectedTemplateKey) {
		this.selectedTemplateKey = selectedTemplateKey;
	}

	public String getSelectedTemplateImage() {
		return selectedTemplateImage;
	}

	public void setSelectedTemplateImage(String selectedTemplateImage) {
		this.selectedTemplateImage = selectedTemplateImage;
	}

	public ICourse getCourse() {
		return course;
	}

	public String getBackgroundColorId() {
		return backgroundColorId;
	}

	public void setBackgroundColorId(String backgroundColorId) {
		this.backgroundColorId = backgroundColorId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BadgeCriteria getBadgeCriteria() {
		return badgeCriteria;
	}

	public boolean needsCustomization() {
		if (!StringHelper.containsNonWhitespace(selectedTemplateImage)) {
			return false;
		}
		String suffix = FileUtils.getFileSuffix(selectedTemplateImage);
		if (!suffix.equalsIgnoreCase("svg")) {
			return false;
		}

		if (getTemplateVariables() == null || getTemplateVariables().isEmpty()) {
			return false;
		}

		return true;
	}

	public Set<String> getTemplateVariables() {
		return templateVariables;
	}

	public void setTemplateVariables(Set<String> templateVariables) {
		this.templateVariables = templateVariables;
	}

	public Mode getMode() {
		return mode;
	}
}
