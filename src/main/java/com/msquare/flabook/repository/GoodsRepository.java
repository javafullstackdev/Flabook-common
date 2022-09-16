package com.msquare.flabook.repository;

import com.msquare.flabook.models.shop.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, String> {

}
