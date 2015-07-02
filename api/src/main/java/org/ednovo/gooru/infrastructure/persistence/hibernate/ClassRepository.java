package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.UserClass;

public interface ClassRepository extends BaseRepository {

	UserClass getClassById(String classUid);

	void deleteUserFromClass(String classUid, String userUid);

	List<Map<String, Object>> getClasses(int limit, int offset);

	List<Map<String, Object>> getClasses(String gooruUid, int limit, int offset);

	Map<String, Object> getClass(String classUid);
	
	List<Map<String, Object>> getStudyClasses(String gooruUid, int limit, int offset);
	
	List<Map<String, Object>>  getMember(String classUid, int limit, int offset);
		
	Map<String, Object> findStudentAndClassId(String classContentId, String gooruUId);

	Long getClassId(String classGooruId);
	
	List<Map<String, Object>> getClassesByCourse(String courseGooruOid, int limit, int offset);
	
}