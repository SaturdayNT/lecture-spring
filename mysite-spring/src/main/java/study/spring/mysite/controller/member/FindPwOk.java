package study.spring.mysite.controller.member;

import java.util.Locale;

import javax.mail.MessagingException;
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

import study.spring.helper.MailHelper;
import study.spring.helper.Util;
import study.spring.helper.WebHelper;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.MemberService;

@Controller
public class FindPwOk {

	private static Logger logger = LoggerFactory.getLogger(FindPw.class);

	@Autowired
	SqlSession sqlSession;
	
	@Autowired
	WebHelper web;
	
	@Autowired
	MailHelper mail;
	
	@Autowired
	Util util;
	
	@Autowired
	MemberService memberService;
	
	@RequestMapping(value = "/member/find_pw_ok.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
	
		web.init();

		/** (3) 로그인 여부 검사 */
		// 로그인 중이라면 이 페이지를 이용할 수 없다.
		if (web.getSession("loginInfo") != null) {
			
			return web.redirect(web.getRootPath() + "/index.do", "이미 로그인 중입니다.");
			
		}
		
		/** (4) 파라미터 받기 */
		// 입력된 메일 주소를 받는다.
		String email = web.getString("email");

		logger.debug("email=" + email);
		
		if (email == null) {
			
			return web.redirect(null, "이메일 주소를 입력하세요.");
			
		}
		
		/** (5) 임시 비밀번호 생성하기 */
		String newPassword = util.getRandomPassword();
		
		/** (6) 입력값을 JavaBeans에 저장하기 */
		Member member = new Member();
		member.setEmail(email);
		member.setUserPw(newPassword);
		
		/** (7) Service를 통한 비밀번호 갱신 */		
		try {
			memberService.updateMemberPasswordByEmail(member);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
			
		} finally {
			
		}
		
		/** (8) 발급된 비밀번호를 메일로 발송하기 */
		String sender = "webmaster@mysite.com";
		String subject = "MySite 비밀번호 변경 안내 입니다.";
		String content = "회원님의 새로운 비밀번호는 <strong>" + newPassword + "</strong>입니다.";
		
		try {
			// 사용자가 입력한 메일주소를 수신자로 설정하여 메일 발송하기
			mail.sendMail(sender, email, subject, content);
		} catch (MessagingException e) {
			return web.redirect(null, "메일 발송에 실패했습니다. 관리자에게 문의 바랍니다.");
			
		}
		
		/** (9) 결과 페이지로 이동 */
		// 여기서는 이전 페이지로 이동함
		return web.redirect(null, "새로운 비밀번호가 메일로 발송되었습니다.");
		
	}

}
