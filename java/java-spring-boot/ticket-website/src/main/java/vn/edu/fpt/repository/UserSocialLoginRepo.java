package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.UserSocialLogin;

import java.util.Optional;

@Repository
public interface UserSocialLoginRepo extends JpaRepository<UserSocialLogin, Integer> {

    // Tìm theo provider và providerKey (dùng cho OAuth2 login)
    Optional<UserSocialLogin> findByProviderAndProviderKey(String provider, String providerKey);

    // Tìm theo customer ID (dùng customer.id vì trong Customer entity dùng field "id")
    Optional<UserSocialLogin> findByCustomer_Id(Integer customerId);

    // Tìm theo customer object
    Optional<UserSocialLogin> findByCustomer(Customer customer);

    // Kiểm tra xem customer đã có social login với provider nào chưa
    boolean existsByCustomerAndProvider(Customer customer, String provider);

    // Xóa theo customer (khi xóa customer)
    void deleteByCustomer(Customer customer);
}