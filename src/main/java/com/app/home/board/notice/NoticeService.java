package com.app.home.board.notice;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.home.board.BoardDAO;
import com.app.home.board.BoardVO;
import com.app.home.file.FileDAO;
import com.app.home.file.FileVO;
import com.app.home.user.UserVO;
import com.app.home.util.FileManager;
import com.app.home.util.Pager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class NoticeService {

	@Autowired
	private BoardDAO boardDAO;
	@Value("${app.file.base.board}")
	private String path;
	@Autowired
	private FileManager fileManager;
	@Autowired
	private FileDAO fileDAO;

	public boolean checkValid(UserVO userVO ,BoardVO boardVO) {
		boolean chkId = false;
		boolean chkTitle = false;
		try {
			for(GrantedAuthority rr : userVO.getAuthorities()) {
				if(rr.getAuthority().equals("관리자")) {
					chkId = true;
				}
			}
			
			
			chkTitle = boardVO.getTitle()!=null;
		}catch(Exception exception) {
			return false;
		}


		return chkId&chkTitle;
	}

	public int setNotice(BoardVO boardVO) throws Exception{
		//sort에 1 (notice) 세팅
		boardVO.setSort("공지");

		int result = boardDAO.setBoard(boardVO);

		if(result == 1 && boardVO.getMultipartFiles() !=null) {
			for(MultipartFile file : boardVO.getMultipartFiles()) {
				if(file.getOriginalFilename()!="") {
					FileVO fileVO = new FileVO();
					String fileName = fileManager.saveFileS3(file);
					fileVO.setFileName(fileName);
					fileVO.setOriName(file.getOriginalFilename());
					fileVO.setBoardId(boardVO.getId());
					fileVO.setFileSize(fileManager.calFileSize(file));

					int result2 =fileDAO.setFile(fileVO);
				}
			}
		}


		return result;
	}

	public BoardVO getDetail(BoardVO boardVO) throws Exception{
		boardVO = boardDAO.getDetail(boardVO);

		return boardVO;
	}

	public int setUpdate(BoardVO boardVO) throws Exception{
		int result = boardDAO.setUpdate(boardVO);

		if(result == 1 && boardVO.getMultipartFiles() !=null) {
			for(MultipartFile file : boardVO.getMultipartFiles()) {
				if(file.getOriginalFilename()!="") {
					FileVO fileVO = new FileVO();
					String fileName = fileManager.saveFileS3(file);
					fileVO.setFileName(fileName);
					fileVO.setOriName(file.getOriginalFilename());
					fileVO.setBoardId(boardVO.getId());
					fileVO.setFileSize(fileManager.calFileSize(file));

					int result2 =fileDAO.setFile(fileVO);
				}
			}
		}

		return result;
	}
	
	public List<BoardVO> getList(Pager pager) throws Exception {
		
		Long totalCount = boardDAO.getTotalCount(pager);
		pager.getNum(totalCount);
		pager.getRowNum();
		
		return boardDAO.getList(pager);
	}
	
	//조회수순으로 리스트 불러오기
	public List<BoardVO> getListByHit(Pager pager) throws Exception {
		
		Long totalCount = boardDAO.getTotalCount(pager);
		pager.getNum(totalCount);
		pager.getRowNum();
		
		return boardDAO.getListByHit(pager);
	}
	
	public int setHit(BoardVO boardVO) throws Exception {
		return boardDAO.setHit(boardVO);
	}
	
	public int setDelete(BoardVO boardVO) throws Exception{
		int result = boardDAO.setDelete(boardVO);
		
		return result;
	}

}
