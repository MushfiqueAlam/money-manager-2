package com.money_manager.repository;

import com.money_manager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {

    //select * from tbl_expenses where profileId=? order by desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_expenses where profileId=? order by desc limit 5;
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id=:profileId")
    BigDecimal findTotalExpenseByProfile(@Param("profileId") Long profileId);


    //select * from tbl_expenses where profileId=? and date between ? and ? and name like %?%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    //select * from tbl_expenses where profileId=? and date between ? and ?
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate,LocalDate endDate);

    //select * from tbl_expenses where profile_id=? and date=?
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId,LocalDate date);
}
