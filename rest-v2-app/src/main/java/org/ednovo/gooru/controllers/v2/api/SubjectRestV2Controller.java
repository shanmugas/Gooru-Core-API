/////////////////////////////////////////////////////////////
// SubjectRestV2Controller.java
// rest-v2-app
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.controllers.v2.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.domain.service.subject.SubjectService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value = {"/v2/subject"})
public class SubjectRestV2Controller extends BaseController implements ConstantProperties{

	@Autowired
	private SubjectService subjectService;

	//@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBJECT_ADD})
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createSubject(HttpServletRequest request, HttpServletResponse response, @RequestBody String data)throws Exception{
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		this.getSubjectService().createSubject(buildSubjectFromInputParameters(getValue(SUBJECT, json)),user);
		
		return null;
	}
	
	//@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBJECT_READ})
	@RequestMapping(method = RequestMethod.GET, value = " ")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getSubjects(HttpServletResponse response, HttpServletRequest request)throws Exception{
		List<Subject> subjectObj = this.getSubjectService().getSubjects();
		return toModelAndViewWithIoFilter(subjectObj, FORMAT_JSON, EXCLUDE_ALL, true, SUBJECT_INCLUDES);
	}
	
	//@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBJECT_READ})
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getSubject(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) String SubjectId)throws Exception{
		Subject subjectObj = this.getSubjectService().getSubject(SubjectId);
		return toModelAndViewWithIoFilter(subjectObj, FORMAT_JSON, EXCLUDE_ALL, true, SUBJECT_INCLUDES);
	}
	
	//@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBJECT_UPDATE})
	@RequestMapping(method = RequestMethod.PUT, value ="/{id}")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView putSubject(HttpServletResponse response, HttpServletRequest request,@RequestBody String data, @PathVariable(ID) String collectionId)throws Exception{
		
		return null;
	}
		
	//@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBJECT_DELETE})
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView deleteSubject(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) String collectionId)throws Exception{
		
		return null;
	}
	
	public SubjectService getSubjectService() {
		return subjectService;
	}
	
	private Subject buildSubjectFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Subject.class);
	}
	
}
