package study.spring.mysite.controller.bbs;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import study.spring.helper.FileInfo;
import study.spring.helper.RegexHelper;
import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsDocument;
import study.spring.mysite.model.BbsFile;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.BbsDocumentService;
import study.spring.mysite.service.BbsFileService;

@Controller
public class DocumentWriteOk {
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
	RegexHelper regex;
	@Autowired
	BbsDocumentService bbsDocumentService;
	@Autowired
	BbsFileService bbsFileService;

	@RequestMapping(value = "/bbs/document_write_ok.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
	
		web.init();

		/** (3) 파일이 포함된 POST 파라미터 받기 */
		try {
			upload.multipartRequest();
		} catch (Exception e) {
			return web.redirect(null, "multipart 데이터가 아닙니다.");
		}

		/** (4) UploadHelper에서 텍스트 형식의 값을 추출 */
		Map<String, String> paramMap = upload.getParamMap();
		String category = paramMap.get("category");
		String writerName = paramMap.get("writer_name");
		String writerPw = paramMap.get("writer_pw");
		String email = paramMap.get("email");
		String subject = paramMap.get("subject");
		String content = paramMap.get("content");
		// 작성자 아이피 주소 가져오기
		String ipAddress = web.getClientIP();
		// 회원 일련번호 --> 비 로그인인 경우 0
		int memberId = 0;

		// 로그인 한 경우, 입력하지 않은 이름, 비밀번호, 이메일을 세션정보로 대체
		Member loginInfo = (Member) web.getSession("loginInfo");
		if (loginInfo != null) {
			writerName = loginInfo.getName();
			email = loginInfo.getEmail();
			writerPw = loginInfo.getUserPw();
			memberId = loginInfo.getId();
		}

		// 전달된 파라미터는 로그로 확인한다.
		logger.debug("category=" + category);
		logger.debug("writer_name=" + writerName);
		logger.debug("writer_pw=" + writerPw);
		logger.debug("email=" + email);
		logger.debug("subject=" + subject);
		logger.debug("content=" + content);
		logger.debug("ipAddress=" + ipAddress);
		logger.debug("memberId=" + memberId);

		/** (5) 게시판 카테고리 값을 받아서 View에 전달 */
		// 파일이 첨부된 경우 WebHelper를 사용할 수 없다.
		// String category = web.getString("category");
		request.setAttribute("category", category);

		/** (6) 존재하는 게시판인지 판별하기 */
		try {
			String bbsName = bbs.getBbsName(category);
			request.setAttribute("bbsName", bbsName);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		/** (7) 입력 받은 파라미터에 대한 유효성 검사 */
		// 이름 + 비밀번호
		if (!regex.isValue(writerName)) {
			return web.redirect(null, "작성자 이름를 입력하세요.");
		}

		if (!regex.isValue(writerPw)) {
			return web.redirect(null, "비밀번호를 입력하세요.");
		}

		// 이메일
		if (!regex.isValue(email)) {
			return web.redirect(null, "이메일을 입력하세요.");
		}

		if (!regex.isEmail(email)) {
			return web.redirect(null, "이메일의 형식이 잘못되었습니다.");
		}

		// 제목 및 내용 검사
		if (!regex.isValue(subject)) {
			return web.redirect(null, "글 제목을 입력하세요.");
		}

		if (!regex.isValue(content)) {
			return web.redirect(null, "내용을 입력하세요.");
		}

		/** (8) 입력 받은 파라미터를 Beans로 묶기 */
		BbsDocument document = new BbsDocument();
		document.setCategory(category);
		document.setWriterName(writerName);
		document.setWriterPw(writerPw);
		document.setEmail(email);
		document.setSubject(subject);
		document.setContent(content);
		document.setIpAddress(ipAddress);
		document.setMemberId(memberId);
		logger.debug("document >> " + document.toString());

		/** (9) Service를 통한 게시물 저장 */
		try {
			//for (int i=1; i<=100; i++) {
				//document.setSubject(subject + "(" + i + ")");
				bbsDocumentService.insertDocument(document);
			//}
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		/** (10) 첨부파일 목록 처리 */
		// 업로드 된 파일 목록
		// --> import study.jsp.helper.FileInfo;
		List<FileInfo> fileList = upload.getFileList();
		
		try {
			// 업로드 된 파일의 수 만큼 반복 처리 한다.
			for (int i = 0; i < fileList.size(); i++) {
				// 업로드 된 정보 하나 추출하여 데이터베이스에 저장하기 위한 형태로 가공해야 한다.
				FileInfo info = fileList.get(i);

				// DB에 저장하기 위한 항목 생성
				BbsFile file = new BbsFile();

				// 몇 번 게시물에 속한 파일인지 지정한다.
				file.setBbsDocumentId(document.getId());
				
				// 데이터 복사
				file.setOriginName(info.getOrginName());
				file.setFileDir(info.getFileDir());
				file.setFileName(info.getFileName());
				file.setContentType(info.getContentType());
				file.setFileSize(info.getFileSize());
				
				// 저장처리
				bbsFileService.insertFile(file);
			}
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		
		/** (11) 저장 완료 후 읽기 페이지로 이동하기 */
		// 읽어들일 게시물을 식별하기 위한 게시물 일련번호값을 전달해야 한다.
		String url = "%s/bbs/document_read.do?category=%s&document_id=%d";
		url = String.format(url, web.getRootPath(), document.getCategory(), document.getId());
		return web.redirect(url, null);
	}

}
