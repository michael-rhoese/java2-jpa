package de.fherfurt.jpa.storages;

import de.fherfurt.jpa.domains.BaseEntity;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<E extends BaseEntity> {

    protected final Class<E> type;
    protected final EntityManager entityManager;

    public Long save(E entity){

        entityManager.getTransaction().begin();
        if (Objects.isNull(entity.getId())){
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        entityManager.getTransaction().commit();
        return entity.getId();
    }

    public void delete(E entity){
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    public int deleteAll(){
        entityManager.getTransaction().begin();

        final String sql = "DELETE FROM " + type.getCanonicalName();
        int entityCount = entityManager.createQuery(sql).getFirstResult();
        entityManager.getTransaction().commit();

        return entityCount;
    }

    public Optional<E> findBy(Long id){
        return Optional.ofNullable(entityManager.find(type, id));
    }

    protected final static String SELECT_FROM = "SELECT entity FROM ";

    public List<E> findAll(){
        final String sql = SELECT_FROM + type.getCanonicalName() + " entity";
        return entityManager.createQuery(sql, type).getResultList();
    }
}
