package study.spring.mysite.controller.bbs;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.service.BbsCommentService;

@Controller
public class CommentList {
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
	@RequestMapping(value = "/comment/comment_list.do")
	public void doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		/** (2) 페이지 형식 지정 + 사용하고자 하는 Helper+Service 객체 생성 */
		// 페이지 형식을 JSON으로 설정한다.
		response.setContentType("application/json");
		
		web.init();
		
		/** (3) 파라미터 받기 */
		int bbsDocumentId = web.getInt("document_id");
		logger.debug("bbs_document_id=" + bbsDocumentId);
		
		/** (4) 입력 받은 파라미터에 대한 유효성 검사 */
		// 덧글이 소속될 게시물의 일련번호
		if (bbsDocumentId == 0) {
			web.printJsonRt("게시물 일련번호가 없습니다.");
		}
		
		/** (5) 입력 받은 파라미터를 Beans로 묶기 */
		BbsComment comment = new BbsComment();
		comment.setBbsDocumentId(bbsDocumentId);
		
		/** (6) Service를 통한 덧글 목록 조회 */
		// 작성 결과를 저장할 객체
		List<BbsComment> item = null;
		try {
			item = bbsCommentService.selectCommentList(comment);
		} catch (Exception e) {
			web.printJsonRt(e.getLocalizedMessage());
		}
		
		/** (7) 처리 결과를 JSON으로 출력하기 */
		// 줄바꿈이나 HTML특수문자에 대한 처리
		for (int i=0; i<item.size(); i++) {
			BbsComment temp = item.get(i);
			temp.setWriterName(web.convertHtmlTag(temp.getWriterName()));
			temp.setEmail(web.convertHtmlTag(temp.getEmail()));
			temp.setContent(web.convertHtmlTag(temp.getContent()));
		}
		
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
