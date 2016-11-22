package study.spring.mysite.service.impl;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import study.spring.mysite.model.Member;
import study.spring.mysite.service.MemberService;

@Service
public class MemberServiceImpl implements MemberService {
	
	private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
	
	/** MyBatis */
	// --> import org.apache.ibatis.session.SqlSession
	@Autowired
	SqlSession sqlSession;

	@Override
	public void selectUserIdCount(Member member) throws Exception {
		try {
			int result = sqlSession.selectOne("MemberMapper.selectUserIdCount", member);

			// 중복된 데이터가 존재한다면?
			if (result > 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("이미 사용중인 아이디 입니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("아이디 중복검사에 실패했습니다.");
		}
	}

	@Override
	public void selectEmailCount(Member member) throws Exception {
		try {
			int result = sqlSession.selectOne("MemberMapper.selectEmailCount", member);

			// 중복된 데이터가 존재한다면?
			if (result > 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("이미 사용중인 이메일 입니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("이메일 중복검사에 실패했습니다.");
		}
	}

	@Override
	public void insertMember(Member member) throws Exception {
		// 아이디 중복검사 및 이메일 중복검사 호출
		selectUserIdCount(member);
		selectEmailCount(member);

		// 데이터 저장처리 = 가입
		// not null로 설정된 값이 설정되지 않았다면 예외 발생됨.
		try {
			int result = sqlSession.insert("MemberMapper.insertMember", member);
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("저장된 회원정보가 없습니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("회원가입에 실패했습니다.");
		}
	}

	@Override
	public Member selectLoginInfo(Member member) throws Exception {
		Member result = null;

		try {
			result = sqlSession.selectOne("MemberMapper.selectLoginInfo", member);

			// 조회된 데이터가 없다는 것은 WHERE절 조건에 맞는 데이터가 없다는 것.
			// --> WHERE절은 아이디와 비밀번호가 일치하는 항목을 지정하므로,
			// 조회된 데이터가 없다는 것은 아이디나 비밀번호가 잘못되었음을 의미한다.
			if (result == null) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("아이디나 비밀번호가 잘못되었습니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("로그인에 실패했습니다.");
		}

		return result;
	}

	@Override
	public void updateMemberPasswordByEmail(Member member) throws Exception {
		try {
			int result = sqlSession.update("MemberMapper.updateMemberPasswordByEmail", member);
			// 수정된 행의 수가 없다는 것은 WHERE절 조건이 맞지 않기 때문이다.
			// 즉, 입력한 이메일과 일치하는 데이터가 없다는 의미
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("가입된 이메일이 아닙니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("비밀번호 변경에 실패했습니다.");
		}
	}
	
	public void selectMemberPasswordCount(Member member) throws Exception {
		try {
			int result = sqlSession.selectOne("MemberMapper.selectMemberPasswordCount", member);

			// 회원번호와 비밀번호가 일치하는 데이터가 0이라면, 비밀번호가 잘못된 상태
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("잘못된 비밀번호 입니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("비밀번호 검사에 실패했습니다.");
		}
	}

	@Override
	public void deleteMember(Member member) throws Exception {
		try {
			int result = sqlSession.delete("MemberMapper.deleteMember", member);
			// 삭제된 데이터가 없다는 것은 WHERE절의 조건값이 맞지 않다는 의미.
			// 이 경우, 첫 번째 WHERE조건에서 사용되는 id값에 대한 회원을 찾을 수 없다는 의미
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("이미 탈퇴된 회원 입니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("회원탈퇴에 실패했습니다.");
		}
	}

	@Override
	public void updateMember(Member member) throws Exception {
		try {
			int result = sqlSession.update("MemberMapper.updateMember", member);
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("변경된 회원정보가 없습니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("회원정보 수정에 실패했습니다.");
		}
	}

	@Override
	public Member selectMember(Member member) throws Exception {
		Member result = null;
		
		try {
			result = sqlSession.selectOne("MemberMapper.selectMember", member);
			if (result == null) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("조회된 정보가 없습니다.");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw new Exception("회원정보 조회에 실패했습니다.");
		}
		
		return result;
	}

}
