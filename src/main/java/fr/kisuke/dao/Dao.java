package fr.kisuke.dao;

import java.util.List;

import fr.kisuke.entity.Entity;


public interface Dao<T extends Entity, I>
{

	List<T> findAll();


	T find(I id);


	T save(T newsEntry);


	void delete(I id);
	
	void persiste(T entity);

}