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
package org.olat.modules.quality.ui;

import static org.olat.modules.quality.QualityDataCollectionTopicType.CUSTOM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.olat.basesecurity.OrganisationModule;
import org.olat.basesecurity.OrganisationRoles;
import org.olat.basesecurity.OrganisationService;
import org.olat.basesecurity.model.OrganisationRefImpl;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.components.form.flexible.elements.DateChooser;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SingleSelection;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.tree.GenericTreeNode;
import org.olat.core.gui.translator.Translator;
import org.olat.core.id.Organisation;
import org.olat.core.id.OrganisationNameComparator;
import org.olat.core.id.OrganisationRef;
import org.olat.core.util.StringHelper;
import org.olat.core.util.UserSession;
import org.olat.core.util.nodes.INode;
import org.olat.modules.curriculum.Curriculum;
import org.olat.modules.curriculum.CurriculumElement;
import org.olat.modules.curriculum.CurriculumElementRef;
import org.olat.modules.curriculum.CurriculumModule;
import org.olat.modules.curriculum.CurriculumRef;
import org.olat.modules.curriculum.ui.CurriculumTreeModel;
import org.olat.modules.quality.QualityDataCollectionTopicType;
import org.olat.modules.quality.QualityDataCollectionView;
import org.olat.modules.quality.QualityExecutorParticipation;
import org.olat.user.ui.organisation.OrganisationTreeModel;

/**
 * 
 * Initial date: 15.06.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class QualityUIFactory {

	private static final String INTENDING = "\u00a0"; // &nbsp; non-breaking space
	private static final Comparator<? super Curriculum> DISPLAY_NAME_COMPARATOR = (c1, c2) -> c1.getDisplayName().compareTo(c2.getDisplayName());
	
	public static String formatTopic(QualityDataCollectionView dataCollectionView) {
		return formatTopic(dataCollectionView.getTopicType(), dataCollectionView.getTranslatedTopicType(),
				dataCollectionView.getTopic());
	}

	public static String formatTopic(QualityExecutorParticipation qualityParticipation) {
		return formatTopic(qualityParticipation.getTopicType(), qualityParticipation.getTranslatedTopicType(),
				qualityParticipation.getTopic());
	}

	public static String formatTopic(QualityDataCollectionTopicType type, String translatedType, String topic) {
		StringBuilder formatedTopic = new StringBuilder();
		if (!CUSTOM.equals(type)) {
			formatedTopic.append(translatedType).append(" ");
		}
		formatedTopic.append(topic);
		return formatedTopic.toString();
	}
	
	static String getTopicTypeKey(QualityDataCollectionTopicType topicType) {
		return topicType.name();
	}
	
	static QualityDataCollectionTopicType getTopicTypeEnum(String key) {
		return QualityDataCollectionTopicType.valueOf(key);
	}

	static String[] getTopicTypeKeys(QualityDataCollectionTopicType actual) {
		return Arrays.stream(QualityDataCollectionTopicType.values())
				.filter(organisationDisabled(actual))
				.filter(curriculumDisabled(actual))
				.map(QualityDataCollectionTopicType::name)
				.toArray(String[]::new);
	}

	static String[] getTopicTypeValues(Translator translator, QualityDataCollectionTopicType actual) {
		return Arrays.stream(QualityDataCollectionTopicType.values())
				.filter(organisationDisabled(actual))
				.filter(curriculumDisabled(actual))
				.map(type -> type.getI18nKey())
				.map(i18n -> translator.translate(i18n))
				.toArray(String[]::new);
	}
	
	private static Predicate<QualityDataCollectionTopicType> organisationDisabled(QualityDataCollectionTopicType actual) {
		return tt ->
				   (actual != null && actual.equals(tt))
				|| !QualityDataCollectionTopicType.ORGANISATION.equals(tt)
				|| CoreSpringFactory.getImpl(OrganisationModule.class).isEnabled();
	}
	
	private static Predicate<QualityDataCollectionTopicType> curriculumDisabled(QualityDataCollectionTopicType actual) {
		return tt -> 
				   (actual != null && actual.equals(tt))
				|| !(QualityDataCollectionTopicType.CURRICULUM.equals(tt)
						|| QualityDataCollectionTopicType.CURRICULUM_ELEMENT.equals(tt))
				|| CoreSpringFactory.getImpl(CurriculumModule.class).isEnabled();
	}
	
	public static KeysValues getCurriculumKeysValues(List<Curriculum> curriculums) {
		String[] keys = curriculums.stream()
				.sorted(DISPLAY_NAME_COMPARATOR)
				.map(Curriculum::getKey)
				.map(String::valueOf)
				.toArray(String[]::new);
		String[] values = curriculums.stream()
				.sorted(DISPLAY_NAME_COMPARATOR)
				.map(Curriculum::getDisplayName)
				.toArray(String[]::new);
		return new KeysValues(keys, values);
	}

	public static String getCurriculumKey(Curriculum curriculum) {
		return String.valueOf(curriculum.getKey());
	}

	public static CurriculumRef getCurriculumRef(String curriculumKey) {
		if (StringHelper.containsNonWhitespace(curriculumKey)) {
			try {
				Long key = Long.valueOf(curriculumKey);
				return new CurriculumRef() {
					
					@Override
					public Long getKey() {
						return key;
					}
				};
			} catch (Exception e) {
				//
			}
		}
		return null;
	}

	public static KeysValues getCurriculumElementKeysValues(CurriculumTreeModel curriculumTreeModel) {
		List<CurriculumElement> elements = new ArrayList<>();
		curriculumElementTreeToList(elements, curriculumTreeModel.getRootNode());
		String[] keys = new String[elements.size()];
		String[] values = new String[elements.size()];
		for (int i = elements.size(); i-->0; ) {
			CurriculumElement element = elements.get(i);
			keys[i] = Long.toString(element.getKey());
			values[i] = computeIntendentionForCurriculumElement(element, new StringBuilder()).append(element.getDisplayName()).toString();
		}
		return new KeysValues(keys, values);
	}
	
	private static void curriculumElementTreeToList(List<CurriculumElement> elements, INode node) {
		if (node instanceof GenericTreeNode) {
			GenericTreeNode genericTreeNode = (GenericTreeNode) node;
			Object userObject = genericTreeNode.getUserObject();
			if (userObject instanceof CurriculumElement) {
				CurriculumElement element = (CurriculumElement) userObject;
				elements.add(element);
			}
			for (int i = 0; i < genericTreeNode.getChildCount(); i++) {
				curriculumElementTreeToList(elements, genericTreeNode.getChildAt(i));
			}
		}
	}

	private static StringBuilder computeIntendentionForCurriculumElement(CurriculumElement element, StringBuilder intendation) {
		CurriculumElement parent = element.getParent();
		if (parent != null) {
			intendation = intendation.append(INTENDING).append(INTENDING).append(INTENDING).append(INTENDING);
			computeIntendentionForCurriculumElement(parent, intendation);
		}
		return intendation;
	}

	public static String getCurriculumElementKey(CurriculumElement curriculumElement) {
		return String.valueOf(curriculumElement.getKey());
	}

	public static CurriculumElementRef getCurriculumElementRef(String curriculumElementKey) {
		if (StringHelper.containsNonWhitespace(curriculumElementKey)) {
			try {
				Long key = Long.valueOf(curriculumElementKey);
				return new CurriculumElementRef() {
					
					@Override
					public Long getKey() {
						return key;
					}
				};
			} catch (Exception e) {
				//
			}
		}
		return null;
	}

	public static KeysValues getTopicOrganisationKeysValues(OrganisationTreeModel organisationModel) {
		List<Organisation> organisations = new ArrayList<>();
		organsiationTreeToList(organisations, organisationModel.getRootNode());
		String[] keys = new String[organisations.size()];
		String[] values = new String[organisations.size()];
		for (int i = organisations.size(); i-->0; ) {
			Organisation organisation = organisations.get(i);
			keys[i] = Long.toString(organisation.getKey());
			values[i] = computeIntendentionForOrganisation(organisation, new StringBuilder()).append(organisation.getDisplayName()).toString();
		}
		return new KeysValues(keys, values);
	}
	
	private static void organsiationTreeToList(List<Organisation> organisations, INode node) {
		if (node instanceof GenericTreeNode) {
			GenericTreeNode genericTreeNode = (GenericTreeNode) node;
			Object userObject = genericTreeNode.getUserObject();
			if (userObject instanceof Organisation) {
				Organisation organisation = (Organisation) userObject;
				organisations.add(organisation);
			}
			for (int i = 0; i < genericTreeNode.getChildCount(); i++) {
				organsiationTreeToList(organisations, genericTreeNode.getChildAt(i));
			}
		}
	}

	private static StringBuilder computeIntendentionForOrganisation(Organisation organisation, StringBuilder intendation) {
		Organisation parent = organisation.getParent();
		if (parent != null) {
			intendation = intendation.append(INTENDING).append(INTENDING).append(INTENDING).append(INTENDING);
			computeIntendentionForOrganisation(parent, intendation);
		}
		return intendation;
	}
	
	static String getTopicOrganisationKey(Organisation organisation) {
		return String.valueOf(organisation.getKey());
	}

	static OrganisationRef getOrganisationRef(String organisationKey) {
		if (StringHelper.containsNonWhitespace(organisationKey)) {
			try {
				Long key = Long.valueOf(organisationKey);
				return new OrganisationRef() {
					
					@Override
					public Long getKey() {
						return key;
					}
				};
			} catch (Exception e) {
				//
			}
		}
		return null;
	}
	
	public static void initOrganisations(UserSession usess, MultipleSelectionElement organisationsEl,
			List<Organisation> currentOrganisations) {
		OrganisationService organisationService = CoreSpringFactory.getImpl(OrganisationService.class);
		
		// Get all organisations of the user
		List<Organisation> userOrganisations = organisationService.getOrganisations(usess.getIdentity(), usess.getRoles(),
				OrganisationRoles.administrator, OrganisationRoles.qualitymanager);
		List<Organisation> allOrganisations = new ArrayList<>(userOrganisations);

		// Complete with the active organisations
		for(Organisation activeOrganisation:currentOrganisations) {
			if(activeOrganisation != null && !allOrganisations.contains(activeOrganisation)) {
				allOrganisations.add(activeOrganisation);
			}
		}
		
		// Sort the organisations
		Collections.sort(allOrganisations, new OrganisationNameComparator(usess.getLocale()));
		
		// Make the keys and values for the form element
		List<String> keyList = new ArrayList<>();
		List<String> valueList = new ArrayList<>();
		for(Organisation elOrganisation:allOrganisations) {
			keyList.add(elOrganisation.getKey().toString());
			valueList.add(elOrganisation.getDisplayName());
		}
		
		// Update the for element and select the active organisations.
		organisationsEl.setKeysAndValues(keyList.toArray(new String[keyList.size()]),
				valueList.toArray(new String[valueList.size()]));
		for(Organisation reOrganisation:currentOrganisations) {
			if(keyList.contains(reOrganisation.getKey().toString())) {
				organisationsEl.select(reOrganisation.getKey().toString(), true);
			}
		}
	}
	
	public static List<OrganisationRef> getSelectedOrganisationRefs(MultipleSelectionElement organisationsEl) {
		return organisationsEl.getSelectedKeys().stream()
				.map(key -> getOrganisationRef(key))
				.collect(Collectors.toList());
	}
	
	public static List<Organisation> getSelectedOrganisations(MultipleSelectionElement organisationsEl,
			List<Organisation> currentOrganisations) {
		OrganisationService organisationService = CoreSpringFactory.getImpl(OrganisationService.class);

		// Copy the current organisations
		List<Organisation> organisations = new ArrayList<>(currentOrganisations);
		Collection<String> selectedOrganisationKeys = organisationsEl.getSelectedKeys();
		
		// Remove unselected organisations
		organisations.removeIf(organisation -> !selectedOrganisationKeys.contains(organisation.getKey().toString()));

		// Add newly selected organisations
		Collection<String> organisationKeys = organisations.stream()
				.map(Organisation::getKey)
				.map(String::valueOf)
				.collect(Collectors.toList());
		for (String selectedOrganisationKey: selectedOrganisationKeys) {
			if (!organisationKeys.contains(selectedOrganisationKey)) {
				Organisation organisation = organisationService.getOrganisation(new OrganisationRefImpl(Long.valueOf(selectedOrganisationKey)));
				if (organisation != null) {
					organisations.add(organisation);
				}
			}
		}
		return organisations;
	}
	
	public static boolean validateInteger(TextElement el, int min, int max) {
		boolean allOk = true;
		el.clearError();
		if(el.isEnabled()) {
			String val = el.getValue();
			if(StringHelper.containsNonWhitespace(val)) {
				
				try {
					double value = Integer.parseInt(val);
					if(min > value) {
						el.setErrorKey("error.wrong.int", null);
						allOk = false;
					} else if(max < value) {
						el.setErrorKey("error.wrong.int", null);
						allOk = false;
					}
				} catch (NumberFormatException e) {
					el.setErrorKey("error.wrong.int", null);
					allOk = false;
				}
			}
		}
		return allOk;
	}
	
	public static boolean validateIsMandatory(TextElement el) {
		boolean allOk = true;
		el.clearError();
		if(el.isEnabled()) {
			String value = el.getValue();
			if (!StringHelper.containsNonWhitespace(value)) {
				el.setErrorKey("form.mandatory.hover", null);
				allOk = false;
			}
		}
		return allOk;
	}
	
	public static boolean validateIsMandatory(SingleSelection el) {
		boolean allOk = true;
		el.clearError();
		if(el.isEnabled()) {
			if (!el.isOneSelected()) {
				el.setErrorKey("form.mandatory.hover", null);
				allOk = false;
			}
		}
		return allOk;
	}

	public static boolean validateIsMandatory(MultipleSelectionElement el) {
		boolean allOk = true;
		el.clearError();
		if(el.isEnabled()) {
			if (!el.isAtLeastSelected(1)) {
				el.setErrorKey("form.mandatory.hover", null);
				allOk = false;
			}
		}
		return allOk;
	}
	
	public static boolean validateIsMandatory(DateChooser el) {
		boolean allOk = true;
		el.clearError();
		if(el.isEnabled()) {
			if (el.getDate() == null) {
				el.setErrorKey("form.mandatory.hover", null);
				allOk = false;
			}
		}
		return allOk;
	}
	
	public static class KeysValues {
		
		private final String[] keys;
		private final String[] values;
		
		protected KeysValues(String[] keys, String[] values) {
			super();
			this.keys = keys;
			this.values = values;
		}

		public String[] getKeys() {
			return keys;
		}

		public String[] getValues() {
			return values;
		}
		
	}

}
