package com.pageconnect.booknetwork.history;

import com.pageconnect.booknetwork.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookTransactionHistoryRepository  extends JpaRepository<BookTransactionHistory, Integer> {
    /**
     * Fetches all book transaction histories for a specific user.
     * The alias 'history' is used to reference the BookTransactionHistory entity throughout the query.
     * Ensure consistency when using aliases to avoid runtime errors.
     */
    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.user.id = :userId
        """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);


    @Query("""
        SELECT history
        FROM BookTransactionHistory history
        WHERE history.book.owner.id = :userId
        """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer id);
}
