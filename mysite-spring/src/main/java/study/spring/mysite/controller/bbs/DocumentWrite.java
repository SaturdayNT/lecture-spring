package study.spring.mysite.controller.bbs;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.WebHelper;

@Controller
public class DocumentWrite {	
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	@Autowired
	WebHelper web;
	@Autowired
	BBSCommon bbs;

	@RequestMapping(value = "/bbs/document_write.do")
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
		
		return new ModelAndView("bbs/document_write");
	}

}
