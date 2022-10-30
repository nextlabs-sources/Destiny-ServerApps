package com.nextlabs.destiny.console.services.impl;

import static com.nextlabs.destiny.console.config.root.RootBeanNameEnum.MGMT_UNIT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.destiny.lib.LeafObject;
import com.bluejungle.pf.destiny.lib.LeafObjectSearchSpec;
import com.bluejungle.pf.destiny.lib.LeafObjectType;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.bluejungle.pf.domain.destiny.subject.SubjectAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberPreviewDTO;
import com.nextlabs.destiny.console.enums.PolicyModelType;
import com.nextlabs.destiny.console.services.DPSProxyService;
import com.nextlabs.destiny.console.services.MemberService;

@Service
public class MemberServiceImpl implements MemberService {

	private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

	@PersistenceContext(unitName = MGMT_UNIT)
	private EntityManager entityManager;

	@Autowired
	private DPSProxyService dpsService;

	private static final Map<LeafObjectType, SubjectAttribute> LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP = new HashMap<>();

	static {
		LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP.put(LeafObjectType.HOST, SubjectAttribute.HOST_NAME);
		LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP.put(LeafObjectType.HOST_GROUP, SubjectAttribute.HOST_LDAP_GROUP_DISPLAY_NAME);
		LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP.put(LeafObjectType.USER,
				SubjectAttribute.forNameAndType("displayName", SubjectType.USER));
		LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP.put(LeafObjectType.USER_GROUP, SubjectAttribute.USER_LDAP_GROUP_DISPLAY_NAME);
		LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP.put(LeafObjectType.APPLICATION, SubjectAttribute.APP_NAME);
	}
	
	public List<MemberDTO> runLeafObjectQueryForMembers(LeafObjectType type, SubjectAttribute attr, RelationOp rel, String searchString){
		IPredicate pred;
		if(searchString.trim().isEmpty()) {
			pred = PredicateConstants.TRUE;
		} else {
			pred = attr.buildRelation(rel, Constant.build(searchString));
		}
		
		LeafObjectSearchSpec leafObjectSearchSpec = new LeafObjectSearchSpec(type, pred, 1000);
        List<MemberDTO> memberDTOList = new ArrayList<>();
		
		try {
			memberDTOList = getMembers(dpsService.getPolicyEditorClient().runLeafObjectQuery(leafObjectSearchSpec));
		} catch (PolicyEditorException e) {
			log.error(e.getMessage(), e);
		}
		
		return memberDTOList;
	}

	@Override
	public List<MemberDTO> findMemberBySearchString(LeafObjectType type, String searchString) {
		return runLeafObjectQueryForMembers(type, LEAF_OBJECT_TYPE_TO_ATTRIBUTE_MAP.get(type), RelationOp.EQUALS, searchString.trim() + "*");
	}
	
	@Override
	public List<MemberDTO> findMemberByUniqueName(LeafObjectType type, String uniqueName){

		SubjectAttribute attr;
		if (type == LeafObjectType.HOST) {
			attr = SubjectAttribute.HOST_NAME;
		} else if(type == LeafObjectType.HOST_GROUP) {
			attr = SubjectAttribute.HOST_LDAP_GROUP;
		} else if (type == LeafObjectType.APPLICATION) {
			attr = SubjectAttribute.APP_NAME;
		} else if(type == LeafObjectType.USER_GROUP){
			attr = SubjectAttribute.USER_LDAP_GROUP;
		} else {
			attr = SubjectAttribute.USER_NAME;
		}

		return uniqueName==null || uniqueName.trim().isEmpty()? null : runLeafObjectQueryForMembers(type, attr, RelationOp.EQUALS, uniqueName);
	}
	
	@Override
	public List<MemberDTO> findMemberById(LeafObjectType type, String id){
		SubjectAttribute attr;
		
		if (type == LeafObjectType.HOST) {
			attr = SubjectAttribute.HOST_ID;
		} else if(type == LeafObjectType.HOST_GROUP) {
			attr = SubjectAttribute.HOST_LDAP_GROUP_ID;
		} else if(type == LeafObjectType.APPLICATION) {
			attr = SubjectAttribute.APP_ID;
		} else if(type == LeafObjectType.USER) {
			attr = SubjectAttribute.USER_ID;
		} else {
			attr = SubjectAttribute.USER_LDAP_GROUP_ID;
		}
		
		return runLeafObjectQueryForMembers(type, attr, RelationOp.EQUALS, id);
	}

	private List<MemberDTO> getMembers(List<LeafObject> leafObjs){
		List<MemberDTO> memberDTOs = new ArrayList<>();
		for(LeafObject leafObj : leafObjs) {
			memberDTOs.add(new MemberDTO(leafObj.getId(), leafObj.getName(), leafObj.getType().getName(), leafObj.getUniqueName(), leafObj.getUid(), leafObj.getDomainName()));
		}
		return memberDTOs;
	}


	public static SpecType getSpecType(String name) {
		if (name.equalsIgnoreCase(LeafObjectType.APPLICATION.getName())) {
			return SpecType.APPLICATION;
		}
		if (name.equalsIgnoreCase(LeafObjectType.HOST.getName())) {
			return SpecType.HOST;
		}
		if (name.equalsIgnoreCase(LeafObjectType.USER.getName()) || name.equalsIgnoreCase(PolicyModelType.SUBJECT.name())) {
			return SpecType.USER;
		}
		return SpecType.RESOURCE;
	}

	public static List<LeafObjectType> getRelatedListObjectTypes(SpecType specType) {
		List<LeafObjectType> list = new ArrayList<>();
		if (specType == SpecType.USER) {
			list.add(LeafObjectType.USER);
			list.add(LeafObjectType.USER_GROUP);
		} else if (specType == SpecType.HOST) {
			list.add(LeafObjectType.HOST);
			list.add(LeafObjectType.HOST_GROUP);
		} else if (specType == SpecType.APPLICATION) {
			list.add(LeafObjectType.APPLICATION);
		}
		return list;
	}

	public static SpecType getMemberType(LeafObjectType leafObjectType) {
		if (LeafObjectType.HOST.equals(leafObjectType) || LeafObjectType.HOST_GROUP.equals(leafObjectType)) {
			return SpecType.HOST;
		} else if (LeafObjectType.APPLICATION.equals(leafObjectType)) {
			return SpecType.APPLICATION;
		}
		return SpecType.USER;
	}
	
	public static String getMemberType(String memberType) {
        return getMemberType(LeafObjectType.forName(memberType)).getName();
	}
	
	
	/**
	 * This method returns a list of members that belong to a group.
	 * The number of members returned depends on the pageSize parameter passed.
	 * 
	 * @param memberPreviewDTO 
	 * @param startIndex 
	 * @param pageSize 
	 * @return List of LeafObjects that belong to that group
	 * @throws PolicyEditorException
	 */
	private List<LeafObject> fetchMembersByGroupId(MemberPreviewDTO memberPreviewDTO, int startIndex, int pageSize) throws PolicyEditorException{
		LeafObjectSearchSpec leafObjectSearchSpec;
		SubjectAttribute attr;
		IPredicate pred;
        SubjectAttribute attrHost = memberPreviewDTO.getType().equalsIgnoreCase(LeafObjectType.HOST.getName())
                                        ? SubjectAttribute.HOST_LDAP_GROUP_ID
                                        : SubjectAttribute.APP_ID;
        attr = memberPreviewDTO.getType().equalsIgnoreCase(LeafObjectType.USER.getName())
                        ? SubjectAttribute.USER_LDAP_GROUP_ID
                        : attrHost;
		
		pred = attr.buildRelation(RelationOp.EQUALS,Constant.build(memberPreviewDTO.getId()));
		
		leafObjectSearchSpec = new LeafObjectSearchSpec(LeafObjectType.forName(memberPreviewDTO.getType()), pred, null,startIndex,pageSize);
		
		List<LeafObject> members = new ArrayList<>();
		members.addAll(dpsService.getPolicyEditorClient().runLeafObjectQuery(leafObjectSearchSpec));
		return members;
	}

	@Override
	public List<String> findGroupMembers(MemberPreviewDTO memberPreviewDTO, int startIndex, int pageSize) throws PolicyEditorException {
		List<String> memberNames = new ArrayList<>();
		List<LeafObject> members = fetchMembersByGroupId(memberPreviewDTO, startIndex, pageSize);
		for(LeafObject member: members) {
			if(member.getName() != null) {
				memberNames.add(member.getName());
			} else if(member.getUniqueName() != null) {
				memberNames.add(member.getUniqueName());
			}	
		}

		return memberNames;
	}
	
	@Override
	public List<String> filterMembersByName(MemberPreviewDTO memberPreviewDTO) throws PolicyEditorException {

		List<String> memberNames = new ArrayList<>();
		List<LeafObject> members = new ArrayList<>();
		
		members.addAll(fetchMembersByGroupId(memberPreviewDTO, 0, 65336));

		for(LeafObject member: members) {
			if(member.getName() != null && (member.getName().toLowerCase()).contains(memberPreviewDTO.getSearchText().toLowerCase())) {
				memberNames.add(member.getName());
			} else if(member.getName() == null && member.getUniqueName() != null && (member.getUniqueName().toLowerCase()).contains(memberPreviewDTO.getSearchText().toLowerCase())) {
				memberNames.add(member.getUniqueName());
			}
		}

		return memberNames;
	}
	
	@Override
	public int getTotalCountofMembers(MemberPreviewDTO memberPreviewDTO) throws PolicyEditorException {

		LeafObjectSearchSpec leafObjectSearchSpec;
		SubjectAttribute attr;
		IPredicate pred;
		SubjectAttribute attrHost = memberPreviewDTO.getType().equalsIgnoreCase(LeafObjectType.HOST.getName())
                        ? SubjectAttribute.HOST_LDAP_GROUP_ID
                        : SubjectAttribute.APP_ID;
        attr = memberPreviewDTO.getType().equalsIgnoreCase(LeafObjectType.USER.getName())
                        ? SubjectAttribute.USER_LDAP_GROUP_ID
                        : attrHost;

		pred = attr.buildRelation(RelationOp.EQUALS, Constant.build(memberPreviewDTO.getId()));

		leafObjectSearchSpec = new LeafObjectSearchSpec(LeafObjectType.forName(memberPreviewDTO.getType()), pred, null, 0, 65336);

		return dpsService.getPolicyEditorClient().countLeafObjectQuery(leafObjectSearchSpec);
	}
}
