/////////////////////////////////////////////////////////////
// SubjectServiceImpl.java
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
package org.ednovo.gooru.domain.service.subject;

import java.util.Date;
import java.util.List;

import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl extends BaseServiceImpl implements SubjectService{

	@Autowired
	private SubjectRepository subjectRepository;

	@Override
    public void createSubject(Subject subject, User user) {
		if(subject !=null){
		    Subject newSubject = new Subject();		    
		    if(subject.getDescription()!=null)
		    	newSubject.setDescription(subject.getDescription());
		    if(user.getOrganization().getOrganizationUid() != null)
		    	newSubject.setOrganizationUid(user.getOrganizationUid());
		    else
		    	newSubject.setOrganizationUid("4261739e-ccae-11e1-adfb-5404a609bd14");
		    if(subject.getImagePath()!=null)
		    	newSubject.setImagePath(subject.getImagePath());
		    if(user.getPartyUid() != null)
		    	newSubject.setCreatorId(user.getUserUid());
		    
		    newSubject.setActiveFlag(subject.getActiveFlag());
		    newSubject.setName(subject.getName());
		    newSubject.setDisplaySequence(subject.getDisplaySequence());
	    	newSubject.setCreatedOn(new Date(System.currentTimeMillis()));
	    	newSubject.setLastModified(new Date(System.currentTimeMillis()));
	    	
		    subjectRepository.save(newSubject);
		}
    }
	
	@Override
	public Subject getSubject(String subjectId) {
		return (Subject) subjectRepository.getSubject(subjectId);
	}
	
	@Override
	public List<Subject> getSubjects() {
		return (List<Subject>) subjectRepository.getSubjects();
	}
	
	
	
	
	public SubjectRepository getSubjectRepository() {
		return subjectRepository;
	}
	
}
