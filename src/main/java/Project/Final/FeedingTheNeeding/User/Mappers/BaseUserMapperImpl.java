//package Project.Final.FeedingTheNeeding.User.Mappers;
//
//import Project.Final.FeedingTheNeeding.User.DTO.BaseUserDTO;
//import Project.Final.FeedingTheNeeding.User.Model.BaseUser;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BaseUserMapperImpl implements BaseUserMapper {
//
//    @Override
//    public BaseUser fromDTO(BaseUserDTO baseUserDTO) {
//        return new BaseUser(baseUserDTO.ID(),
//                baseUserDTO.firstName(),
//                baseUserDTO.lastName(),
//                baseUserDTO.phoneNumber(),
//                baseUserDTO.address(),
//                baseUserDTO.city());
//    }
//
//    @Override
//    public BaseUserDTO toDTO(BaseUser baseUser) {
//        return new BaseUserDTO(baseUser.getId(),
//                baseUser.getFirstName(),
//                baseUser.getLastName(),
//                baseUser.getPhoneNumber(),
//                baseUser.getAddress(),
//                baseUser.getCity());
//    }
//}