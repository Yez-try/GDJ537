package com.app.home.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.app.home.user.DepartmentVO;
import com.app.home.user.UserVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("messenger")
@Slf4j
public class MessengerController {
	
	@Autowired
	private NoteService noteService;

	@Autowired
	private MessengerService messengerService;
	
	@Autowired
	private PickService pickService;
	
	@GetMapping("chat")
	public ModelAndView getMyChat(HttpSession session)throws Exception{
		Integer id = 10;
		ModelAndView mv = new ModelAndView();
		List<DepartmentVO> dl = messengerService.getDepList();
		List<UserVO> el = messengerService.getEmpList();
		List<UserVO> pl = pickService.getPickList(id.toString());
		
		mv.addObject("myId", id);
		mv.addObject("depList", dl);
		mv.addObject("empList", el);
		mv.addObject("pickList", pl);
		mv.addObject("message", "all");
		return mv;
	}
	
	@PostMapping("searchEmp")
	public ModelAndView getSearchResult(HttpSession session, String kind, String keyword) throws Exception{
		log.info("keyword:{},kind:{}", keyword, kind);
		int id = 10;
		Map<String, String> map = new HashMap<>();
		map.put("keyword", keyword);
		map.put("kind", kind);
		List<UserVO> el = messengerService.getSearchResult(map);
		ModelAndView mv = new ModelAndView();
		mv.addObject("myId", id);
		mv.addObject("empList", el);
		mv.addObject("message", "search");
		mv.setViewName("/messenger/chat");
		return mv;
	}
	
	@GetMapping("pickCheck")
	@ResponseBody
	public int setPickCheck (String myId, String yourId) throws Exception{
		int result = pickService.createPick(myId, yourId);
		log.info("result:{}",result);
		return result;
	}
	
	@PostMapping("pickCancel")
	@ResponseBody
	public int pickCancel (String myId, String yourId) throws Exception{
		int result = pickService.pickCancel(myId, yourId);
		log.info("취소 result:{}",result);
		return result;
	}
	
	//수신함
	@GetMapping("note")
	public ModelAndView getReceiveNoteList(UserVO employeeVO, NotePager notePager)throws Exception{
		ModelAndView mv = new ModelAndView();
		//임시 id
		employeeVO.setId(20221231);

		List<NoteVO> ar = noteService.getReceiveNoteList(employeeVO, notePager);
		
		mv.addObject("list", ar);
		mv.addObject("pager", notePager);
		
		if(ar.size()==0) {
			log.info("=============================비었따");
			mv.addObject("message5", "검색 결과가 없습니다.");
		} else {
			mv.addObject("message5", "");
		}
		
		
		Integer id = 1;
		List<DepartmentVO> dl = messengerService.getDepList();
		List<UserVO> el = messengerService.getEmpList();
		List<UserVO> pl = pickService.getPickList(id.toString());
		
		mv.addObject("myId", id);
		mv.addObject("depList", dl);
		mv.addObject("empList", el);
		mv.addObject("pickList", pl);
		mv.addObject("message", "all");
		
		return mv;
	}
	
	//발신함
	@GetMapping("note/sent")
	@ResponseBody
	public ModelAndView getSendNoteList(UserVO employeeVO, NotePager notePager)throws Exception{
		
		
		
		ModelAndView mv = new ModelAndView("jsonView");
		//임시 id
		employeeVO.setId(20221231);
		List<NoteVO> ar = noteService.getSendNoteList(employeeVO, notePager);
		mv.addObject("list", ar);
		
		
		mv.addObject("pager", notePager);
		
		
		if(ar.size()==0) {
			log.info("=============================발신비었따");
			mv.addObject("message5", "검색 결과가 없습니다.");
		} else {
			mv.addObject("message5", "");
		}
		return mv;
	}
	
	//쪽지 상세
	@GetMapping("note/detail")
	public ModelAndView getNoteDetail(NoteVO noteVO)throws Exception{
		ModelAndView mv = new ModelAndView();
		noteVO = noteService.getNoteDetail(noteVO);
		mv.addObject("detail", noteVO);
		mv.setViewName("messenger/note/detail");
		return mv;
	}
	
	//쪽지발송
	@GetMapping("note/send")
	public ModelAndView setSendNote(UserVO employeeVO)throws Exception{
		ModelAndView mv = new ModelAndView();
		employeeVO.setId(20221231);
		mv.addObject("member", employeeVO);
		return mv;
	}
	
	@PostMapping("note/send")
	public ModelAndView setSendNote(NoteVO noteVO)throws Exception{
		ModelAndView mv = new ModelAndView();
		String message = "";
		int result = noteService.setSendNote(noteVO);
		if(result==1) {
			message = "쪽지가 발송되었습니다.";
		} else {
			message = "쪽지 발송에 실패했습니다.";
		}
		mv.addObject("message", message);
		mv.setViewName("messenger/note/sendAfter");
		return mv;
	}
	
	// --------------------- 효경 ------------------------------
	
	
	// 채팅방 목록
	@GetMapping("addRoom")
	public String setAddRoom()throws Exception{
		
		ModelAndView mv = new ModelAndView();
		
		List<RoomVO> roomVOs = new ArrayList<>();
		
		roomVOs = messengerService.getRoomList();
		
		mv.addObject("roomList", roomVOs);
		mv.setViewName("messenger/room/addRoom");
		
		return "messenger/room/addRoom";
	}
	
	// 채팅방 추가
	@PostMapping("addRoom")
	public ModelAndView setAddRoom(HttpSession session, RoomVO roomVO)throws Exception{
		
		//임시 ID
		roomVO.setHostId(5678);
		ModelAndView mv = new ModelAndView();
		
		for(Integer id : roomVO.getId()) {
			UserVO userVO = new UserVO();
			userVO.setId(id);
		}
		
		int result = messengerService.setAddRoom(roomVO);
		
		
		mv.setViewName("messenger/chat");
		
		return mv;
	}
	// --------------------- 유리 ------------------------------
	
	//-----------
	
	@GetMapping("chat1")
	public ModelAndView chat()throws Exception{
		ModelAndView mv= new ModelAndView();
	
		mv.setViewName("messenger/chat1");
		return mv;
	}

	@GetMapping("chatroom")
	public ModelAndView chat3()throws Exception{
		ModelAndView mv= new ModelAndView();
	
		mv.setViewName("redirect:../messenger/chat");
		return mv;
	}

	
}



