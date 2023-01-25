package com.itmo.eva;

import com.itmo.eva.mapper.PositionMapper;
import com.itmo.eva.model.entity.Position;
import com.itmo.eva.model.entity.Teacher;
import com.itmo.eva.model.enums.IdentityEnum;
import com.itmo.eva.model.vo.TeacherVo;
import com.itmo.eva.service.TeacherService;
import com.itmo.eva.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

@SpringBootTest
class EvaApplicationTests {

	@Resource
	private PositionMapper positionMapper;

	@Resource
	private TeacherService teacherService;

	@Test
	void contextLoads() {
		List<Position> positionList = positionMapper.selectList(null);
		System.out.println(positionList);
	}

	@Test
	void getById() {
		String content = "Hello world!";
		MailUtil.sendMail("cqkjmx@163.com",content,"测试邮件");
		System.out.println("发送成功");

	}

}
