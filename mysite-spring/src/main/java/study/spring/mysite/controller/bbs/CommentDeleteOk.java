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

import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.BbsCommentService;


@Controller
public class CommentDeleteOk {	
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	BbsCommentService bbsCommentService;

	@ResponseBody
	@RequestMapping(value = "/comment/comment_delete_ok.do")
	public void doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		
		/** (2) 페이지 형식 지정 + 사용하고자 하는 Helper+Service 객체 생성 */
		// 페이지 형식을 JSON으로 설정한다.
		response.setContentType("application/json");
		
		web.init();
		
		/** (3) 덧글번호와 비밀번호 받기 */
		int commentId = web.getInt("comment_id");
		String writerPw = web.getString("writer_pw");
		
		logger.debug("commentId=" + commentId);
		logger.debug("writerPw=" + writerPw);
		
		if (commentId == 0) {
			web.printJsonRt("덧글 번호가 없습니다.");
		}
		
		/** (4) 파라미터를 Beans로 묶기 */	
		BbsComment comment = new BbsComment();
		comment.setId(commentId);
		comment.setWriterPw(writerPw);
		
		/** (5) 데이터 삭제 처리 */
		// 로그인 중이라면 회원일련번호를 Beans에 추가한다.
		Member loginInfo = (Member) web.getSession("loginInfo");
		if (loginInfo != null) {
			comment.setMemberId(loginInfo.getId());
		}
		
		try {
			// Beans에 추가된 자신의 회원번호를 사용하여 자신의 덧글임을 판별한다.
			// --> 자신의 덧글이 아니라면 비밀번호 검사
			if (bbsCommentService.selectCommentCountByMemberId(comment) < 1) {
				bbsCommentService.selectCommentCountByPw(comment);
			}
			bbsCommentService.deleteComment(comment);	// 덧글삭제
		} catch (Exception e) {
			web.printJsonRt(e.getLocalizedMessage());
		}
		
		/** (6) 처리 결과를 JSON으로 출력하기 */
		// --> import java.util.HashMap;
		// --> import java.util.Map;
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rt", "OK");
		data.put("commentId", commentId);
		
		// --> import com.fasterxml.jackson.databind.ObjectMapper;
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), data);
		} catch (Exception e) {
			web.printJsonRt(e.getLocalizedMessage());	
		}
	}

}
