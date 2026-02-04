package com.money_manager.repository;

import com.money_manager.entity.ExpenseEntity;
import com.money_manager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity,Long> {

    //select * from tbl_incomes where profileId=? order by desc
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_incomes where profileId=? order by desc limit 5;
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id=:profileId")
    BigDecimal findTotalIncomesByProfile(@Param("profileId") Long profileId);


    //select * from tbl_incomes where profileId=? and date between ? and ? and name like %?%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort);

    //select * from tbl_incomes where profileId=? and date between ? and ?
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate,LocalDate endDate);
}
