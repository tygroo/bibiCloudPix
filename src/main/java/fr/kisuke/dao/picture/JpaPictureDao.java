package fr.kisuke.dao.picture;

import fr.kisuke.dao.JpaDao;
import fr.kisuke.entity.Pictures;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;


/**
 *
 */
public class JpaPictureDao extends JpaDao<Pictures, Long> implements PictureDao
{

	public JpaPictureDao()
	{
		super(Pictures.class);
	}


	@Override
	@Transactional(readOnly = true)
	public List<Pictures> findAll()
	{
		final CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Pictures> criteriaQuery = builder.createQuery(Pictures.class);

		Root<Pictures> root = criteriaQuery.from(Pictures.class);
		criteriaQuery.orderBy(builder.desc(root.get("date")));

		TypedQuery<Pictures> typedQuery = this.getEntityManager().createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

}
