package fr.kisuke.dao.user;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import fr.kisuke.dao.JpaDao;
import fr.kisuke.entity.Users;


public class JpaUserDao extends JpaDao<Users, Long> implements UserDao
{

	public JpaUserDao()
	{
		super(Users.class);
	}


	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		Users user = this.findByName(username);
		if (null == user) {
			throw new UsernameNotFoundException("The user with name " + username + " was not found");
		}

		return user;
	}


	@Override
	@Transactional(readOnly = true)
	public Users findByName(String name)
	{
		final CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Users> criteriaQuery = builder.createQuery(this.entityClass);

		Root<Users> root = criteriaQuery.from(this.entityClass);
		Path<String> namePath = root.get("name");
		criteriaQuery.where(builder.equal(namePath, name));

		TypedQuery<Users> typedQuery = this.getEntityManager().createQuery(criteriaQuery);
		List<Users> users = typedQuery.getResultList();

		if (users.isEmpty()) {
			return null;
		}

		return users.iterator().next();
	}


}
