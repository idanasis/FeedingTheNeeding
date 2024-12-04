package Project.Final.FeedingTheNeeding.User.Repository;

import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
import Project.Final.FeedingTheNeeding.User.Model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<BaseUser, Long>, IUserRepository {

  //  @Override
   // Optional<BaseUser> findByEmail(String email);

  //  @Override
  //  boolean existsByEmail(String email);

//    @Override
//    List<BaseUser> findByStatus(UserStatus status);

}