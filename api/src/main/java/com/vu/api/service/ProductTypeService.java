package com.vu.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductTypeRequest;
import com.vu.api.DTO.response.ProductTypeResponse;

/**
 * Lớp ProductTypeService là một lớp dịch vụ trong ứng dụng, được đánh dấu với @Service để Spring Boot có thể quản lý nó như một bean.
 * Lớp này sẽ chứa các phương thức liên quan đến quản lý loại sản phẩm (
 */
@Service
public interface ProductTypeService {
    // Phương thức để lấy tất cả các loại sản phẩm, trả về một danh sách các đối tượng ProductTypeResponse chứa thông
    // tin về từng loại sản phẩm.
    public List<ProductTypeResponse> getAllProductType();

    // Phương thức để lấy tất cả các loại sản phẩm đang hoạt động (status = true), trả về một danh sách các đối tượng
    // ProductTypeResponse.
    public List<ProductTypeResponse> getAllProductTypeActive();

    // Phương thức để tạo một loại sản phẩm mới, nhận vào một đối tượng ProductTypeRequest chứa thông tin về loại sản
    // phẩm cần tạo, và trả về một đối tượng ProductTypeResponse chứa thông tin về loại sản phẩm vừa được tạo.
    public ProductTypeResponse createProductType(ProductTypeRequest request);

    // Phương thức để cập nhật thông tin của một loại sản phẩm đã tồn tại, nhận vào mã định danh của loại sản phẩm cần
    // cập nhật và một đối tượng ProductTypeRequest chứa thông tin mới, và trả về một đối tượng ProductTypeResponse chứa
    // thông tin về loại sản phẩm sau khi đã được cập nhật.
    public ProductTypeResponse updateProductType(String productTypeId, ProductTypeRequest request);

    // Phương thức để xóa một loại sản phẩm, nhận vào mã định danh của loại sản phẩm cần xóa. Phương thức này không trả
    // về giá trị nào, nhưng sẽ thực hiện hành động xóa loại sản phẩm khỏi cơ sở dữ liệu.
    // Lưu ý: Thay vì xóa hoàn toàn loại sản phẩm khỏi cơ sở dữ liệu, có thể cân nhắc việc chỉ cập nhật trường status
    // thành false để đánh dấu loại sản phẩm là không hoạt động, nhằm giữ lại lịch sử và tránh mất dữ liệu quan trọng.
    public void deleteProductType(String productTypeId);
}
