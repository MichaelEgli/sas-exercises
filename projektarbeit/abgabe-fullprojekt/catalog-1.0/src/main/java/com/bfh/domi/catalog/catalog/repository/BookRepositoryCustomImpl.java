package com.bfh.domi.catalog.catalog.repository;

import com.bfh.domi.catalog.catalog.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final EntityManager em;

    public BookRepositoryCustomImpl(EntityManager em) {
        this.em = em;
    }

    // Query 4: Finde alle Bücher (Book) nach einer beliebigen Anzahl von Keywords (Gross-/Kleinschreibung ignorieren).
    // ALLE Keywords müssen im Buch enthalten sein und zwar entweder im Feld Titel ODER Autoren ODER Herausgeber
    @Override
    public List<Book> findBooksByKeywords(Set<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return Collections.emptyList();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        List <Predicate> predicateKeywords = new ArrayList<>();
        Predicate finalPredicate = cb.conjunction();

        for (String keyword : keywords) {
            String searchForKeyword = "%" + keyword.toLowerCase() + "%";
            Predicate predicatesTitle = cb.like(cb.lower(book.get("title")), searchForKeyword);
            Predicate predicatesAuthors = cb.like(cb.lower(book.get("authors")), searchForKeyword);
            Predicate predicatesPublisher = cb.like(cb.lower(book.get("publisher")), searchForKeyword);
            // Kombinieren der Predicates innerhalb eines Keywords mit OR
            predicateKeywords.add(cb.or(predicatesTitle, predicatesAuthors, predicatesPublisher));
            // Kombinieren der Predicates zwischen den Keywords mit AND
            finalPredicate = cb.and(predicateKeywords.toArray(new Predicate[predicateKeywords.size()]));

        }

        cq.select(book).where(finalPredicate);
        return em.createQuery(cq).getResultList();
    }
}
