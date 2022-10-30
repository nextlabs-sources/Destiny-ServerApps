package com.nextlabs.destiny.console.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bluejungle.pf.destiny.lib.LeafObjectType;
import com.bluejungle.pf.destiny.services.PolicyEditorException;
import com.nextlabs.destiny.console.annotations.ApiVersion;
import com.nextlabs.destiny.console.dto.common.CollectionDataResponseDTO;
import com.nextlabs.destiny.console.dto.common.ConsoleResponseEntity;
import com.nextlabs.destiny.console.dto.common.ResponseDTO;
import com.nextlabs.destiny.console.dto.common.SimpleResponseDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberDTO;
import com.nextlabs.destiny.console.dto.policymgmt.MemberPreviewDTO;
import com.nextlabs.destiny.console.services.MemberService;

@RestController
@ApiVersion(1)
@RequestMapping("/component/members")
public class MemberController extends AbstractRestController {
	
	private static final Logger log = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	private MemberService memberService;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/find")
	public ConsoleResponseEntity<CollectionDataResponseDTO> findMembers(
			@RequestParam(value = "type", required = false) List<String> types,
			@RequestParam String searchString,
			@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "1000", required = false) int pageSize) {

		log.debug("request came to fetch all members for types: {}  and search string: {}", (types == null ? "" : types)
				, searchString);

		List<MemberDTO> membersList = new ArrayList<>();
		CollectionDataResponseDTO response;

		try {
			if (types != null) {
				for (String leafObjectType : types) {
					membersList.addAll(memberService.findMemberBySearchString(LeafObjectType.forName(leafObjectType), searchString));
				}
			}
			PageRequest pageable = PageRequest.of(pageNo, pageSize);
			Page<MemberDTO> membersPage = new PageImpl(membersList);

			if (!membersList.isEmpty()) {
				response = CollectionDataResponseDTO.create(msgBundle.getText("success.data.found.code"), msgBundle.getText("success.data.found"));
				response.setData(membersList);
				response.setPageSize(pageable.getPageSize());
				response.setPageNo(pageable.getPageNumber());
				response.setTotalPages(membersPage.getTotalPages());
				response.setTotalNoOfRecords(membersPage.getTotalElements());
			} else {
				response = CollectionDataResponseDTO.create(msgBundle.getText("no.data.found.code"), msgBundle.getText("no.data.found"));
			}

		} catch (PolicyEditorException e) {
			response = CollectionDataResponseDTO.create(msgBundle.getText("no.data.found.code"),
					msgBundle.getText("no.data.found"));
		}
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/preview")
	public ConsoleResponseEntity<ResponseDTO> previewMembers(@RequestBody MemberPreviewDTO memberPreviewDTO) {
		log.debug("fetching preview");
		
		validations.assertNotNull(memberPreviewDTO, "Member Preview DTO");
		validations.assertNotBlank(memberPreviewDTO.getType(), "Member Type");
		
		log.debug("Member's Id: {}", memberPreviewDTO.getId());
		
		ResponseDTO response;
		int startIndex = 0;
		try {
			String[] type = memberPreviewDTO.getType().split(" ");
			memberPreviewDTO.setType(type[0].toUpperCase());
			memberPreviewDTO.setGroup(true);
			startIndex = memberPreviewDTO.getPageNo() * memberPreviewDTO.getPageSize();
			memberPreviewDTO.setMembers(memberService.findGroupMembers(memberPreviewDTO, startIndex, memberPreviewDTO.getPageSize()));
			if(!memberPreviewDTO.getMembers().isEmpty() && memberPreviewDTO.getTotalMembers() == 0)
				memberPreviewDTO.setTotalMembers(memberService.getTotalCountofMembers(memberPreviewDTO));
			memberPreviewDTO.setType(StringUtils.capitalize(memberPreviewDTO.getType().toLowerCase()) + " Group");
			
			response = SimpleResponseDTO.create(msgBundle.getText("success.data.found.code"),
					msgBundle.getText("success.data.found"), memberPreviewDTO);
		} catch (Exception e) {
			log.error("Error in fetching the preview of the group :", e);
			response = ResponseDTO.create(msgBundle.getText("no.data.found.code"), msgBundle.getText("no.data.found"));
		}
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/preview/filter")
	public ConsoleResponseEntity<ResponseDTO> filterMembers(@RequestBody MemberPreviewDTO memberPreviewDTO) {
		log.debug("filtering preview");
		validations.assertNotNull(memberPreviewDTO, "Member Preview DTO");
		validations.assertNotBlank(memberPreviewDTO.getType(), "Member Type");
		
		ResponseDTO response;
		try {
			String[] type = memberPreviewDTO.getType().split(" ");
			memberPreviewDTO.setType(type[0].toUpperCase());
			memberPreviewDTO.setGroup(true);
			
			if (memberPreviewDTO.getSearchText() != null && !memberPreviewDTO.getSearchText().isEmpty())
				memberPreviewDTO.setMembers(memberService.filterMembersByName(memberPreviewDTO));
			else
				memberPreviewDTO.setMembers(memberService.findGroupMembers(memberPreviewDTO, 0, 20));

			memberPreviewDTO.setType(StringUtils.capitalize(memberPreviewDTO.getType().toLowerCase()) + " Group");
			response = SimpleResponseDTO.create(msgBundle.getText("success.data.found.code"),
					msgBundle.getText("success.data.found"), memberPreviewDTO);
		} catch (Exception e) {
			log.error("Error in fetching the preview of the group :", e);
			response = ResponseDTO.create(msgBundle.getText("no.data.found.code"), msgBundle.getText("no.data.found"));
		}
		return ConsoleResponseEntity.get(response, HttpStatus.OK);
	}
	
	
	@Override
	protected Logger getLog() {
		return log;
	}

}
