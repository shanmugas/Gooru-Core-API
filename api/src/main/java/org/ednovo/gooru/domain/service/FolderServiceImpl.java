/////////////////////////////////////////////////////////////
// FolderServiceImpl.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class FolderServiceImpl extends BaseServiceImpl implements FolderService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RedisService redisService;
	
	@Autowired
	private CollectionService collectionService;

	@Override
	public SearchResults<Map<String, Object>> getMyCollectionsToc(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, String excludeType) {
		if (!BaseUtil.isUuid(gooruUid)) {
			User user = this.getUserRepository().getUserByUserName(gooruUid, true);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		rejectIfNull(gooruUid, GL0056, 404, USER);
		List<Object[]> result = this.getCollectionRepository().getMyFolder(gooruUid, limit, offset, sharing, collectionType, true, orderBy, excludeType);
		List<Map<String, Object>> folders = new ArrayList<Map<String, Object>>();
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				collection.put(TITLE, object[0]);
				collection.put(GOORU_OID, object[1]);
				collection.put(TYPE, object[2]);
				collection.put(IDEAS, object[12]);
				collection.put(QUESTIONS, object[13]);
				collection.put(PERFORMANCE_TASKS, object[14]);
				collection.put(COLLECTION_TYPE, object[15]);
				collection.put(COLLECTION_ITEM_ID, object[6]);
				collection.put(DESCRIPTION, object[7] != null ? object[7] : object[19]);
				collection.put(URL, object[20]);
				if (object[21] != null) {
					collection.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(object[21]), new TypeReference<Map<String, String>>() {
					}));
				}
				if (object[2] != null && object[2].toString().equalsIgnoreCase(SCOLLECTION)) {
					collection.put(COLLECTION_ITEMS, getFolderTocItems(String.valueOf(object[1]), sharing, collectionType, orderBy, ASC, excludeType));
				}
				folders.add(collection);
			}
		}
		SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		searchResult.setSearchResults(folders);
		searchResult.setTotalHitCount(this.getCollectionRepository().getMyShelfCount(gooruUid, sharing, collectionType, excludeType));
		return searchResult;
	}

	@Override
	public List<Map<String, Object>> getFolderTocItems(String gooruOid, String sharing, String collectionType, String orderBy, String sortOrder, String excludeType) {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, null, null, sharing, orderBy, collectionType, true, sortOrder, true, excludeType);
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(TITLE, object[0]);
				item.put(GOORU_OID, object[1]);
				item.put(TYPE, object[2]);
				if (object[2] != null && (!object[2].toString().equalsIgnoreCase(SCOLLECTION) && !object[2].toString().equalsIgnoreCase(FOLDER))) {
					if (object[5] != null) {
						Map<String, Object> resourceFormat = new HashMap<String, Object>();
						resourceFormat.put(VALUE, object[5]);
						resourceFormat.put(DISPLAY_NAME, object[6]);
						item.put(RESOURCEFORMAT, resourceFormat);
					}
				}
				if (object[2] != null && object[2].toString().equalsIgnoreCase(SCOLLECTION)) {
					item.put(COLLECTION_ITEMS, getFolderTocItems(String.valueOf(object[1]), sharing, collectionType, orderBy, ASC, excludeType));
				}
				item.put(IDEAS, object[12]);
				item.put(QUESTIONS, object[13]);
				item.put(PERFORMANCE_TASKS, object[14]);
				item.put(COLLECTION_TYPE, object[18]);
				item.put(COLLECTION_ITEM_ID, object[8]);
				item.put(DESCRIPTION, object[9] != null ? object[9] : object[22]);
				item.put(URL, object[15]);
				if (object[23] != null) {
					item.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(object[23]), new TypeReference<Map<String, String>>() {
					}));
				}
				items.add(item);
			}
		}
		return items;

	}

	@Override
	public String getMyCollectionsToc(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, String excludeType, boolean clearCache) {
		final String cacheKey = V2_ORGANIZE_DATA + gooruUid + HYPHEN + offset + HYPHEN + limit + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + HYPHEN + orderBy + HYPHEN + excludeType + HYPHEN + TOC;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);

		}
		if (data == null) {
			data = SerializerUtil.serializeToJson(this.getMyCollectionsToc(gooruUid, limit, offset, sharing, collectionType, orderBy, excludeType), TOC_EXCLUDES, true, true);
			getRedisService().putValue(cacheKey, data, 86400);
		}
		return data;
	}

	@Override
	public String getFolderTocItems(String gooruOid, String sharing, String collectionType, String orderBy, String excludeType, boolean clearCache) {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		rejectIfNull(collection, GL0056, 404, FOLDER);
		final String cacheKey = V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + HYPHEN + gooruOid + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + orderBy + HYPHEN + excludeType + HYPHEN + TOC;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);
		}
		if (data == null) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put(TITLE, collection.getTitle());
			item.put(IDEAS, collection.getIdeas());
			item.put(QUESTIONS, collection.getQuestions());
			item.put(PERFORMANCE_TASKS, collection.getPerformanceTasks());
			item.put(DESCRIPTION, collection.getGoals() != null ? collection.getGoals() : collection.getDescription());
			item.put(COLLECTION_TYPE, collection.getCollectionType());
			item.put(URL, collection.getUrl());
			item.put(COLLECTION_ITEMS, this.getFolderTocItems(gooruOid, sharing, collectionType, orderBy, collection.getResourceType().getName().equalsIgnoreCase(SCOLLECTION) ? ASC : DESC, excludeType));
			data = SerializerUtil.serializeToJson(item, TOC_EXCLUDES, true, true);
			getRedisService().putValue(cacheKey, data, 86400);
		}
		return data;
	}
	
	@Override
	public List<Map<String, String>> getFolderNode(String collectionId) {
        Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
        rejectIfNull(collection, GL0056, 404, COLLECTION);
		return this.getCollectionService().getParentCollection(collectionId, collection.getUser().getGooruUId(), true);
	}

	@Override
	public Map<String, Object> getNextCollectionItem(String collectionItemId, String excludeType) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		rejectIfNull(collectionItem, GL0056, 404, COLLECTION_ITEM);
		Map<String, Object> nextCollection =  new HashMap<String, Object>();
		CollectionItem nextCollectionItem = this.getCollectionRepository().getNextCollectionItemResource(collectionItem.getCollection().getGooruOid(), collectionItem.getItemSequence(), excludeType);
		if (nextCollectionItem != null) { 
			nextCollection.put(COLLECTION_ITEM_ID, nextCollectionItem.getCollectionItemId());
			nextCollection.put(TITLE, nextCollectionItem.getResource().getTitle());
			nextCollection.put(GOORU_OID, nextCollectionItem.getResource().getGooruOid());
			nextCollection.put(THUMBNAILS, nextCollectionItem.getResource().getThumbnails());
		}
		return nextCollection;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}
}