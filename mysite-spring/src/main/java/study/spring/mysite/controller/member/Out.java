package study.spring.mysite.controller.member;

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
public class Out {
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	@Autowired
	WebHelper web;

	@RequestMapping(value = "/member/out.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		/** (2) 사용하고자 하는 Helper 객체 생성 */
		web.init();

		/** (3) 로그인 여부 검사 */
		// 로그인 중이 아니라면 탈퇴할 수 없다.
		if (web.getSession("loginInfo") == null) {
			return web.redirect(web.getRootPath() + "/index.do", "로그인 후에 이용 가능합니다.");
		}

		/** (4) 사용할 View의 이름 리턴 */
		return new ModelAndView("member/out");
	}
}
