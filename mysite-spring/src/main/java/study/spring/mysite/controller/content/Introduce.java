package study.spring.mysite.controller.content;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Introduce {
	@RequestMapping(value = "/introduce.do")
	public ModelAndView doRun(Locale locale, Model model, 
			HttpServletRequest request, HttpServletResponse response) {
		// "/WEB-INF/views/content/introduce.jsp" 파일을 View로 사용한다.
		return new ModelAndView("content/introduce");
	}
}
