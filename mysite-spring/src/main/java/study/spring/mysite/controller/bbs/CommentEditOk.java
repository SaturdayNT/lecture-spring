package study.spring.mysite.controller.bbs;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.spring.helper.RegexHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.BbsCommentService;

@Controller
public class CommentEditOk {	
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	RegexHelper regex;
	@Autowired
	BbsCommentService bbsCommentService;

	@ResponseBody
	@RequestMapping(value = "/comment/comment_edit_ok.do")
	public void doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		/** (2) 페이지 형식 지정 + 사용하고자 하는 Helper+Service 객체 생성 */
		// 페이지 형식을 JSON으로 설정한다.
		response.setContentType("application/json");
		
		web.init();

		/** (3) 파라미터 받기 */
		int commentId = web.getInt("comment_id");
		String writerName = web.getString("writer_name");
		String writerPw = web.getString("writer_pw");
		String email = web.getString("email");
		String content = web.getString("content");
		// 작성자 아이피 주소 가져오기
		String ipAddress = web.getClientIP();
		// 회원 일련번호 --> 비 로그인인 경우 0
		int memberId = 0;

		// 전달된 파라미터는 로그로 확인한다.
		logger.debug("comment_id=" + commentId);
		logger.debug("writer_name=" + writerName);
		logger.debug("writer_pw=" + writerPw);
		logger.debug("email=" + email);
		logger.debug("content=" + content);
		logger.debug("ipAddress=" + ipAddress);
		logger.debug("memberId=" + memberId);

		/** (4) 로그인 한 경우 자신의 글이라면 입력하지 않은 정보를 세션 데이터로 대체한다. */
		// 소유권 검사 정보
		boolean myComment = false;
		
		Member loginInfo = (Member) web.getSession("loginInfo");
		if (loginInfo != null) {
			try {
				// 소유권 판정을 위하여 사용하는 임시 객체
				BbsComment temp = new BbsComment();
				temp.setId(commentId);
				temp.setMemberId(loginInfo.getId());

				if (bbsCommentService.selectCommentCountByMemberId(temp) > 0) {
					// 소유권을 의미하는 변수 변경
					myComment = true;
					// 입력되지 않은 정보들 갱신
					writerName = loginInfo.getName();
					email = loginInfo.getEmail();
					writerPw = loginInfo.getUserPw();
					memberId = loginInfo.getId();
				}
			} catch (Exception e) {
				web.printJsonRt(e.getLocalizedMessage());
			}
		}

		// 전달된 파라미터는 로그로 확인한다.
		logger.debug("commentId=" + commentId);
		logger.debug("writer_name=" + writerName);
		logger.debug("writer_pw=" + writerPw);
		logger.debug("email=" + email);
		logger.debug("content=" + content);
		logger.debug("ipAddress=" + ipAddress);
		logger.debug("memberId=" + memberId);

		/** (5) 입력 받은 파라미터에 대한 유효성 검사 */
		if (commentId == 0) {
			web.printJsonRt("덧글 번호가 없습니다.");
		}
		
		// 이름 + 비밀번호
		if (!regex.isValue(writerName)) {
			web.printJsonRt("작성자 이름를 입력하세요.");
		}

		if (!regex.isValue(writerPw)) {
			web.printJsonRt("비밀번호를 입력하세요.");
		}

		// 이메일
		if (!regex.isValue(email)) {
			web.printJsonRt("이메일을 입력하세요.");
		}

		if (!regex.isEmail(email)) {
			web.printJsonRt("이메일의 형식이 잘못되었습니다.");
		}

		if (!regex.isValue(content)) {
			web.printJsonRt("내용을 입력하세요.");
		}

		/** (6) 입력 받은 파라미터를 Beans로 묶기 */
		BbsComment comment = new BbsComment();
		// UPDATE문의 WHERE절에서 사용해야 하므로 덧글 번호 추가
		comment.setId(commentId);
		comment.setWriterName(writerName);
		comment.setWriterPw(writerPw);
		comment.setEmail(email);
		comment.setContent(content);
		comment.setIpAddress(ipAddress);
		comment.setMemberId(memberId);
		logger.debug(comment.toString());

		/** (7) 게시물 변경을 위한 Service 기능을 호출 */
		BbsComment item = null;
		try {
			// 자신의 글이 아니라면 비밀번호 검사를 먼저 수행한다.
			if (!myComment) {
				bbsCommentService.selectCommentCountByPw(comment);
			}
			bbsCommentService.updateComment(comment);
			// 변경된 결과를 조회
			item = bbsCommentService.selectComment(comment);
		} catch (Exception e) {
			web.printJsonRt(e.getLocalizedMessage());	
		}
		
		/** (8) 처리 결과를 JSON으로 출력하기 */
		// 줄바꿈이나 HTML특수문자에 대한 처리
		item.setWriterName(web.convertHtmlTag(item.getWriterName()));
		item.setEmail(web.convertHtmlTag(item.getEmail()));
		item.setContent(web.convertHtmlTag(item.getContent()));
		
		// --> import java.util.HashMap;
		// --> import java.util.Map;
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rt", "OK");
		data.put("item", item);
		
		// --> import com.fasterxml.jackson.databind.ObjectMapper;
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), data);
		} catch (Exception e) {
			web.printJsonRt(e.getLocalizedMessage());	
		}
	}

}
