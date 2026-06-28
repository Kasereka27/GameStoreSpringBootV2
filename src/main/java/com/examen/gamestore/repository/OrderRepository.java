package com.examen.gamestore.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.examen.gamestore.domain.enums.OrderStatus;
import com.examen.gamestore.domain.enums.Platform;
import com.examen.gamestore.domain.model.Game;
import com.examen.gamestore.domain.model.LicenseKey;
import com.examen.gamestore.domain.model.Order;
import com.examen.gamestore.domain.model.OrderItem;
import com.examen.gamestore.domain.enums.LicenseKeyStatus;
import com.examen.gamestore.web.dto.AdminOrderView;
import com.examen.gamestore.web.dto.DailySalesView;
import com.examen.gamestore.web.dto.LibraryGameView;
import com.examen.gamestore.web.dto.OrderSummaryView;

@Repository
public class OrderRepository {

	private final JdbcClient jdbcClient;

	public OrderRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public UUID insert(Order order) {
		UUID id = order.getId() != null ? order.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO orders (
					id, order_number, user_id, status, subtotal, discount_amount, total_amount,
					payment_method, stripe_payment_intent_id, promo_code_id,
					billing_first_name, billing_last_name, billing_email, billing_phone,
					billing_address, billing_postal_code, billing_city, billing_country,
					created_at, updated_at
				) VALUES (
					:id, :orderNumber, :userId, :status, :subtotal, :discountAmount, :totalAmount,
					:paymentMethod, :stripePaymentIntentId, :promoCodeId,
					:billingFirstName, :billingLastName, :billingEmail, :billingPhone,
					:billingAddress, :billingPostalCode, :billingCity, :billingCountry,
					CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
				)
				""")
				.param("id", id)
				.param("orderNumber", order.getOrderNumber())
				.param("userId", order.getUserId())
				.param("status", order.getStatus().name())
				.param("subtotal", order.getSubtotal())
				.param("discountAmount", order.getDiscountAmount())
				.param("totalAmount", order.getTotalAmount())
				.param("paymentMethod", order.getPaymentMethod())
				.param("stripePaymentIntentId", order.getStripePaymentIntentId())
				.param("promoCodeId", order.getPromoCodeId())
				.param("billingFirstName", order.getBillingFirstName())
				.param("billingLastName", order.getBillingLastName())
				.param("billingEmail", order.getBillingEmail())
				.param("billingPhone", order.getBillingPhone())
				.param("billingAddress", order.getBillingAddress())
				.param("billingPostalCode", order.getBillingPostalCode())
				.param("billingCity", order.getBillingCity())
				.param("billingCountry", order.getBillingCountry())
				.update();
		return id;
	}

	public void insertItem(OrderItem item) {
		UUID id = item.getId() != null ? item.getId() : UUID.randomUUID();
		jdbcClient.sql("""
				INSERT INTO order_items (id, order_id, game_id, license_key_id, unit_price, quantity)
				VALUES (:id, :orderId, :gameId, :licenseKeyId, :unitPrice, :quantity)
				""")
				.param("id", id)
				.param("orderId", item.getOrderId())
				.param("gameId", item.getGameId())
				.param("licenseKeyId", item.getLicenseKeyId())
				.param("unitPrice", item.getUnitPrice())
				.param("quantity", item.getQuantity())
				.update();
	}

	public void updateStatus(UUID orderId, OrderStatus status) {
		jdbcClient.sql("""
				UPDATE orders SET status = :status, updated_at = CURRENT_TIMESTAMP WHERE id = :id
				""")
				.param("id", orderId)
				.param("status", status.name())
				.update();
	}

	public Optional<Order> findById(UUID id) {
		return jdbcClient.sql("""
				SELECT id, order_number, user_id, status, subtotal, discount_amount, total_amount,
				       payment_method, stripe_payment_intent_id, promo_code_id,
				       billing_first_name, billing_last_name, billing_email, billing_phone,
				       billing_address, billing_postal_code, billing_city, billing_country,
				       created_at, updated_at
				FROM orders WHERE id = :id
				""")
				.param("id", id)
				.query(this::mapOrderRow)
				.optional();
	}

	public Optional<Order> findByOrderNumber(String orderNumber) {
		return jdbcClient.sql("""
				SELECT id, order_number, user_id, status, subtotal, discount_amount, total_amount,
				       payment_method, stripe_payment_intent_id, promo_code_id,
				       billing_first_name, billing_last_name, billing_email, billing_phone,
				       billing_address, billing_postal_code, billing_city, billing_country,
				       created_at, updated_at
				FROM orders WHERE order_number = :orderNumber
				""")
				.param("orderNumber", orderNumber)
				.query(this::mapOrderRow)
				.optional();
	}

	public List<OrderSummaryView> findSummariesByUserId(UUID userId) {
		return jdbcClient.sql("""
				SELECT o.id, o.order_number, o.status, o.total_amount, o.created_at,
				       COALESCE(SUM(oi.quantity), 0) AS item_count
				FROM orders o
				LEFT JOIN order_items oi ON oi.order_id = o.id
				WHERE o.user_id = :userId
				GROUP BY o.id, o.order_number, o.status, o.total_amount, o.created_at
				ORDER BY o.created_at DESC
				""")
				.param("userId", userId)
				.query((rs, rowNum) -> {
					OrderSummaryView view = new OrderSummaryView();
					view.setId(UUID.fromString(rs.getString("id")));
					view.setOrderNumber(rs.getString("order_number"));
					view.setStatus(OrderStatus.fromString(rs.getString("status")));
					view.setTotalAmount(rs.getBigDecimal("total_amount"));
					view.setItemCount(rs.getInt("item_count"));
					view.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
					return view;
				})
				.list();
	}

	public List<OrderItem> findItemsByOrderId(UUID orderId) {
		return jdbcClient.sql("""
				SELECT oi.id, oi.order_id, oi.game_id, oi.license_key_id, oi.unit_price, oi.quantity,
				       g.title, g.slug, g.cover_image_url, g.platform,
				       lk.key_value, lk.status AS key_status
				FROM order_items oi
				INNER JOIN games g ON g.id = oi.game_id
				LEFT JOIN license_keys lk ON lk.id = oi.license_key_id
				WHERE oi.order_id = :orderId
				ORDER BY oi.id ASC
				""")
				.param("orderId", orderId)
				.query(this::mapOrderItemRow)
				.list();
	}

	public List<LibraryGameView> findLibraryByUserId(UUID userId) {
		return jdbcClient.sql("""
				SELECT g.id AS game_id, g.title, g.slug, g.cover_image_url,
				       lk.key_value, o.created_at AS purchased_at, o.order_number
				FROM order_items oi
				INNER JOIN orders o ON o.id = oi.order_id
				INNER JOIN games g ON g.id = oi.game_id
				INNER JOIN license_keys lk ON lk.id = oi.license_key_id
				WHERE o.user_id = :userId AND o.status = 'COMPLETED'
				ORDER BY o.created_at DESC
				""")
				.param("userId", userId)
				.query((rs, rowNum) -> {
					LibraryGameView view = new LibraryGameView();
					view.setGameId(UUID.fromString(rs.getString("game_id")));
					view.setTitle(rs.getString("title"));
					view.setSlug(rs.getString("slug"));
					view.setCoverImageUrl(rs.getString("cover_image_url"));
					view.setLicenseKey(rs.getString("key_value"));
					view.setPurchasedAt(rs.getObject("purchased_at", LocalDateTime.class));
					view.setOrderNumber(rs.getString("order_number"));
					return view;
				})
				.list();
	}

	public BigDecimal sumRevenueToday() {
		BigDecimal sum = jdbcClient.sql("""
				SELECT COALESCE(SUM(total_amount), 0)
				FROM orders
				WHERE status IN ('COMPLETED', 'PAID')
				  AND created_at >= CURRENT_DATE
				""")
				.query(BigDecimal.class)
				.single();
		return sum != null ? sum : BigDecimal.ZERO;
	}

	public long countOrdersToday() {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*)
				FROM orders
				WHERE created_at >= CURRENT_DATE
				""")
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public long countByStatus(OrderStatus status) {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*) FROM orders WHERE status = :status
				""")
				.param("status", status.name())
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public long countPendingOlderThanHours(int hours) {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*)
				FROM orders
				WHERE status = 'PENDING'
				  AND created_at < CURRENT_TIMESTAMP - (:hours || ' hours')::INTERVAL
				""")
				.param("hours", hours)
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	public List<AdminOrderView> findRecentForAdmin(int limit) {
		return findAllForAdmin(null, limit, 0);
	}

	public List<AdminOrderView> findAllForAdmin(OrderStatus statusFilter, int limit, int offset) {
		var spec = new StringBuilder("""
				SELECT o.id, o.order_number, o.status, o.total_amount, o.created_at,
				       u.first_name, u.last_name, u.email,
				       COALESCE(SUM(oi.quantity), 0) AS item_count
				FROM orders o
				INNER JOIN users u ON u.id = o.user_id
				LEFT JOIN order_items oi ON oi.order_id = o.id
				WHERE 1=1
				""");
		if (statusFilter != null) {
			spec.append(" AND o.status = :status");
		}
		spec.append("""
				 GROUP BY o.id, o.order_number, o.status, o.total_amount, o.created_at,
				          u.first_name, u.last_name, u.email
				 ORDER BY o.created_at DESC
				 LIMIT :limit OFFSET :offset
				""");
		var query = jdbcClient.sql(spec.toString())
				.param("limit", limit)
				.param("offset", offset);
		if (statusFilter != null) {
			query = query.param("status", statusFilter.name());
		}
		return query.query(this::mapAdminOrderRow).list();
	}

	public List<DailySalesView> findDailySalesLastDays(java.time.LocalDate startDate) {
		return jdbcClient.sql("""
				SELECT CAST(created_at AS DATE) AS sale_date,
				       COALESCE(SUM(total_amount), 0) AS revenue,
				       COUNT(*) AS order_count
				FROM orders
				WHERE status IN ('COMPLETED', 'PAID')
				  AND created_at >= :startDate
				GROUP BY CAST(created_at AS DATE)
				ORDER BY sale_date ASC
				""")
				.param("startDate", startDate.atStartOfDay())
				.query((rs, rowNum) -> {
					DailySalesView view = new DailySalesView();
					view.setDate(rs.getObject("sale_date", LocalDate.class));
					view.setRevenue(rs.getBigDecimal("revenue"));
					view.setOrderCount(rs.getLong("order_count"));
					return view;
				})
				.list();
	}

	public List<AdminOrderView> findAllForExport() {
		return findAllForAdmin(null, 10_000, 0);
	}

	public long countByUserAndGame(UUID userId, UUID gameId) {
		Long count = jdbcClient.sql("""
				SELECT COUNT(*)
				FROM order_items oi
				INNER JOIN orders o ON o.id = oi.order_id
				WHERE o.user_id = :userId AND oi.game_id = :gameId AND o.status = 'COMPLETED'
				""")
				.param("userId", userId)
				.param("gameId", gameId)
				.query(Long.class)
				.single();
		return count != null ? count : 0L;
	}

	private AdminOrderView mapAdminOrderRow(ResultSet rs, int rowNum) throws SQLException {
		AdminOrderView view = new AdminOrderView();
		view.setId(UUID.fromString(rs.getString("id")));
		view.setOrderNumber(rs.getString("order_number"));
		view.setStatus(OrderStatus.fromString(rs.getString("status")));
		view.setTotalAmount(rs.getBigDecimal("total_amount"));
		view.setItemCount(rs.getInt("item_count"));
		view.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		String firstName = rs.getString("first_name");
		String lastName = rs.getString("last_name");
		view.setCustomerName((firstName + " " + lastName).trim());
		view.setCustomerEmail(rs.getString("email"));
		return view;
	}

	private Order mapOrderRow(ResultSet rs, int rowNum) throws SQLException {
		Order order = new Order();
		order.setId(UUID.fromString(rs.getString("id")));
		order.setOrderNumber(rs.getString("order_number"));
		order.setUserId(UUID.fromString(rs.getString("user_id")));
		order.setStatus(OrderStatus.fromString(rs.getString("status")));
		order.setSubtotal(rs.getBigDecimal("subtotal"));
		order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
		order.setTotalAmount(rs.getBigDecimal("total_amount"));
		order.setPaymentMethod(rs.getString("payment_method"));
		order.setStripePaymentIntentId(rs.getString("stripe_payment_intent_id"));
		String promoId = rs.getString("promo_code_id");
		if (promoId != null) {
			order.setPromoCodeId(UUID.fromString(promoId));
		}
		order.setBillingFirstName(rs.getString("billing_first_name"));
		order.setBillingLastName(rs.getString("billing_last_name"));
		order.setBillingEmail(rs.getString("billing_email"));
		order.setBillingPhone(rs.getString("billing_phone"));
		order.setBillingAddress(rs.getString("billing_address"));
		order.setBillingPostalCode(rs.getString("billing_postal_code"));
		order.setBillingCity(rs.getString("billing_city"));
		order.setBillingCountry(rs.getString("billing_country"));
		order.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
		order.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
		return order;
	}

	private OrderItem mapOrderItemRow(ResultSet rs, int rowNum) throws SQLException {
		OrderItem item = new OrderItem();
		item.setId(UUID.fromString(rs.getString("id")));
		item.setOrderId(UUID.fromString(rs.getString("order_id")));
		item.setGameId(UUID.fromString(rs.getString("game_id")));
		String licenseKeyId = rs.getString("license_key_id");
		if (licenseKeyId != null) {
			item.setLicenseKeyId(UUID.fromString(licenseKeyId));
		}
		item.setUnitPrice(rs.getBigDecimal("unit_price"));
		item.setQuantity(rs.getInt("quantity"));

		Game game = new Game();
		game.setId(item.getGameId());
		game.setTitle(rs.getString("title"));
		game.setSlug(rs.getString("slug"));
		game.setCoverImageUrl(rs.getString("cover_image_url"));
		String platform = rs.getString("platform");
		if (platform != null) {
			game.setPlatform(Platform.valueOf(platform));
		}
		item.setGame(game);

		if (licenseKeyId != null) {
			LicenseKey key = new LicenseKey();
			key.setId(UUID.fromString(licenseKeyId));
			key.setKeyValue(rs.getString("key_value"));
			key.setStatus(LicenseKeyStatus.fromString(rs.getString("key_status")));
			item.setLicenseKey(key);
		}
		return item;
	}
}
