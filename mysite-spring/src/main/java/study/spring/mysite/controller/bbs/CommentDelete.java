package study.spring.mysite.controller.bbs;

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

import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.BbsCommentService;


@Controller
public class CommentDelete {
	
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	BbsCommentService bbsCommentService;

	@RequestMapping(value = "/comment/comment_delete.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
	
		web.init();
		
		/** (3) 덧글 번호 받기 */
		int commentId = web.getInt("comment_id");
		if (commentId == 0) {
			return web.redirect(null, "덧글 번호가 없습니다.");
		}
		
		// 파라미터를 Beans로 묶기
		BbsComment comment = new BbsComment();
		comment.setId(commentId);
		
		// 로그인 한 경우 현재 회원의 일련번호를 추가한다. (비로그인 시 0으로 설정됨)
		Member loginInfo = (Member) web.getSession("loginInfo");
		if (loginInfo != null) {
			comment.setMemberId(loginInfo.getId());
		}
		
		/** (4) 게시물 일련번호를 사용한 데이터 조회 */	
		// 회원번호가 일치하는 게시물 수 조회하기
		int commentCount = 0; 
		try {
			commentCount = bbsCommentService.selectCommentCountByMemberId(comment);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}
		
		/** (5) 자신의 글에 대한 요청인지에 대한 여부를 view에 전달 */
		boolean myComment = commentCount > 0;
		logger.debug("myComment = " + myComment);
		request.setAttribute("myComment", myComment);
		
		// 상태유지를 위하여 게시글 일련번호를 View에 전달한다.
		request.setAttribute("commentId", commentId);
		
		return new ModelAndView("bbs/comment_delete");
	}
}
