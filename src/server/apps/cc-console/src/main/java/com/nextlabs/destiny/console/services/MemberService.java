package com.nextlabs.destiny.console.services;

import java.util.List;

import com.bluejungle.pf.destiny.lib.LeafObjectType;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberPreviewDTO;

public interface MemberService {
	
	List<String> findGroupMembers(MemberPreviewDTO memberPreviewDTO, int startIndex, int pageSize) throws PolicyEditorException;

	List<String> filterMembersByName(MemberPreviewDTO memberPreviewDTO) throws PolicyEditorException;

	int getTotalCountofMembers(MemberPreviewDTO memberPreviewDTO) throws PolicyEditorException;

	List<MemberDTO> findMemberBySearchString(LeafObjectType type, String searchString) throws PolicyEditorException;

	List<MemberDTO> findMemberByUniqueName(LeafObjectType type, String uniqueName);

	List<MemberDTO> findMemberById(LeafObjectType type, String id);
}
