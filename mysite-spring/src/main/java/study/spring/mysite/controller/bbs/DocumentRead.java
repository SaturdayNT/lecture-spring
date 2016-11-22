package study.spring.mysite.controller.bbs;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.RegexHelper;
import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.model.BbsDocument;
import study.spring.mysite.model.BbsFile;
import study.spring.mysite.service.BbsDocumentService;
import study.spring.mysite.service.BbsFileService;

@Controller
public class DocumentRead {
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	//private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	BBSCommon bbs;
	@Autowired
	UploadHelper upload;
	@Autowired
	RegexHelper regex;
	@Autowired
	BbsDocumentService bbsDocumentService;
	@Autowired
	BbsFileService bbsFileService;

	@RequestMapping(value = "/bbs/document_read.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
	
		web.init();

		/** (3) 게시판 카테고리 값을 받아서 View에 전달 */
		String category = web.getString("category");
		request.setAttribute("category", category);

		/** (4) 존재하는 게시판인지 판별하기 */
		try {
			String bbsName = bbs.getBbsName(category);
			request.setAttribute("bbsName", bbsName);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		/** (5) 글 번호 파라미터 받기 */
		int documentId = web.getInt("document_id");
		if (documentId == 0) {
			return web.redirect(null, "글 번호가 지정되지 않았습니다.");
		}

		// 파라미터를 Beans로 묶기
		BbsDocument document = new BbsDocument();
		document.setId(documentId);
		document.setCategory(category);

		BbsFile file = new BbsFile();
		file.setBbsDocumentId(documentId);

		/** (6) 게시물 일련번호를 사용한 데이터 조회 */
		// 지금 읽고 있는 게시물이 저장될 객체
		BbsDocument readDocument = null;
		// 이전글이 저장될 객체
		BbsDocument prevDocument = null;
		// 다음글이 저장될 객체
		BbsDocument nextDocument = null;
		// 첨부파일 정보가 저장될 객체
		List<BbsFile> fileList = null;

		/** 조회수 중복 갱신 방지 처리 */
		// 카테고리와 게시물 일련번호를 조합한 문자열을 생성
		// ex) document_notice_15
		String cookieKey = "document_" + category + "_" + documentId;
		// 준비한 문자열에 대응되는 쿠키값 조회
		String cookieVar = web.getCookie(cookieKey);

		try {
			// 쿠키값이 없다면 조회수 갱신
			if (cookieVar == null) {
				bbsDocumentService.updateDocumentHit(document);
				// 준비한 문자열에 대한 쿠키를 24시간동안 저장
				web.setCookie(cookieKey, "Y", 60 * 60 * 24);
			}
			readDocument = bbsDocumentService.selectDocument(document);
			prevDocument = bbsDocumentService.selectPrevDocument(document);
			nextDocument = bbsDocumentService.selectNextDocument(document);
			fileList = bbsFileService.selectFileList(file);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());	
		}

		/** (7) 읽은 데이터를 View에게 전달한다. */
		request.setAttribute("readDocument", readDocument);
		request.setAttribute("prevDocument", prevDocument);
		request.setAttribute("nextDocument", nextDocument);
		request.setAttribute("fileList", fileList);

		return new ModelAndView("bbs/document_read");
	}

}
