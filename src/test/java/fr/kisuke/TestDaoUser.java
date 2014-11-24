package fr.kisuke;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.kisuke.dao.user.UserDao;
import fr.kisuke.entity.Users;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/resources/context.xml"} )
public class TestDaoUser {
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void testFindUserByEmail() {
		

		Users userUser = new Users("user2", this.passwordEncoder.encode("user"));
		userUser.addRole("user");
		
//		 Users adminUser = userDao.find(1L);
//			adminUser.addRole("user");
//			adminUser.addRole("admin");
//			userDao.save(adminUser);
			
		
		//this.userDao.save(userUser);
		//this.userDao.persiste(userUser);
		
		// Users user = new Users("user1", passwordEncoder.encode("user"));
		userDao.save(userUser);
		Users usrTest = userDao.find(1L);
		usrTest.addRole("user");
		userDao.save(usrTest);

		assertThat(userDao.findAll()).isNotEmpty();
		assertThat(userDao.findAll().size()).isGreaterThan(1);
	}
}
