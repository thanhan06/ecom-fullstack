package com.vu.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.DTO.request.ProductTypeRequest;
import com.vu.api.DTO.response.ProductTypeResponse;
import com.vu.api.ResponseConfig.ApiResponse;
import com.vu.api.ResponseConfig.ApiResponses;
import com.vu.api.service.ProductTypeService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Class ProductTypeController để xử lý các endpoint liên quan đến quản lý loại sản phẩm, bao gồm lấy danh sách loại sản phẩm, tạo mới, cập nhật và xóa loại sản phẩm
 */
@RestController
@RequestMapping("/product-types")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeController {
    // Tiêm ProductTypeService để xử lý logic liên quan đến loại sản phẩm
    @Autowired
    ProductTypeService productTypeService;

    /**
     * Endpoint GET /product-types/active để lấy danh sách tất cả loại sản phẩm đang hoạt động (status = true)
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng
     * cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là danh sách ProductTypeResponse nếu
     * thành công, hoặc lỗi nếu thất bại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAllProductType(HttpServletRequest req) {
        // Gọi service để lấy danh sách tất cả loại sản phẩm
        List<ProductTypeResponse> productTypes = productTypeService.getAllProductType();
        return ApiResponses.ok(req, productTypes);
    }

    /**
     * Endpoint GET /product-types/active để lấy danh sách tất cả loại sản phẩm đang hoạt động (status = true)
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng
     * cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là danh sách ProductTypeResponse nếu
     * thành công, hoặc lỗi nếu thất bại
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAllProductTypeActive(HttpServletRequest req) {
        // Gọi service để lấy danh sách tất cả loại sản phẩm đang hoạt động
        List<ProductTypeResponse> productTypes = productTypeService.getAllProductTypeActive();
        return ApiResponses.ok(req, productTypes);
    }

    /**
     * Endpoint POST /product-types/create để tạo mới một loại sản phẩm, chỉ cho phép người dùng có quyền ADMIN thực hiện
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @param request đối tượng ProductTypeRequest chứa thông tin cần thiết để tạo mới loại sản phẩm (tên loại sản phẩm)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là ProductTypeResponse nếu tạo mới thành công, hoặc lỗi nếu thất bại
     * @throws AccessDeniedException nếu người dùng không có quyền ADMIN
     * @PreAuthorize("hasAuthority('ADMIN')") để chỉ cho phép người dùng có quyền ADMIN thực hiện endpoint này, nếu không sẽ trả về lỗi Access Denied
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> createProductType(
            HttpServletRequest req, @RequestBody @Valid ProductTypeRequest request) {
        return ApiResponses.created(req, productTypeService.createProductType(request));
    }

    /**
     * Endpoint PUT /product-types/update/{productTypeId} để cập nhật thông tin của một loại sản phẩm, chỉ cho phép người dùng có quyền ADMIN thực hiện
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @param productTypeId ID của loại sản phẩm cần cập nhật (lấy từ đường dẫn)
     * @param request đối tượng ProductTypeRequest chứa thông tin cần thiết để cập nhật loại sản phẩm (tên loại sản phẩm, trạng thái hoạt động)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là ProductTypeResponse nếu cập nhật thành công, hoặc lỗi nếu thất bại
     * @throws AccessDeniedException nếu người dùng không có quyền ADMIN
     * @PreAuthorize("hasAuthority('ADMIN')") để chỉ cho phép người dùng có quyền ADMIN thực hiện endpoint này, nếu không sẽ trả về lỗi Access Denied
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{productTypeId}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> updateProductType(
            HttpServletRequest req,
            @PathVariable String productTypeId,
            @RequestBody @Valid ProductTypeRequest request) {
        return ApiResponses.ok(req, productTypeService.updateProductType(productTypeId, request));
    }

    /**
     * Endpoint DELETE /product-types/{productTypeId} để xóa một loại sản phẩm, chỉ cho phép người dùng có quyền ADMIN thực hiện
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @param productTypeId ID của loại sản phẩm cần xóa (lấy từ đường dẫn)
     * @return ResponseEntity chứa ApiResponse với dữ liệu null nếu xóa thành công, hoặc lỗi nếu thất bại
     * @throws AccessDeniedException nếu người dùng không có quyền ADMIN
     * @PreAuthorize("hasAuthority('ADMIN')") để chỉ cho phép người dùng có quyền ADMIN thực hiện endpoint này, nếu không sẽ trả về lỗi Access Denied
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{productTypeId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductType(
            HttpServletRequest req, @PathVariable String productTypeId) {
        // Gọi service để xóa loại sản phẩm
        productTypeService.deleteProductType(productTypeId);
        return ApiResponses.ok(req, null);
    }
}
