package study.spring.mysite.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;

@Controller
public class Download {
	/** (1) 사용하고자 하는 Helper + Service 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	WebHelper web;
	@Autowired
	UploadHelper upload;
	
	@RequestMapping(value = "/download.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
	
		web.init();
		
		/** (3) 파라미터 받기 */
		// 서버상에 저장되어 있는 파일경로 (필수)
		String filePath = web.getString("file");
		// 원본 파일이름 (미필수)
		String orginName = web.getString("orgin");

		/** (4) 다운로드 스트림 출력하기 */
		if (filePath != null) {
			try {
				logger.debug("Create Thumbnail Image --> " + filePath);
				upload.printFileStream(filePath, orginName);
			} catch (IOException e) {
				logger.debug(e.getLocalizedMessage());
			}
		}
		
		return null;
	}
}
