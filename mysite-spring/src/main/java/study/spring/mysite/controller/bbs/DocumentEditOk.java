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
public class DocumentEditOk {
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	BBSCommon bbs;
	@Autowired
	UploadHelper upload;
	@Autowired
	RegexHelper regex;
	@Autowired
	BbsDocumentService bbsDocumentService;
	@Autowired
	BbsFileService bbsFileService;

	@RequestMapping(value = "/bbs/document_edit_ok.do")
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

		// 글 번호 가져오기 --> Map에서 가져오는 값은 모두 String이므로 형변환 필요
		int documentId = 0;
		try {
			documentId = Integer.parseInt(paramMap.get("document_id"));
		} catch (NumberFormatException e) {
			return web.redirect(null, "글 번호가 올바르지 않습니다.");
		}

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

		/** (7) 로그인 한 경우 자신의 글이라면 입력하지 않은 정보를 세션 데이터로 대체한다. */
		// 소유권 검사 정보
		boolean myDocument = false;
		
		Member loginInfo = (Member) web.getSession("loginInfo");
		if (loginInfo != null) {
			try {
				// 소유권 판정을 위하여 사용하는 임시 객체
				BbsDocument temp = new BbsDocument();
				temp.setCategory(category);
				temp.setId(documentId);
				temp.setMemberId(loginInfo.getId());

				if (bbsDocumentService.selectDocumentCountByMemberId(temp) > 0) {
					// 소유권을 의미하는 변수 변경
					myDocument = true;
					// 입력되지 않은 정보들 갱신
					writerName = loginInfo.getName();
					email = loginInfo.getEmail();
					writerPw = loginInfo.getUserPw();
					memberId = loginInfo.getId();
				}
			} catch (Exception e) {
				return web.redirect(null, e.getLocalizedMessage());
			}
		}

		// 전달된 파라미터는 로그로 확인한다.
		logger.debug("documentId=" + documentId);
		logger.debug("category=" + category);
		logger.debug("writer_name=" + writerName);
		logger.debug("writer_pw=" + writerPw);
		logger.debug("email=" + email);
		logger.debug("subject=" + subject);
		logger.debug("content=" + content);
		logger.debug("ipAddress=" + ipAddress);
		logger.debug("memberId=" + memberId);

		/** (8) 입력 받은 파라미터에 대한 유효성 검사 */
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

		/** (9) 입력 받은 파라미터를 Beans로 묶기 */
		BbsDocument document = new BbsDocument();
		// UPDATE문의 WHERE절에서 사용해야 하므로 글 번호 추가
		// --> 글 번호는 숫자로 변환해서 처리해야 한다.
		document.setId(documentId);
		document.setCategory(category);
		document.setWriterName(writerName);
		document.setWriterPw(writerPw);
		document.setEmail(email);
		document.setSubject(subject);
		document.setContent(content);
		document.setIpAddress(ipAddress);
		document.setMemberId(memberId);
		logger.debug(document.toString());

		/** (10) 게시물 변경을 위한 Service 기능을 호출 */
		try {
			// 자신의 글이 아니라면 비밀번호 검사를 먼저 수행한다.
			if (!myDocument) {
				bbsDocumentService.selectDocumentCountByPw(document);
			}
			bbsDocumentService.updateDocument(document);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		/** (11) 삭제를 선택한 첨부파일에 대한 처리 */
		// 삭제할 파일 목록에 대한 체크결과 --> 체크박스의 선택값을 paramMap에서 추출
		String delFile = paramMap.get("del_file");

		if (delFile != null) {
			// 콤마 단위로 잘라서 배열로 변환
			String[] delFileList = delFile.split(",");
	
			for (int i = 0; i < delFileList.length; i++) {
				try {
					// 체크박스에 의해서 전달된 id값으로 개별 파일에 대한 Beans 생성
					BbsFile file = new BbsFile();
					file.setId(Integer.parseInt(delFileList[i]));
	
					// 개별 파일에 대한 정보를 조회하여 실제 파일을 삭제한다.
					BbsFile item = bbsFileService.selectFile(file);
					upload.removeFile(item.getFileDir() + "/" + item.getFileName());
	
					// DB에서 파일정보 삭제처리
					bbsFileService.deleteFile(file);
				} catch (Exception e) {
					return web.redirect(null, e.getLocalizedMessage());
				}
			}
		}

		/** (12) 추가적으로 업로드 된 파일 정보 처리 */
		// 업로드 된 파일 목록
		List<FileInfo> fileInfoList = upload.getFileList();

		// 업로드 된 파일의 수 만큼 반복 처리 한다.
		for (int i = 0; i < fileInfoList.size(); i++) {
			// 업로드 된 정보 하나 추출
			// --> 업로드 된 정보를 데이터베이스에 저장하기 위한 형태로 가공해야 한다.
			FileInfo info = fileInfoList.get(i);

			// DB에 저장하기 위한 항목 하나 생성
			BbsFile file = new BbsFile();

			// 데이터 복사
			file.setOriginName(info.getOrginName());
			file.setFileDir(info.getFileDir());
			file.setFileName(info.getFileName());
			file.setContentType(info.getContentType());
			file.setFileSize(info.getFileSize());

			// 어느 게시물에 속한 파일인지 인식해야 하므로 글 번호 추가
			file.setBbsDocumentId(documentId);

			// 복사된 데이터를 DB에 저장
			try {
				bbsFileService.insertFile(file);
			} catch (Exception e) {
				return web.redirect(null, e.getLocalizedMessage());
			}
		}
		

		String url = "%s/bbs/document_read.do?category=%s&document_id=%d";
		url = String.format(url, web.getRootPath(), category, documentId);
		return web.redirect(url, null);
	}

}
