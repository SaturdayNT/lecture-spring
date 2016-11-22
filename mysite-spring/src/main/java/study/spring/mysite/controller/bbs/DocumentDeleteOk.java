package study.spring.mysite.controller.bbs;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.model.BbsDocument;
import study.spring.mysite.model.BbsFile;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.BbsCommentService;
import study.spring.mysite.service.BbsDocumentService;
import study.spring.mysite.service.BbsFileService;

@Controller
public class DocumentDeleteOk {
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	BBSCommon bbs;
	@Autowired
	UploadHelper upload;
	@Autowired
	BbsDocumentService bbsDocumentService;
	@Autowired
	BbsCommentService bbsCommentService;
	@Autowired
	BbsFileService bbsFileService;

	@RequestMapping(value = "/bbs/document_delete_ok.do")
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
		
		/** (5) 게시글 번호와 비밀번호 받기 */
		int documentId = web.getInt("document_id");
		String writerPw = web.getString("writer_pw");
		
		logger.debug("documentId=" + documentId);
		logger.debug("writerPw=" + writerPw);
		
		if (documentId == 0) {
			return web.redirect(null, "글 번호가 없습니다.");
		}
		
		/** (6) 파라미터를 Beans로 묶기 */	
		BbsDocument document = new BbsDocument();
		document.setId(documentId);
		document.setCategory(category);
		document.setWriterPw(writerPw);
		
		BbsFile file = new BbsFile();
		file.setBbsDocumentId(documentId);
		
		// 게시물에 속한 덧글 삭제를 위해서 생성
		BbsComment comment = new BbsComment();
		comment.setBbsDocumentId(documentId);
		
		/** (7) 데이터 삭제 처리 */
		// 로그인 중이라면 회원일련번호를 Beans에 추가한다.
		Member loginInfo = (Member) web.getSession("loginInfo");
		if (loginInfo != null) {
			document.setMemberId(loginInfo.getId());
		}
		
		List<BbsFile> fileList = null;	// 게시물에 속한 파일 목록에 대한 조회결과
			
		try {
			// Beans에 추가된 자신의 회원번호를 사용하여 자신의 글임을 판별한다.
			// --> 자신의 글이 아니라면 비밀번호 검사
			if (bbsDocumentService.selectDocumentCountByMemberId(document) < 1) {
				bbsDocumentService.selectDocumentCountByPw(document);
			}
			
			fileList = bbsFileService.selectFileList(file);	// 게시글에 포함된 파일목록을 조회
			bbsFileService.deleteFileAll(file);				// 게시물에 속한 파일목록 모두 삭제
			
			// 덧글이 게시물을 참조하므로, 덧글이 먼저 삭제되어야 한다.
			bbsCommentService.deleteCommentAll(comment);
			bbsDocumentService.deleteDocument(document);	// 게시글 삭제
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}
		
		/** (8) 실제 파일을 삭제한다. */
		// DB에서 파일 정보가 삭제되더라도 실제 저장되어 있는 파일 자체가 삭제되는 것은 아니다.
		// 실제 파일도 함께 삭제하기 위해서 (7)번 절차에서 파일정보를 삭제하기 전에 미리
		// 조회해 둔 것이다.
		// 조회한 파일 목록만큼 반복하면서 저장되어 있는 파일을 삭제한다.
		if (fileList != null) {
			for (int i=0; i<fileList.size(); i++) {
				BbsFile f = fileList.get(i);
				String filePath = f.getFileDir() + "/" + f.getFileName();
				logger.debug("fileRemove: " + filePath);
				upload.removeFile(filePath);
			}
		}
		
		/** (9) 페이지 이동 */
		String url = "%s/bbs/document_list.do?category=%s";
		url = String.format(url, web.getRootPath(), category);
		
		return web.redirect(url, "삭제되었습니다.");
	}

}
