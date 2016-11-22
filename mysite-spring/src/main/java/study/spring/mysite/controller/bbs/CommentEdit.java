package study.spring.mysite.controller.bbs;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.WebHelper;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.service.BbsCommentService;

@Controller
public class CommentEdit {	
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	//private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	BbsCommentService bbsCommentService;

	@RequestMapping(value = "/comment/comment_edit.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		web.init();

		/** (3) 글 번호 파라미터 받기 */
		int commentId = web.getInt("comment_id");
		if (commentId == 0) {
			return web.redirect(null, "덧글 번호가 지정되지 않았습니다.");
		}

		// 파라미터를 Beans로 묶기
		BbsComment comment = new BbsComment();
		comment.setId(commentId);
		
		/** (4) 덧글 일련번호를 사용한 데이터 조회 */
		// 지금 읽고 있는 덧글이 저장될 객체
		BbsComment readComment = null;

		try {
			readComment = bbsCommentService.selectComment(comment);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		/** (5) 읽은 데이터를 View에게 전달한다. */
		request.setAttribute("comment", readComment);

		return new ModelAndView("bbs/comment_edit");
	}

}
