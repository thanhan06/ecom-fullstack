package com.vu.api.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.ProductTypeRequest;
import com.vu.api.DTO.response.ProductTypeResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.ProductTypeEntity;
import com.vu.api.mapper.ProductTypeMapper;
import com.vu.api.repository.ProductTypeRepository;
import com.vu.api.service.ProductTypeService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Class ProductTypeServiceImpl để triển khai các phương thức trong ProductTypeService,
 * bao gồm các chức năng để lấy danh sách loại sản phẩm, tạo mới, cập nhật
 * và xóa loại sản phẩm. Sử dụng ProductTypeRepository để tương tác với cơ sở dữ liệu
 * và ProductTypeMapper để chuyển đổi giữa ProductTypeEntity và các DTO liên quan đến loại
 * sản phẩm. Các phương thức trong class này cũng xử lý các lỗi liên quan đến việc tồn tại tên loại sản phẩm và không tìm thấy loại sản phẩm khi cập nhật hoặc xóa.
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductTypeServiceImpl implements ProductTypeService {
    // Inject ProductTypeRepository để tương tác với cơ sở dữ liệu và ProductTypeMapper để chuyển đổi giữa
    // ProductTypeEntity và các DTO liên quan đến loại sản phẩm
    @Autowired
    ProductTypeRepository productTypeRepository;

    // Inject ProductTypeMapper để chuyển đổi giữa ProductTypeEntity và các DTO liên quan đến loại sản phẩm
    @Autowired
    ProductTypeMapper productTypeMapper;

    /**
     * Phương thức getAllProductType để lấy danh sách tất cả các loại sản phẩm, bao gồm cả loại sản phẩm đang hoạt động và không hoạt động, sau đó chuyển đổi chúng thành ProductTypeResponse để trả về cho client.
     * @return List<ProductTypeResponse> - Danh sách các loại sản phẩm dưới dạng ProductTypeResponse.
     */
    @Override
    public List<ProductTypeResponse> getAllProductType() {
        // Lấy tất cả các loại sản phẩm từ cơ sở dữ liệu và chuyển đổi chúng thành ProductTypeResponse để trả về cho
        // client
        List<ProductTypeEntity> productTypeEntities = productTypeRepository.findAll();

        return productTypeEntities.stream()
                .map(entity -> productTypeMapper.toProductTypeResponse(entity))
                .toList();
    }

    /**
     * Phương thức getAllProductTypeActive để lấy danh sách tất cả các loại sản phẩm đang hoạt động, sau đó chuyển đổi chúng thành ProductTypeResponse để trả về cho client.
     * @return List<ProductTypeResponse> - Danh sách các loại sản phẩm đang hoạt động dưới dạng ProductTypeResponse.
     */
    @Override
    public List<ProductTypeResponse> getAllProductTypeActive() {
        // Lấy tất cả các loại sản phẩm đang hoạt động từ cơ sở dữ liệu và chuyển đổi chúng thành ProductTypeResponse để
        // trả về cho
        List<ProductTypeEntity> productTypeEntities = productTypeRepository.findByStatusTrue();

        return productTypeEntities.stream()
                .map(entity -> productTypeMapper.toProductTypeResponse(entity))
                .toList();
    }

    /**
     * Phương thức createProductType để tạo mới một loại sản phẩm dựa trên dữ liệu nhận được từ ProductTypeRequest. Phương thức này kiểm tra xem tên loại sản phẩm đã tồn tại hay chưa, nếu đã tồn tại sẽ ném ra ApiException với mã lỗi tương ứng. Nếu tên loại sản phẩm hợp lệ, phương thức sẽ tạo một ProductTypeEntity mới, gán các giá trị cần thiết và lưu vào cơ sở dữ liệu, sau đó chuyển đổi entity đã lưu thành ProductTypeResponse để trả về cho client.
     * @param request - Dữ liệu nhận được từ client để tạo mới loại sản phẩm, bao gồm tên loại sản phẩm.
     * @return ProductTypeResponse - Thông tin về loại sản phẩm vừa được tạo dưới dạng ProductTypeResponse.
     * @throws ApiException - Nếu tên loại sản phẩm đã tồn tại hoặc có lỗi liên quan đến việc tạo mới loại sản phẩm.
     */
    @Override
    public ProductTypeResponse createProductType(ProductTypeRequest request) {
        // Kiểm tra xem tên loại sản phẩm đã tồn tại hay chưa, nếu đã tồn tại sẽ ném ra ApiException với mã lỗi tương
        // ứng
        if (productTypeRepository.existsByProductTypeName(request.productTypeName())) {
            throw new ApiException(ErrorCode.PRODUCT_TYPE_NAME_IS_EXIST);
        }

        // Lấy tên người dùng từ SecurityContext để gán vào trường createUser của ProductTypeEntity
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // Tạo mã định danh mới cho loại sản phẩm bằng cách lấy mã định danh lớn nhất hiện tại và tăng lên 1, sau đó
        // định dạng lại thành chuỗi có 3 chữ số
        String maxProductTypeId = productTypeRepository.findMaxProductTypeId();

        // Nếu chưa có loại sản phẩm nào, bắt đầu từ 001
        int newProductTypeId = 1; // Mặc định nếu chưa có loại sản phẩm nào

        // Nếu đã có loại sản phẩm, tăng mã định danh lớn nhất lên 1
        if (maxProductTypeId != null && !maxProductTypeId.isEmpty()) {
            newProductTypeId = Integer.parseInt(maxProductTypeId) + 1;
        }
        // Định dạng mã định danh mới thành chuỗi có 3 chữ số, ví dụ: 001, 002, ..., 010, 011, ...
        String newProductTypeIdStr = String.format("%03d", newProductTypeId);

        // Tạo một ProductTypeEntity mới, gán các giá trị cần thiết và lưu vào cơ sở dữ liệu, sau đó chuyển đổi entity
        // đã lưu thành ProductTypeResponse để trả về cho client
        ProductTypeEntity productTypeEntity = new ProductTypeEntity();

        productTypeEntity.setProductTypeId(newProductTypeIdStr);
        productTypeEntity.setProductTypeName(request.productTypeName());
        productTypeEntity.setCreateUser(userId);

        ProductTypeEntity savedEntity = productTypeRepository.save(productTypeEntity);
        return productTypeMapper.toProductTypeResponse(savedEntity);
    }

    /**
     * Phương thức updateProductType để cập nhật thông tin của một loại sản phẩm dựa trên mã định danh và dữ liệu nhận được từ ProductTypeRequest. Phương thức này kiểm tra xem loại sản phẩm có tồn tại hay không, nếu không tồn tại sẽ ném ra ApiException với mã lỗi tương ứng. Nếu loại sản phẩm tồn tại, phương thức sẽ kiểm tra xem tên loại sản phẩm mới có trùng với tên của loại sản phẩm khác hay không (ngoại trừ chính loại sản phẩm đang được cập nhật), nếu có trùng sẽ ném ra ApiException với mã lỗi tương ứng. Nếu tất cả các kiểm tra đều hợp lệ, phương thức sẽ cập nhật tên loại sản phẩm và trường updatedUser, sau đó lưu lại vào cơ sở dữ liệu và chuyển đổi entity đã cập nhật thành ProductTypeResponse để trả về cho client.
     * @param productTypeId - Mã định danh của loại sản phẩm cần cập nhật.
     * @param request - Dữ liệu nhận được từ client để cập nhật loại sản phẩm
     * bao gồm tên loại sản phẩm mới.
     * @return ProductTypeResponse - Thông tin về loại sản phẩm vừa được cập nhật dưới dạng ProductTypeResponse.
     * @throws ApiException - Nếu loại sản phẩm không tồn tại, tên loại sản phẩm mới trùng với tên của loại sản phẩm khác hoặc có lỗi liên quan đến việc cập nhật loại sản phẩm.
     */
    @Override
    public ProductTypeResponse updateProductType(String productTypeId, ProductTypeRequest request) {
        // Kiểm tra xem loại sản phẩm có tồn tại hay không, nếu không tồn tại sẽ ném ra ApiException với mã lỗi tương
        // ứng
        ProductTypeEntity productTypeEntity = productTypeRepository
                .findById(productTypeId)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        // Kiểm tra xem tên loại sản phẩm mới có trùng với tên của loại sản phẩm khác hay không (ngoại trừ chính loại
        // sản phẩm đang được cập nhật), nếu có trùng sẽ ném ra ApiException với mã lỗi tương ứng
        if (productTypeRepository.existsByProductTypeName(request.productTypeName())
                && !productTypeEntity.getProductTypeName().equals(request.productTypeName())) {
            throw new ApiException(ErrorCode.PRODUCT_TYPE_NAME_IS_EXIST);
        }

        // Cập nhật tên loại sản phẩm và trường updatedUser, sau đó lưu lại vào cơ sở dữ liệu và chuyển đổi entity đã
        // cập nhật thành
        productTypeEntity.setProductTypeName(request.productTypeName());
        productTypeEntity.setUpdatedUser(
                SecurityContextHolder.getContext().getAuthentication().getName());

        ProductTypeEntity updatedEntity = productTypeRepository.save(productTypeEntity);
        return productTypeMapper.toProductTypeResponse(updatedEntity);
    }

    /**
     * Phương thức deleteProductType để xóa một loại sản phẩm dựa trên mã định danh. Phương thức này kiểm tra xem loại sản phẩm có tồn tại hay không, nếu không tồn tại sẽ ném ra ApiException với mã lỗi tương ứng. Nếu loại sản phẩm tồn tại, phương thức sẽ cập nhật trường status thành false (đánh dấu là không hoạt động) và trường updatedUser, sau đó lưu lại vào cơ sở dữ liệu. Phương thức này không trả về dữ liệu nào cho client.
     * @param productTypeId - Mã định danh của loại sản phẩm cần xóa
     * @throws ApiException - Nếu loại sản phẩm không tồn tại hoặc có lỗi liên quan đến việc xóa loại sản phẩm.
     * Lưu ý: Phương thức này không thực sự xóa bản ghi khỏi cơ sở dữ liệu mà chỉ đánh dấu nó là không hoạt động bằng cách cập nhật trường status thành false, điều này cho phép giữ lại lịch sử và dữ liệu liên quan đến loại sản phẩm trong cơ sở dữ liệu mà không làm mất dữ liệu quan trọng.
     */
    @Override
    public void deleteProductType(String productTypeId) {
        // Kiểm tra xem loại sản phẩm có tồn tại hay không, nếu không tồn tại sẽ ném ra ApiException với mã lỗi tương
        // ứng
        ProductTypeEntity productTypeEntity = productTypeRepository
                .findById(productTypeId)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        // Cập nhật trường status thành false (đánh dấu là không hoạt động) và trường updatedUser, sau đó lưu lại vào cơ
        // sở dữ liệu
        productTypeEntity.setStatus(false);
        productTypeEntity.setUpdatedUser(
                SecurityContextHolder.getContext().getAuthentication().getName());
        productTypeRepository.save(productTypeEntity);
    }
}
