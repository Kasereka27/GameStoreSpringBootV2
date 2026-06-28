package com.examen.gamestore.web.controller.view;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.examen.gamestore.repository.OrderRepository;
import com.examen.gamestore.web.dto.AdminOrderView;

@Controller
public class AdminReportController {

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final OrderRepository orderRepository;

	public AdminReportController(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@GetMapping("/admin/reports")
	public String reports(Model model) {
		model.addAttribute("orderCount", orderRepository.findAllForExport().size());
		return "admin/reports";
	}

	@GetMapping("/admin/reports/orders.csv")
	public ResponseEntity<byte[]> exportOrdersCsv() throws IOException {
		List<AdminOrderView> orders = orderRepository.findAllForExport();
		StringBuilder csv = new StringBuilder();
		csv.append("reference,client,email,date,total,statut,articles\n");
		for (AdminOrderView order : orders) {
			csv.append(escape(order.getOrderNumber())).append(',');
			csv.append(escape(order.getCustomerName())).append(',');
			csv.append(escape(order.getCustomerEmail())).append(',');
			csv.append(order.getCreatedAt() != null ? order.getCreatedAt().format(DATE_FMT) : "").append(',');
			csv.append(order.getTotalAmount()).append(',');
			csv.append(order.getStatus()).append(',');
			csv.append(order.getItemCount()).append('\n');
		}
		byte[] body = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=commandes.csv")
				.contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
				.body(body);
	}

	private String escape(String value) {
		if (value == null) {
			return "";
		}
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return value;
	}
}
