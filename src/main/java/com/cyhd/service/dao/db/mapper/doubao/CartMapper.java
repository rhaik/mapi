package com.cyhd.service.dao.db.mapper.doubao;

/*
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.Cart;
 

@Repository
public interface CartMapper {

	@Select("select * from money_cart where user_id=#{0}")
	public List<Cart> getCartList(int userId);


	@Select("select count(*) from money_cart where user_id=#{0} and product_id=#{1}")
	public int getCartProductByProductId(int userId, int productId);

	@Select("select count(*) from money_cart where user_id=#{0}")
	public int countCartByUserId(int userId);

	@Insert("INSERT INTO `money_cart` (`user_id`, `product_activity_id`, `product_id`, `number`, `createtime`) VALUES "
			+ "(#{user_id}, #{product_activity_id}, #{product_id}, #{number}, now())")
	public int add(Cart cart);

	@Update("Update money_cart set product_activity_id=#{2},number=number+#{3} where user_id=#{0} AND product_id=#{1}")
	public int addCartNumber(int userId, int productId, int productActivityId, int number);

	@Update("Update money_cart set product_activity_id=#{2},number=#{3} where user_id=#{0} AND product_id=#{1}")
	public int updateCartNumber(int userId, int productId, int productActivityId, int number);

	@Delete("DELETE FROM `money_cart` WHERE user_id=#{0}")
	public int deleteCart(int userId);

	@Delete("DELETE FROM `money_cart` WHERE user_id=#{0} AND product_activity_id=#{1}")
	public int deleteCartProductActivityId(int userId, int productActivityId);

}
*/