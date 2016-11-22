package study.spring.mysite.service.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import study.spring.mysite.model.BbsFile;
import study.spring.mysite.service.BbsFileService;

@Service
public class BbsFileServiceImpl implements BbsFileService {
	
	/** MyBatis */
	// --> import org.apache.ibatis.session.SqlSession
	@Autowired
	SqlSession sqlSession;
	
	@Override
	public void insertFile(BbsFile file) throws Exception {
		try {
			int result = sqlSession.insert("BbsFileMapper.insertFile", file);
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			throw new Exception("저장된 파일정보가 없습니다.");
		} catch (Exception e) {
			throw new Exception("파일정보 등록에 실패했습니다.");
		}
	}

	@Override
	public List<BbsFile> selectFileList(BbsFile file) throws Exception {
		List<BbsFile> result = null;
		
		try {
			// 첨부파일이 없는 경우도 있으므로, 조회결과가 null인 경우 예외를 발생하지 않는다.
			result = sqlSession.selectList("BbsFileMapper.selectFileList", file);
		} catch (Exception e) {
			throw new Exception("파일 정보 조회에 실패했습니다.");
		}
		
		return result;
	}
	
	@Override
	public void deleteFileAll(BbsFile file) throws Exception {
		try {
			// 첨부파일이 없는 경우도 있으므로, 결과가 0인 경우 예외를 발생하지 않는다.
			sqlSession.delete("BbsFileMapper.deleteFileAll", file);
		} catch (Exception e) {
			throw new Exception("첨부파일 정보 삭제에 실패했습니다.");
		}
	}

	@Override
	public BbsFile selectFile(BbsFile file) throws Exception {
		BbsFile result = null;
		
		try {
			result = sqlSession.selectOne("BbsFileMapper.selectFile", file);
			if (result == null) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			throw new Exception("존재하지 않는 파일에 대한 요청입니다.");
		} catch (Exception e) {
			throw new Exception("파일 정보 조회에 실패했습니다.");
		}
		
		return result;
	}

	@Override
	public void deleteFile(BbsFile file) throws Exception {
		try {
			int result = sqlSession.delete("BbsFileMapper.deleteFile", file);
			if (result == 0) {
				throw new NullPointerException();
			}
		} catch (NullPointerException e) {
			throw new Exception("삭제된 파일 정보가 없습니다.");
		} catch (Exception e) {
			throw new Exception("첨부파일 정보 삭제에 실패했습니다.");
		}
	}
}
