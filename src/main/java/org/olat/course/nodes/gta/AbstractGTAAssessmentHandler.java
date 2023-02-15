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
package org.olat.course.nodes.gta;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BlankController;
import org.olat.core.id.Identity;
import org.olat.course.assessment.AssessmentManager;
import org.olat.course.assessment.handler.AssessmentConfig;
import org.olat.course.assessment.handler.AssessmentHandler;
import org.olat.course.config.CourseConfig;
import org.olat.course.learningpath.evaluation.LearningPathEvaluatorBuilder;
import org.olat.course.learningpath.manager.LearningPathNodeAccessProvider;
import org.olat.course.nodes.CourseNode;
import org.olat.course.nodes.GTACourseNode;
import org.olat.course.nodes.gta.ui.GTAAssessmentDetailsController;
import org.olat.course.run.scoring.AccountingEvaluators;
import org.olat.course.run.scoring.AccountingEvaluatorsBuilder;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.repository.RepositoryEntryRef;

/**
 * 
 * Initial date: 20 Aug 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public abstract class AbstractGTAAssessmentHandler implements AssessmentHandler {

	@Override
	public AssessmentConfig getAssessmentConfig(RepositoryEntryRef courseEntry, CourseNode courseNode) {
		return new GTAAssessmentConfig(courseEntry, courseNode);
	}

	@Override
	public AssessmentEntry getAssessmentEntry(CourseNode courseNode, UserCourseEnvironment userCourseEnvironment) {
		AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
		Identity assessedIdentity = userCourseEnvironment.getIdentityEnvironment().getIdentity();
		return am.getAssessmentEntry(courseNode, assessedIdentity);
	}
	
	@Override
	public AccountingEvaluators getEvaluators(CourseNode courseNode, CourseConfig courseConfig) {
		if (LearningPathNodeAccessProvider.TYPE.equals(courseConfig.getNodeAccessType().getType())) {
			return LearningPathEvaluatorBuilder.buildDefault();
		}
		return AccountingEvaluatorsBuilder.defaultConventional();
	}
	
	@Override
	public Controller getDetailsEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel,
			CourseNode courseNode, UserCourseEnvironment coachCourseEnv, UserCourseEnvironment assessedUsserCourseEnv) {
		if (courseNode instanceof GTACourseNode) {
			GTACourseNode gtaNode = (GTACourseNode) courseNode;
			return new GTAAssessmentDetailsController(ureq, wControl, coachCourseEnv, assessedUsserCourseEnv, gtaNode);
		}
		return new BlankController(ureq, wControl);
	}
	
	@Override
	public boolean hasCustomIdentityList() {
		return true;
	}

}
